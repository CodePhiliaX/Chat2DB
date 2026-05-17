import React, { memo, useState, useRef, useEffect, useCallback } from 'react';
import { Button, Input, Spin, message, Tag, Alert } from 'antd';
import { DownOutlined, RightOutlined } from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { v4 as uuidv4 } from 'uuid';
import { formatParams } from '@/utils/url';
import connectToEventSource, { cancelChatSession } from '@/utils/eventSource';
import CascaderDB from '@/components/CascaderDB';
import { IAiChatPromptType, ITableCommentResult, IBatchTableCommentResult, IFieldMappingResult, IDataExpressionResult } from '@/pages/main/workspace/store/common';
import { useWorkspaceStore } from '@/pages/main/workspace/store';
import { useAiChatStore, ChatStateType, IChatMessage } from '@/pages/main/workspace/store/aiChatStore';
import styles from './index.less';

const STATE_LABELS: Record<ChatStateType, { text: string; color: string }> = {
  IDLE: { text: '等待中', color: 'default' },
  AUTO_SELECTING_TABLES: { text: '选择表...', color: 'processing' },
  FETCHING_TABLE_SCHEMA: { text: '获取表结构...', color: 'processing' },
  BUILDING_PROMPT: { text: '构建提示...', color: 'processing' },
  STREAMING: { text: 'AI生成中', color: 'processing' },
  COMPLETED: { text: '完成', color: 'success' },
  FAILED: { text: '失败', color: 'error' },
};

const isActiveState = (state?: ChatStateType): boolean => {
  return state
    ? ['AUTO_SELECTING_TABLES', 'FETCHING_TABLE_SCHEMA', 'BUILDING_PROMPT', 'STREAMING'].includes(state)
    : false;
};

const ThinkingBlock = memo<{ thinking: string; collapsed?: boolean }>(({ thinking, collapsed = true }) => {
  const [isCollapsed, setIsCollapsed] = useState(collapsed);

  return (
    <div className={styles.thinkingBlock}>
      <div className={styles.thinkingHeader} onClick={() => setIsCollapsed(!isCollapsed)}>
        {isCollapsed ? <RightOutlined /> : <DownOutlined />}
        <span>思考过程</span>
      </div>
      {!isCollapsed && (
        <div className={styles.thinkingContent}>
          <ReactMarkdown remarkPlugins={[remarkGfm]}>{thinking}</ReactMarkdown>
        </div>
      )}
    </div>
  );
});

function extractJsonFromContent(content: string): ITableCommentResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"table_comment"[\s\S]*"column_comments"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as ITableCommentResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.table_comment) {
      return directJson as ITableCommentResult;
    }
  } catch (e) {
    console.error('[extractJsonFromContent] Parse error:', e);
  }
  return null;
}

function extractBatchJsonFromContent(content: string): IBatchTableCommentResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"tables"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as IBatchTableCommentResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.tables) {
      return directJson as IBatchTableCommentResult;
    }
  } catch (e) {
    console.error('[extractBatchJsonFromContent] Parse error:', e);
  }
  return null;
}

function extractFieldMappingFromContent(content: string): IFieldMappingResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"mappings"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as IFieldMappingResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.mappings) {
      return directJson as IFieldMappingResult;
    }
  } catch (e) {
    console.error('[extractFieldMappingFromContent] Parse error:', e);
  }
  return null;
}

function extractDataExpressionFromContent(content: string): IDataExpressionResult | null {
  try {
    const jsonMatch = content.match(/\{[\s\S]*"column_expressions"[\s\S]*\}/);
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0]) as IDataExpressionResult;
    }
    const directJson = JSON.parse(content);
    if (directJson.column_expressions) {
      return directJson as IDataExpressionResult;
    }
  } catch (e) {
    console.error('[extractDataExpressionFromContent] Parse error:', e);
  }
  return null;
}

interface IProps {
  className?: string;
  data?: any;
}

