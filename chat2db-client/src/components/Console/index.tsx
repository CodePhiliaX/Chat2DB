import React, { useEffect, useMemo, useRef, useState, useImperativeHandle, ForwardedRef, forwardRef } from 'react';
import { connect } from 'umi';
import { formatParams } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import { Button, Spin, message, Drawer, Modal } from 'antd';
import ChatInput, { SyncModelType } from './ChatInput';
import Editor, { IEditorOptions, IExportRefFunction, IRangeType } from './MonacoEditor';
import historyServer from '@/service/history';
import aiServer from '@/service/ai';
import { v4 as uuidv4 } from 'uuid';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import Iconfont from '../Iconfont';
import { IAiConfig, ITreeNode } from '@/typings';
import { IAIState } from '@/models/ai';
import Popularize from '@/components/Popularize';
import { formatSql, getCookie } from '@/utils';
import { chatErrorForKey, chatErrorToLogin } from '@/constants/chat';
import { AiSqlSourceType } from '@/typings/ai';
import i18n from '@/i18n';
import configService from '@/service/config';
// import NewEditor from './NewMonacoEditor';
import styles from './index.less';
import indexedDB from '@/indexedDB';

enum IPromptType {
  NL_2_SQL = 'NL_2_SQL',
  SQL_EXPLAIN = 'SQL_EXPLAIN',
  SQL_OPTIMIZER = 'SQL_OPTIMIZER',
  SQL_2_SQL = 'SQL_2_SQL',
  ChatRobot = 'ChatRobot',
}

// enum IPromptTypeText {
//   NL_2_SQL = '自然语言转换',
//   SQL_EXPLAIN = '解释SQL',
//   SQL_OPTIMIZER = 'SQL优化',
//   SQL_2_SQL = 'SQL转换',
//   ChatRobot = 'Chat机器人',
// }

export type IAppendValue = {
  text: any;
  range?: IRangeType;
};

interface IProps {
  /** 调用来源 */
  source: 'workspace';
  /** 是否是活跃的console，用于快捷键 */
  isActive?: boolean;
  /** 添加或修改的内容 */
  appendValue?: IAppendValue;
  defaultValue?: string;
  /** 是否开启AI输入 */
  hasAiChat: boolean;
  /** 是否可以开启SQL转到自然语言的相关ai操作 */
  hasAi2Lang?: boolean;
  /** 是否有 */
  hasSaveBtn?: boolean;
  value?: string;
  executeParams: {
    databaseName?: string;
    dataSourceId?: number;
    type?: DatabaseTypeCode;
    consoleId?: number;
    schemaName?: string;
    consoleName?: string;
    status?: ConsoleStatus;
  };
  tableList?: ITreeNode[];
  editorOptions?: IEditorOptions;
  aiModel: IAIState;
  dispatch: any;
  remainingBtnLoading: boolean;
  // remainingUse: IAIState['remainingUse'];
  // onSQLContentChange: (v: string) => void;
  onExecuteSQL: (sql: string) => void;
  onConsoleSave: () => void;
  tables: any[];
}

export interface IConsoleRef {
  editorRef: IExportRefFunction | undefined;
}

