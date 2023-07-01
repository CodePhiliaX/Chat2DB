import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'umi';
import { formatParams } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import { Button, Spin, message, notification, Drawer, Modal } from 'antd';
import ChatInput from './ChatInput';
import Editor, { IEditorOptions, IExportRefFunction, IRangeType } from './MonacoEditor';
import { format } from 'sql-formatter';
import sqlServer from '@/service/sql';
import historyServer from '@/service/history';
import { v4 as uuidv4 } from 'uuid';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import Iconfont from '../Iconfont';
import { ITreeNode } from '@/typings';
import styles from './index.less';
import i18n from '@/i18n';

enum IPromptType {
  NL_2_SQL = 'NL_2_SQL',
  SQL_EXPLAIN = 'SQL_EXPLAIN',
  SQL_OPTIMIZER = 'SQL_OPTIMIZER',
  SQL_2_SQL = 'SQL_2_SQL',
  ChatRobot = 'ChatRobot',
}

enum IPromptTypeText {
  NL_2_SQL = '自然语言转换',
  SQL_EXPLAIN = '解释SQL',
  SQL_OPTIMIZER = 'SQL优化',
  SQL_2_SQL = 'SQL转换',
  ChatRobot = 'Chat机器人',
}

export type IAppendValue = {
  text: any;
  range?: IRangeType;
};

interface IProps {
  /** 是否是活跃的console，用于快捷键 */
  isActive?: boolean;
  /** 添加或修改的内容 */
  appendValue?: IAppendValue;
  /** 是否开启AI输入 */
  hasAiChat: boolean;
  /** 是否可以开启SQL转到自然语言的相关ai操作 */
  hasAi2Lang?: boolean;
  /** 是否有 */
  hasSaveBtn?: boolean;
  value?: string;
  tables: ITreeNode[];
  executeParams: {
    databaseName?: string;
    dataSourceId?: number;
    type?: DatabaseTypeCode;
    consoleId?: number;
    schemaName?: string;
    consoleName?: string;
  };
  editorOptions?: IEditorOptions;
  // onSQLContentChange: (v: string) => void;
  onExecuteSQL: (result: any, sql: string, createHistoryParams) => void;
  onConsoleSave: () => void;
}

function Console(props: IProps) {
  const { hasAiChat = true, executeParams, appendValue, isActive, hasSaveBtn = true, value } = props;
  const uid = useMemo(() => uuidv4(), []);
  const chatResult = useRef('');
  const editorRef = useRef<IExportRefFunction>();
  const [selectedTables, setSelectedTables] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [aiContent, setAiContent] = useState('');
  const [isAiDrawerOpen, setIsAiDrawerOpen] = useState(false);
  const [isAiDrawerLoading, setIsAiDrawerLoading] = useState(false);

  useEffect(() => {
    if (appendValue) {
      editorRef?.current?.setValue(appendValue.text, appendValue.range);
    }
  }, [appendValue]);

  const tableListName = useMemo(() => {
    const tableList = (props.tables || []).map((t) => t.name);

    // 默认选中前八个
    setSelectedTables(tableList.slice(0, 8));

    return tableList;
  }, [props.tables]);

  const handleAiChat = (content: string, promptType: IPromptType) => {
    const { dataSourceId, databaseName, schemaName } = executeParams;
    const isNL2SQL = promptType === IPromptType.NL_2_SQL;
    if (isNL2SQL) {
      setIsLoading(true);
    } else {
      setIsAiDrawerOpen(true);
      setIsAiDrawerLoading(true);
    }
    const params = formatParams({
      message: content,
      promptType,
      dataSourceId,
      databaseName,
      schemaName,
      tableNames: selectedTables,
    });

    const handleMessage = (message: string) => {
      setIsLoading(false);

      try {
        const isEOF = message === '[DONE]';
        if (isEOF) {
          closeEventSource();
          setIsLoading(false);
          if (isNL2SQL) {
            editorRef?.current?.setValue('\n\n\n');
          } else {
            setIsAiDrawerLoading(false);
            chatResult.current += '\n\n\n';
            setAiContent(chatResult.current);
            chatResult.current = '';
          }
          return;
        }

        if (isNL2SQL) {
          editorRef?.current?.setValue(JSON.parse(message).content);
        } else {
          chatResult.current += JSON.parse(message).content;
        }
      } catch (error) {
        console.log('handleMessage', error);
        setIsLoading(false);
      }
    };

    const handleError = (error: any) => {
      console.error('Error:', error);
      setIsLoading(false);
    };

    const closeEventSource = connectToEventSource({
      url: `/api/ai/chat?${params}`,
      uid,
      onMessage: handleMessage,
      onError: handleError,
    });
  };

  const onPressChatInput = (value: string) => {
    handleAiChat(value, IPromptType.NL_2_SQL);
  };

  const executeSQL = (sql?: string) => {
    const sqlContent = sql || editorRef?.current?.getCurrentSelectContent() || editorRef?.current?.getAllContent();

    if (!sqlContent) {
      return;
    }

    let p: any = {
      sql: sqlContent,
      ...executeParams,
    };
    sqlServer.executeSql(p).then((res) => {
      let createHistoryParams: any = {
        ...executeParams,
        ddl: sqlContent,
      };
      props.onExecuteSQL?.(res, sqlContent!, createHistoryParams);
    });
  };

  const saveConsole = (value?: string) => {
    // const a = editorRef.current?.getAllContent();
    let p: any = {
      id: executeParams.consoleId,
      status: ConsoleStatus.RELEASE,
      ddl: value
    };
    historyServer.updateSavedConsole(p).then((res) => {
      message.success(i18n('common.tips.saveSuccessfully'));
      props.onConsoleSave && props.onConsoleSave();
    });
  };

  const addAction = useMemo(
    () => [
      {
        id: 'explainSQL',
        label: i18n('common.text.explainSQL'),
        action: (selectedText: string) => handleAiChat(selectedText, IPromptType.SQL_EXPLAIN),
      },
      {
        id: 'optimizeSQL',
        label: i18n('common.text.optimizeSQL'),
        action: (selectedText: string) => handleAiChat(selectedText, IPromptType.SQL_OPTIMIZER),
      },
      {
        id: 'changeSQL',
        label: i18n('common.text.conversionSQL'),
        action: (selectedText: string) => handleAiChat(selectedText, IPromptType.SQL_2_SQL),
      },
    ],
    [],
  );

  return (
    <div className={styles.console}>
      <Spin spinning={isLoading} style={{ height: '100%' }}>
        {hasAiChat && (
          <ChatInput
            tables={tableListName}
            onPressEnter={onPressChatInput}
            selectedTables={selectedTables}
            onSelectTables={(tables: string[]) => {
              setSelectedTables(tables);
            }}
          />
        )}
        {/* <div key={uuid()}>{chatContent.current}</div> */}

        <Editor
          id={uid}
          isActive={isActive}
          ref={editorRef as any}
          className={hasAiChat ? styles.consoleEditorWithChat : styles.consoleEditor}
          addAction={addAction}
          onSave={saveConsole}
          onExecute={executeSQL}
          options={props.editorOptions}
        // onChange={}
        />
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
            <Button type="default" className={styles.saveButton} onClick={() => saveConsole()}>
              {i18n('common.button.save')}
            </Button>
          )}
        </div>
        <Button
          type="text"
          onClick={() => {
            const contextTmp = editorRef?.current?.getAllContent();
            editorRef?.current?.setValue(format(contextTmp || ''), 'cover');
          }}
        >
          {i18n('common.button.format')}
        </Button>
      </div>
    </div>
  );
}

export default Console;
