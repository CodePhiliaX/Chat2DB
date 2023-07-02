import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'umi';
import { formatParams } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import { Button, Spin, message, Drawer, Modal } from 'antd';
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
import { IRemainingUse } from '@/typings/ai';
import { IAIState } from '@/models/ai';
import { WECHAT_MP_URL } from '@/constants/social';
import Popularize from '@/components/Popularize';
import { handelLocalStorageSavedConsole, readLocalStorageSavedConsoleText } from '@/utils'

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
  /** 是谁在调用我 */
  source: 'workspace',
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
  };
  tableList?: ITreeNode[];
  editorOptions?: IEditorOptions;
  aiModel: IAIState;
  dispatch: Function;
  // remainingUse: IAIState['remainingUse'];
  // onSQLContentChange: (v: string) => void;
  onExecuteSQL: (result: any, sql: string, createHistoryParams) => void;
  onConsoleSave: () => void;
  tables: any[];
}

function Console(props: IProps) {
  const { hasAiChat = true, executeParams, appendValue, isActive, hasSaveBtn = true, value, aiModel, dispatch, source, defaultValue } = props;
  const uid = useMemo(() => uuidv4(), []);
  const chatResult = useRef('');
  const editorRef = useRef<IExportRefFunction>();
  const [selectedTables, setSelectedTables] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [aiContent, setAiContent] = useState('');
  const [isAiDrawerOpen, setIsAiDrawerOpen] = useState(false);
  const [isAiDrawerLoading, setIsAiDrawerLoading] = useState(false);
  const monacoHint = useRef<any>();
  const [modal, contextHolder] = Modal.useModal();
  const [popularizeModal, setPopularizeModal] = useState(false);
  const timerRef = useRef<any>();

  useEffect(() => {
    if (appendValue) {
      editorRef?.current?.setValue(appendValue.text, appendValue.range);
    }
  }, [appendValue]);

  useEffect(() => {
    monacoHint.current?.dispose();
    const myEditorHintData: any = {};
    props.tables?.map((item: any) => {
      myEditorHintData[item.name] = [];
    });
    monacoHint.current = editorRef?.current?.handleRegisterTigger(myEditorHintData);
  }, [props.tables]);

  useEffect(() => {
    if (source !== 'workspace') {
      return
    }
    // 离开时保存
    if (!isActive) {
      // 离开时清除定时器
      if (timerRef.current) {
        clearInterval(timerRef.current)
        handelLocalStorageSavedConsole(executeParams.consoleId!, 'save', editorRef?.current?.getAllContent());
      }
    } else {
      // 活跃时自动保存
      timingAutoSave();
    }
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current)
      }
    }
  }, [isActive])

  useEffect(() => {
    if (source !== 'workspace') {
      return
    }
    const value = readLocalStorageSavedConsoleText(executeParams.consoleId!)
    if (value) {
      editorRef?.current?.setValue(value, 'reset');
    }
  }, [])

  function timingAutoSave() {
    timerRef.current = setInterval(() => {
      handelLocalStorageSavedConsole(executeParams.consoleId!, 'save', editorRef?.current?.getAllContent());
    }, 5000)
  }

  const tableListName = useMemo(() => {
    const tableList = (props.tables || []).map((t) => t.name);

    // 默认选中前八个
    setSelectedTables(tableList.slice(0, 8));

    return tableList;
  }, [props.tables]);

  const handleAiChat = (content: string, promptType: IPromptType) => {
    // if (!aiModel.remainingUse?.remainingUses) {
    //   popUpPrompts();
    //   return;
    // }

    dispatch({
      type: 'ai/fetchRemainingUse',
      payload: {
        key: aiModel?.remainingUse?.key,
      },
    });

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
      ddl: value,
    };

    historyServer.updateSavedConsole(p).then((res) => {
      handelLocalStorageSavedConsole(executeParams.consoleId!, 'delete');
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

  const popUpPrompts = () => {
    setPopularizeModal(true)
  };
  return (
    <div className={styles.console}>
      <Spin spinning={isLoading} style={{ height: '100%' }}>
        {hasAiChat && (
          <ChatInput
            tables={tableListName}
            remainingUse={aiModel.remainingUse}
            onPressEnter={onPressChatInput}
            selectedTables={selectedTables}
            onSelectTables={(tables: string[]) => {
              setSelectedTables(tables);
            }}
            onClickRemainBtn={() => {
              popUpPrompts();
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
          tables={props.tables}
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
            <Button type="default" className={styles.saveButton} onClick={() => saveConsole(editorRef?.current?.getAllContent())}>
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
      <Modal
        open={popularizeModal}
        footer={false}
        onCancel={() => setPopularizeModal(false)}
      >
        <Popularize></Popularize>
      </Modal>

    </div>
  );
}

const dvaModel = connect(({ ai }: { ai: IAIState }) => ({
  aiModel: ai,
}));
export default dvaModel(Console);