function Console(props: IProps, ref: ForwardedRef<IConsoleRef>) {
  const {
    hasAiChat = true,
    executeParams,
    appendValue,
    isActive,
    hasSaveBtn = true,
    aiModel,
    dispatch,
    source,
    defaultValue,
  } = props;
  const uid = useMemo(() => uuidv4(), []);
  const chatResult = useRef('');
  const editorRef = useRef<IExportRefFunction>();
  const [selectedTables, setSelectedTables] = useState<string[]>([]);
  const [syncTableModel, setSyncTableModel] = useState<number>(0);
  const [isLoading, setIsLoading] = useState(false);
  const [aiContent, setAiContent] = useState('');
  const [isAiDrawerOpen, setIsAiDrawerOpen] = useState(false);
  const [isAiDrawerLoading, setIsAiDrawerLoading] = useState(false);
  const [popularizeModal, setPopularizeModal] = useState(false);
  const [modalProps, setModalProps] = useState({});
  const [isStream, setIsStream] = useState(false);
  const timerRef = useRef<any>();
  const aiFetchIntervalRef = useRef<any>();
  const closeEventSource = useRef<any>();
  // 上一次同步的console数据
  const lastSyncConsole = useRef<any>(defaultValue);

  /**
   * 当前选择的AI类型是Chat2DBAI
   */
  const isChat2DBAI = useMemo(
    () => aiModel.aiConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI,
    [aiModel.aiConfig?.aiSqlSource],
  );

  useEffect(() => {
    handleSelectTableSyncModel();
  }, [aiModel.hasWhite, localStorage.getItem('syncTableModel')]);

  useEffect(() => {
    if (appendValue) {
      editorRef?.current?.setValue(appendValue.text, appendValue.range);
    }
  }, [appendValue]);

  useImperativeHandle(ref, () => ({
    editorRef: editorRef?.current,
  }));

  useEffect(() => {
    if (source !== 'workspace') {
      return;
    }
    // 离开时保存
    if (!isActive && timerRef.current) {
      // 离开时清除定时器
      indexedDB.updateData('chat2db', 'workspaceConsoleDDL', {
        consoleId: executeParams.consoleId!,
        ddl: editorRef?.current?.getAllContent(),
        userId: getCookie('CHAT2DB.USER_ID'),
      });
      clearInterval(timerRef.current);
    } else {
      // 活跃时自动保存
      indexedDB
        .getDataByCursor('chat2db', 'workspaceConsoleDDL', {
          consoleId: executeParams.consoleId!,
          userId: getCookie('CHAT2DB.USER_ID'),
        })
        .then((res: any) => {
          const value = defaultValue || res?.[0]?.ddl || '';
          const oldValue = editorRef?.current?.getAllContent();
          if (value !== oldValue) {
            editorRef?.current?.setValue(value, 'reset');
          }
          setTimeout(() => {
            timingAutoSave();
          }, 0);
        });
    }
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [isActive]);

  function timingAutoSave(status?: ConsoleStatus) {
    if (timerRef.current) {
      clearInterval(timerRef.current);
    }
    timerRef.current = setInterval(() => {
      const ddl = editorRef?.current?.getAllContent();
      if (ddl === lastSyncConsole.current) {
        return;
      }
      lastSyncConsole.current = ddl;
      if (executeParams.status === ConsoleStatus.RELEASE || status === ConsoleStatus.RELEASE) {
        const p: any = {
          id: executeParams.consoleId,
          ddl,
        };
        historyServer.updateSavedConsole(p);
      } else {
        indexedDB.updateData('chat2db', 'workspaceConsoleDDL', {
          consoleId: executeParams.consoleId!,
          ddl,
          userId: getCookie('CHAT2DB.USER_ID'),
        });
      }
    }, 5000);
  }

  const tableListName = useMemo(() => {
    const tableList = (props.tables || []).map((t) => t.name);

    // 默认选中前八个
    setSelectedTables(tableList.slice(0, 8));

    return tableList;
  }, [props.tables]);

  const handleApiKeyEmptyOrGetQrCode = async (shouldPoll?: boolean) => {
    setIsLoading(true);
    try {
      const { wechatQrCodeUrl, token, tip } = await aiServer.getLoginQrCode({});
      setIsLoading(false);

      setPopularizeModal(true);
      setModalProps({
        imageUrl: wechatQrCodeUrl,
        token,
        tip,
      });
      if (shouldPoll) {
        let pollCnt = 0;
        aiFetchIntervalRef.current = setInterval(async () => {
          const { apiKey } = (await aiServer.getLoginStatus({ token })) || {};
          pollCnt++;
          if (apiKey || pollCnt >= 60) {
            clearInterval(aiFetchIntervalRef.current);
          }
          if (apiKey) {
            setPopularizeModal(false);

            await dispatch({
              type: 'ai/setAiConfig',
              payload: {
                ...(aiModel.aiConfig || {}),
                apiKey,
              },
            });
            await dispatch({
              type: 'ai/fetchRemainingUse',
              payload: {
                apiKey,
              },
            });
          }
        }, 3000);
      }
    } catch (e) {
      setIsLoading(false);
    }
  };

  const handleAIChatInEditor = async (content: string, promptType: IPromptType, ext?: string) => {
    const aiConfig = await configService.getAiSystemConfig({});
    handleAiChat(content, promptType, aiConfig, ext);
  };

  const handleAiChat = async (content: string, promptType: IPromptType, aiConfig?: IAiConfig, ext?: string) => {
    const { apiKey } = aiConfig || props.aiModel?.aiConfig || {};
    if (!apiKey && isChat2DBAI) {
      handleApiKeyEmptyOrGetQrCode(true);
      return;
    }

    const { dataSourceId, databaseName, schemaName } = executeParams;
    const isNL2SQL = promptType === IPromptType.NL_2_SQL;
    if (isNL2SQL) {
      setIsLoading(true);
    } else {
      setAiContent('');
      setIsAiDrawerOpen(true);
      setIsAiDrawerLoading(true);
    }
    const params = formatParams({
      message: content,
      promptType,
      dataSourceId,
      databaseName,
      schemaName,
      tableNames: syncTableModel ? selectedTables : null,
      ext,
    });

    const handleMessage = (message: string) => {
      setIsLoading(false);
      setIsAiDrawerLoading(false);
      try {
        const isEOF = message === '[DONE]';
        if (isEOF) {
          closeEventSource.current();
          setIsStream(false);
          if (isChat2DBAI) {
            dispatch({
              type: 'ai/fetchRemainingUse',
              payload: {
                apiKey,
              },
            });
          }
          if (isNL2SQL) {
            editorRef?.current?.setValue('\n');
          } else {
            setIsAiDrawerLoading(false);
            chatResult.current += '\n';
            setAiContent(chatResult.current);
            chatResult.current = '';
          }
          return;
        }

        let hasErrorToLogin = false;
        chatErrorToLogin.forEach((err) => {
          if (message.includes(err)) {
            hasErrorToLogin = true;
          }
        });
        let hasKeyLimitedOrExpired = false;
        chatErrorForKey.forEach((err) => {
          if (message.includes(err)) {
            hasKeyLimitedOrExpired = true;
          }
        });

        if (hasKeyLimitedOrExpired) {
          closeEventSource.current();
          setIsLoading(false);
          handlePopUp();
          return;
        }

        if (hasErrorToLogin) {
          closeEventSource.current();
          setIsLoading(false);
          hasErrorToLogin && handleApiKeyEmptyOrGetQrCode(true);
          // hasErrorToInvite && handleClickRemainBtn();
          dispatch({
            type: 'ai/fetchRemainingUse',
            payload: {
              apiKey,
            },
          });
          return;
        }

        if (isNL2SQL) {
          editorRef?.current?.setValue(JSON.parse(message).content);
        } else {
          chatResult.current += JSON.parse(message).content;
          setAiContent(chatResult.current);
        }
      } catch (error) {
        setIsLoading(false);
        setIsAiDrawerLoading(false);
        setIsStream(false);
        closeEventSource.current();
      }
    };

    const handleError = (error: any) => {
      console.error('Error:', error);
      setIsLoading(false);
      closeEventSource.current();
    };

    closeEventSource.current = connectToEventSource({
      url: `/api/ai/chat?${params}`,
      uid,
      onOpen: () => {
        setIsStream(true);
      },
      onMessage: handleMessage,
      onError: handleError,
    });
  };

  const executeSQL = (sql?: string) => {
    const sqlContent = sql || editorRef?.current?.getCurrentSelectContent() || editorRef?.current?.getAllContent();

    if (!sqlContent) {
      return;
    }
    props.onExecuteSQL && props.onExecuteSQL(sqlContent);
  };

  const saveConsole = (value?: string) => {
    // const a = editorRef.current?.getAllContent();
    const p: any = {
      id: executeParams.consoleId,
      status: ConsoleStatus.RELEASE,
      ddl: value,
    };

    historyServer.updateSavedConsole(p).then(() => {
      indexedDB.deleteData('chat2db', 'workspaceConsoleDDL', executeParams.consoleId!);
      message.success(i18n('common.tips.saveSuccessfully'));
      props.onConsoleSave && props.onConsoleSave();
      timingAutoSave(ConsoleStatus.RELEASE);
    });
  };

  const addAction = [
    {
      id: 'explainSQL',
      label: i18n('common.text.explainSQL'),
      action: (selectedText: string) => handleAIChatInEditor(selectedText, IPromptType.SQL_EXPLAIN),
    },
    {
      id: 'optimizeSQL',
      label: i18n('common.text.optimizeSQL'),
      action: (selectedText: string) => handleAIChatInEditor(selectedText, IPromptType.SQL_OPTIMIZER),
    },
    {
      id: 'changeSQL',
      label: i18n('common.text.conversionSQL'),
      action: (selectedText: string, ext?: string) => {
        handleAIChatInEditor(selectedText, IPromptType.SQL_2_SQL, ext);
      },
    },
  ];

  const handleClickRemainBtn = async () => {
    if (!isChat2DBAI) return;

    // chat2dbAi模型下，没有key，就需要登录
    if (!aiModel.aiConfig?.apiKey) {
      handleApiKeyEmptyOrGetQrCode(true);
      return;
    }
    handlePopUp();
  };

  /**
   * 弹框 关注公众号
   */
  const handlePopUp = () => {
    setModalProps({
      imageUrl:
        'http://oss.sqlgpt.cn/static/chat2db-wechat.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_256/quality,Q_80/format,webp',
      tip: (
        <>
          {aiModel.remainingUse?.remainingUses === 0 && <p>Key次数用完或者过期</p>}
          <p>微信扫描二维码并关注公众号获得 AI 使用机会。</p>
        </>
      ),
    });
    setPopularizeModal(true);
  };

  /**
   * 格式化sql
   */
  const handleSQLFormat = () => {
    let setValueType = 'select';
    let sql = editorRef?.current?.getCurrentSelectContent();
    if (!sql) {
      sql = editorRef?.current?.getAllContent() || '';
      setValueType = 'cover';
    }
    formatSql(sql, executeParams.type!).then((res) => {
      editorRef?.current?.setValue(res, setValueType);
    });
  };

  const handleSelectTableSyncModel = () => {
    const syncModel = localStorage.getItem('syncTableModel');
    const hasAiAccess = aiModel.hasWhite;
    if (syncModel !== null) {
      setSyncTableModel(Number(syncModel));
      return;
    }

    setSyncTableModel(hasAiAccess ? SyncModelType.AUTO : SyncModelType.MANUAL);
  };

  return (
    <div className={styles.console} ref={ref as any}>
      <Spin spinning={isLoading} style={{ height: '100%' }}>
        {hasAiChat && (
          <ChatInput
            isStream={isStream}
            disabled={isLoading}
            aiType={aiModel.aiConfig?.aiSqlSource}
            remainingUse={aiModel.remainingUse}
            remainingBtnLoading={props.remainingBtnLoading}
            tables={tableListName}
            onPressEnter={(value: string) => {
              // editorRef?.current?.toFocus();
              handleAiChat(value, IPromptType.NL_2_SQL);
            }}
            selectedTables={selectedTables}
            onSelectTables={(tables: string[]) => {
              if (tables.length > 8) {
                message.warning({
                  content: i18n('chat.input.tableSelect.error.TooManyTable'),
                });
                return;
              }
              setSelectedTables(tables);
            }}
            onClickRemainBtn={handleClickRemainBtn}
            syncTableModel={syncTableModel}
            onSelectTableSyncModel={(model: number) => {
              setSyncTableModel(model);
              localStorage.setItem('syncTableModel', String(model));
            }}
            onCancelStream={() => {
              closeEventSource.current();
              setIsStream(false);
              setIsLoading(false);
            }}
          />
        )}
        {/* <div key={uuid()}>{chatContent.current}</div> */}

        <Editor
          id={uid}
          defaultValue={defaultValue}
          isActive={isActive}
          ref={editorRef as any}
          className={hasAiChat ? styles.consoleEditorWithChat : styles.consoleEditor}
          addAction={addAction}
          onSave={saveConsole}
          onExecute={executeSQL}
          options={props.editorOptions}
        />

        {/* <NewEditor id={uid} dataSource={props.executeParams.type} database={props.executeParams.databaseName} /> */}

        {/* <Modal open={modelConfig.open}>{modelConfig.content}</Modal> */}
        <Drawer open={isAiDrawerOpen} getContainer={false} mask={false} onClose={() => setIsAiDrawerOpen(false)}>
          <Spin spinning={isAiDrawerLoading} style={{ height: '100%' }}>
            <div className={styles.aiBlock}>{aiContent}</div>
          </Spin>
        </Drawer>
      </Spin>

      <div className={styles.consoleOptionsWrapper}>
        <div className={styles.consoleOptionsLeft}>
          <Button type="primary" className={styles.runButton} onClick={() => executeSQL()}>
            <Iconfont code="&#xe637;" />
            {i18n('common.button.execute')}
          </Button>
          {hasSaveBtn && (
            <Button
              type="default"
              className={styles.saveButton}
              onClick={() => saveConsole(editorRef?.current?.getAllContent())}
            >
              {i18n('common.button.save')}
            </Button>
          )}
        </div>
        <Button type="text" onClick={handleSQLFormat}>
          {i18n('common.button.format')}
        </Button>
      </div>
      <Modal
        open={popularizeModal}
        footer={false}
        onCancel={() => {
          aiFetchIntervalRef.current && clearInterval(aiFetchIntervalRef.current);
          setPopularizeModal(false);
        }}
      >
        <Popularize {...modalProps} />
      </Modal>
    </div>
  );
}

const dvaModel = connect(({ ai, loading }: { ai: IAIState; loading: any }) => ({
  aiModel: ai,
  remainingBtnLoading: loading.effects['ai/fetchRemainingUse'],
}));
export default dvaModel(forwardRef(Console));