export default memo<IProps>(() => {
  const [inputValue, setInputValue] = useState('');
  const closeEventSource = useRef<() => void>();
  const sessionIdRef = useRef<string>();
  const commentCallbackRef = useRef<(result: ITableCommentResult) => void>();
  const batchCommentCallbackRef = useRef<(result: IBatchTableCommentResult) => void>();
  const mappingCallbackRef = useRef<(result: IFieldMappingResult) => void>();
  const expressionCallbackRef = useRef<(result: IDataExpressionResult) => void>();

  const {
    currentSessionId,
    sessions,
    createSession,
    updateState,
    appendContent,
    addMessage,
    setSelectedTables,
    setSchemaInfo,
    setError,
    setLastRequest,
    clearSession,
    resetCurrentContent,
    lastRequest,
  } = useAiChatStore();

  const currentSession = currentSessionId ? sessions.get(currentSessionId) : null;

  const { consoleList, activeConsoleId, currentConnectionDetails, pendingAiChat } = useWorkspaceStore((state) => ({
    consoleList: state.consoleList,
    activeConsoleId: state.activeConsoleId,
    currentConnectionDetails: state.currentConnectionDetails,
    pendingAiChat: state.pendingAiChat,
  }));

  const activeConsole = consoleList?.find((c) => c.id === activeConsoleId);

  const [boundInfo, setBoundInfo] = useState(() => ({
    dataSourceId: activeConsole?.dataSourceId || currentConnectionDetails?.id,
    databaseName: activeConsole?.databaseName || '',
    schemaName: activeConsole?.schemaName || '',
    tableNames: pendingAiChat?.tableNames || null,
  }));

  useEffect(() => {
    const activeConsoleInfo = consoleList?.find((c) => c.id === activeConsoleId);
    if (activeConsoleInfo) {
      setBoundInfo((prev) => ({
        dataSourceId: activeConsoleInfo.dataSourceId || prev.dataSourceId,
        databaseName: activeConsoleInfo.databaseName || prev.databaseName,
        schemaName: activeConsoleInfo.schemaName || prev.schemaName,
        tableNames: prev.tableNames,
      }));
    }
  }, [activeConsoleId, consoleList]);

  useEffect(() => {
    if (pendingAiChat && pendingAiChat.message) {
      const overrideBoundInfo = {
        dataSourceId: pendingAiChat.dataSourceId || boundInfo.dataSourceId,
        databaseName: pendingAiChat.databaseName || boundInfo.databaseName,
        schemaName: pendingAiChat.schemaName || boundInfo.schemaName,
        tableNames: pendingAiChat.tableNames || boundInfo.tableNames || null,
      };
      if (pendingAiChat.dataSourceId && pendingAiChat.dataSourceId !== boundInfo.dataSourceId) {
        setBoundInfo((prev) => ({
          ...prev,
          dataSourceId: pendingAiChat.dataSourceId,
          tableNames: pendingAiChat.tableNames || prev.tableNames,
        }));
      }
      if (pendingAiChat.onCommentGenerated) {
        commentCallbackRef.current = pendingAiChat.onCommentGenerated;
      }
      if (pendingAiChat.onBatchCommentGenerated) {
        batchCommentCallbackRef.current = pendingAiChat.onBatchCommentGenerated;
      }
      if (pendingAiChat.onMappingGenerated) {
        mappingCallbackRef.current = pendingAiChat.onMappingGenerated;
      }
      if (pendingAiChat.onExpressionGenerated) {
        expressionCallbackRef.current = pendingAiChat.onExpressionGenerated;
      }
      sendAiChatInternal(pendingAiChat.message, pendingAiChat.promptType, overrideBoundInfo);
      useWorkspaceStore.setState({ pendingAiChat: null });
    }
  }, [pendingAiChat, boundInfo, sendAiChatInternal]);

  const sendAiChat = (messageText: string, promptType: IAiChatPromptType = 'NL_2_SQL', tableNames?: string[] | null) => {
    const infoWithTables = {
      ...boundInfo,
      tableNames: tableNames !== undefined ? tableNames : boundInfo.tableNames,
    };
    sendAiChatInternal(messageText, promptType, infoWithTables);
  };

  const sendAiChatInternal = useCallback(
    (messageText: string, promptType: IAiChatPromptType = 'NL_2_SQL', info: typeof boundInfo) => {
      console.log('[AiChat] sendAiChat called with:', { messageText, promptType, info });
      if (!messageText.trim()) {
        message.warning('请输入问题');
        return;
      }

      if (!info.dataSourceId) {
        message.warning('请先选择数据库连接');
        return;
      }

      const sessionId = uuidv4();
      console.log('[AiChat] Created sessionId:', sessionId);
      sessionIdRef.current = sessionId;
      createSession(sessionId);

      const userMessage: IChatMessage = {
        id: uuidv4(),
        role: 'user',
        content: messageText,
      };
      addMessage(sessionId, userMessage);

      setLastRequest({
        message: messageText,
        promptType,
        dataSourceId: info.dataSourceId,
        databaseName: info.databaseName,
        schemaName: info.schemaName,
        tableNames: info.tableNames,
      });

      resetCurrentContent(sessionId);

      const params = formatParams({
        message: messageText,
        promptType,
        dataSourceId: info.dataSourceId,
        databaseName: info.databaseName,
        schemaName: info.schemaName,
        tableNames: info.tableNames,
      });

      closeEventSource.current = connectToEventSource({
        url: `/api/ai/chat?${params}`,
        uid: sessionId,
        onOpen: () => {
          console.log('[AiChat] SSE connection opened');
          updateState(sessionId, 'IDLE');
        },
        onStateChange: (state, _msg) => {
          console.log('[AiChat] State changed:', state, _msg);
          updateState(sessionId, state);
        },
        onMessage: (content, thinking) => {
          console.log('[AiChat] Message received:', { content, thinking });
          appendContent(sessionId, content, thinking);
        },
        onTablesSelected: (tables) => {
          console.log('[AiChat] Tables selected:', tables);
          setSelectedTables(sessionId, tables);
        },
        onSchemaFetched: (ddl) => {
          console.log('[AiChat] Schema fetched, ddl length:', ddl?.length);
          setSchemaInfo(sessionId, ddl);
        },
        onDone: () => {
          console.log('[AiChat] onDone callback, sessionId:', sessionId);
          updateState(sessionId, 'COMPLETED');
          const currentSessions = useAiChatStore.getState().sessions;
          console.log('[AiChat] sessions in onDone:', currentSessions);
          const session = currentSessions.get(sessionId);
          console.log('[AiChat] session in onDone:', session);
          if (session?.currentContent) {
            addMessage(sessionId, {
              id: uuidv4(),
              role: 'assistant',
              content: session.currentContent,
              thinking: session.currentThinking || undefined,
            });

            if (promptType === 'NL_2_COMMENT' && commentCallbackRef.current) {
              try {
                const jsonContent = extractJsonFromContent(session.currentContent);
                if (jsonContent) {
                  console.log('[AiChat] Parsed comment result:', jsonContent);
                  commentCallbackRef.current(jsonContent);
                  message.success('AI 注释已生成，请查看并确认');
                }
              } catch (e) {
                console.error('[AiChat] Failed to parse comment JSON:', e);
                message.warning('无法解析 AI 生成的注释，请手动查看');
              }
              commentCallbackRef.current = undefined;
            }

            if (promptType === 'NL_2_COMMENT_BATCH' && batchCommentCallbackRef.current) {
              try {
                const jsonContent = extractBatchJsonFromContent(session.currentContent);
                if (jsonContent) {
                  console.log('[AiChat] Parsed batch comment result:', jsonContent);
                  batchCommentCallbackRef.current(jsonContent);
                  message.success('AI 批量注释已生成');
                }
              } catch (e) {
                console.error('[AiChat] Failed to parse batch comment JSON:', e);
                message.warning('无法解析 AI 生成的批量注释，请手动查看');
              }
              batchCommentCallbackRef.current = undefined;
            }

            if (promptType === 'NL_2_FIELD_MAPPING' && mappingCallbackRef.current) {
              try {
                const jsonContent = extractFieldMappingFromContent(session.currentContent);
                if (jsonContent) {
                  console.log('[AiChat] Parsed field mapping result:', jsonContent);
                  mappingCallbackRef.current(jsonContent);
                  message.success('AI 字段映射推荐已生成，请查看并确认');
                }
              } catch (e) {
                console.error('[AiChat] Failed to parse field mapping JSON:', e);
                message.warning('无法解析 AI 生成的映射推荐，请手动查看');
              }
              mappingCallbackRef.current = undefined;
            }

            if (promptType === 'NL_2_DATA_EXPRESSION' && expressionCallbackRef.current) {
              try {
                const jsonContent = extractDataExpressionFromContent(session.currentContent);
                if (jsonContent) {
                  console.log('[AiChat] Parsed data expression result:', jsonContent);
                  expressionCallbackRef.current(jsonContent);
                  message.success('AI 表达式推荐已生成，请查看并确认');
                }
              } catch (e) {
                console.error('[AiChat] Failed to parse data expression JSON:', e);
                message.warning('无法解析 AI 生成的表达式，请手动查看');
              }
              expressionCallbackRef.current = undefined;
            }
          }
          closeEventSource.current = undefined;
        },
        onError: (error) => {
          console.error('[AiChat] SSE error:', error);
          setError(sessionId, error);
          message.error(error);
          closeEventSource.current = undefined;
        },
      });
    },
    [
      createSession,
      addMessage,
      setLastRequest,
      resetCurrentContent,
      updateState,
      appendContent,
      setSelectedTables,
      setSchemaInfo,
      setError,
    ],
  );

  const handleCancel = async () => {
    if (closeEventSource.current) {
      closeEventSource.current();
      closeEventSource.current = undefined;
    }
    if (sessionIdRef.current) {
      await cancelChatSession(sessionIdRef.current);
      clearSession(sessionIdRef.current);
    }
  };

  const handleRetry = () => {
    if (lastRequest) {
      sendAiChat(lastRequest.message, lastRequest.promptType, lastRequest.tableNames);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendAiChat(inputValue);
    }
  };

  const handleBoundInfoChange = (value: { dataSourceId: number; databaseName: string; schemaName: string }) => {
    setBoundInfo({
      dataSourceId: value.dataSourceId,
      databaseName: value.databaseName,
      schemaName: value.schemaName,
    });
  };

  const stateInfo = currentSession ? STATE_LABELS[currentSession.state] : null;
  const isProcessing = isActiveState(currentSession?.state);
  const isInputDisabled = isProcessing || !boundInfo.dataSourceId;

  return (
    <div className={styles.aiChatContainer}>
      {stateInfo && (
        <div className={styles.statusBar}>
          <Tag color={stateInfo.color}>{stateInfo.text}</Tag>
          {currentSession?.selectedTables && currentSession.selectedTables.length > 0 && (
            <div className={styles.selectedTables}>
              <span>已选择表：</span>
              {currentSession.selectedTables.map((t) => (
                <Tag key={t} color="blue">
                  {t}
                </Tag>
              ))}
            </div>
          )}
          {isProcessing && (
            <Button size="small" danger onClick={handleCancel}>
              停止
            </Button>
          )}
        </div>
      )}

      <div className={styles.contentArea}>
        {currentSession?.messages.map((msg) => (
          <div key={msg.id} className={msg.role === 'user' ? styles.userBlock : styles.aiBlock}>
            {msg.thinking && <ThinkingBlock thinking={msg.thinking} />}
            <ReactMarkdown remarkPlugins={[remarkGfm]}>{msg.content}</ReactMarkdown>
          </div>
        ))}
        {currentSession?.state === 'STREAMING' && (currentSession.currentContent || currentSession.currentThinking) && (
          <div className={styles.aiBlock}>
            <Spin size="small">
              {currentSession.currentThinking && (
                <div className={styles.thinkingBlock}>
                  <div className={styles.thinkingHeader}>
                    <DownOutlined />
                    <span>思考过程...</span>
                  </div>
                  <div className={styles.thinkingContent}>
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{currentSession.currentThinking}</ReactMarkdown>
                  </div>
                </div>
              )}
              {currentSession.currentContent && (
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{currentSession.currentContent}</ReactMarkdown>
              )}
            </Spin>
          </div>
        )}
        {currentSession?.state === 'FAILED' && currentSession.error && (
          <div className={styles.errorBlock}>
            <Alert type="error" message={currentSession.error} />
            <Button type="primary" onClick={handleRetry} style={{ marginTop: 8 }}>
              重试
            </Button>
          </div>
        )}
      </div>

      <div className={styles.inputFormArea}>
        <CascaderDB
          onChange={handleBoundInfoChange}
          curConnectionId={boundInfo.dataSourceId}
          curDatabaseName={boundInfo.databaseName}
          curSchemaName={boundInfo.schemaName}
        />
        <Input.TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="请输入您的问题..."
          autoSize={{ minRows: 3, maxRows: 6 }}
          disabled={isProcessing}
        />
        <div className={styles.buttonGroup}>
          <Button
            type="primary"
            onClick={() => sendAiChat(inputValue)}
            disabled={!inputValue.trim() || isInputDisabled}
          >
            发送
          </Button>
        </div>
      </div>
    </div>
  );
});
