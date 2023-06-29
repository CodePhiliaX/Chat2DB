import React, { useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'umi';
import { formatParams } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import { Button, Spin, message, notification } from 'antd';
import ChatInput from './ChatInput';
import Editor, { IEditorOptions, IExportRefFunction, IRangeType } from './MonacoEditor';
import { format } from 'sql-formatter';
import sqlServer from '@/service/sql';
import historyServer from '@/service/history';
import { v4 as uuidv4 } from 'uuid';
import styles from './index.less';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import Iconfont from '../Iconfont';
import { IWorkspaceModelType } from '@/models/workspace';

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
  editorOptions: IEditorOptions;
  // onSQLContentChange: (v: string) => void;
  onExecuteSQL: (result: any, sql?: string) => void;
  workspaceModel: IWorkspaceModelType;
  dispatch: any;
}

function Console(props: IProps) {
  const {
    hasAiChat = true,
    executeParams,
    appendValue,
    isActive,
    dispatch,
    hasSaveBtn = true,
    value,
  } = props;
  const uid = useMemo(() => uuidv4(), []);
  const chatResult = useRef('');
  const editorRef = useRef<IExportRefFunction>();
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (appendValue) {
      editorRef?.current?.setValue(appendValue.text, appendValue.range);
    }
  }, [appendValue]);

  const onPressChatInput = (value: string) => {
    const { dataSourceId, databaseName, schemaName } = executeParams;
    const params = formatParams({
      message: value,
      dataSourceId,
      databaseName,
      schemaName,
      //TODO:
      promptType: IPromptType.NL_2_SQL,
    });

    // setIsLoading(true);

    const handleMessage = (message: string) => {
      try {
        const isEOF = message === '[DONE]';
        if (isEOF) {
          closeEventSource();
          setIsLoading(false);
          console.log('chatResult', chatResult.current);
          return;
        }
        chatResult.current += JSON.parse(message).content;
        // setContext((prevData) => prevData + JSON.parse(message).content);
      } catch (error) {
        console.log('handleMessage', error);
      }
    };

    const handleError = (error: any) => {
      console.error('Error:', error);
    };

    const closeEventSource = connectToEventSource({
      url: `/api/ai/chat?${params}`,
      uid,
      onMessage: handleMessage,
      onError: handleError,
    });
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
    props.onExecuteSQL?.(undefined);
    sqlServer.executeSql(p).then((res) => {
      props.onExecuteSQL?.(res, sqlContent!);
      // console.log(res)
      let p: any = {
        ...executeParams,
        ddl: sqlContent,
      };
      historyServer.createHistory(p);
    });
  };

  const saveConsole = (value?: string) => {
    const a = editorRef.current?.getAllContent();

    let p: any = {
      id: executeParams.consoleId,
      status: ConsoleStatus.RELEASE,
    };
    historyServer.updateSavedConsole(p).then((res) => {
      message.success('保存成功');
      dispatch({
        type: 'workspace/fetchGetSavedConsole',
      });
      // notification.open({
      //   type: 'success',
      //   message: '保存成功',
      // });
    });
  };

  const addAction = useMemo(
    () => [
      {
        id: 'explainSQL',
        label: '解释SQL',
        action: (selectedText: string) => handleAIRelativeOperation(IPromptType.SQL_EXPLAIN, selectedText),
      },
      {
        id: 'optimizeSQL',
        label: '优化SQL',
        action: (selectedText: string) => handleAIRelativeOperation(IPromptType.SQL_OPTIMIZER, selectedText),
      },
      {
        id: 'changeSQL',
        label: 'SQL转化',
        action: (selectedText: string) => handleAIRelativeOperation(IPromptType.SQL_2_SQL, selectedText),
      },
    ],
    [],
  );

  const handleAIRelativeOperation = (id: string, selectedText: string) => {
    console.log('handleAIRelativeOperation', id, selectedText);
  };

  return (
    <div className={styles.console}>
      <Spin spinning={isLoading} style={{ height: '100%' }}>
        {hasAiChat && <ChatInput onPressEnter={onPressChatInput} />}
        {/* <div key={uuid()}>{chatContent.current}</div> */}
        <Editor
          id={uid}
          isActive={isActive}
          ref={editorRef as any}
          className={hasAiChat ? styles.console_editor_with_chat : styles.console_editor}
          addAction={addAction}
          onSave={saveConsole}
          onExecute={executeSQL}
          options={props.editorOptions}
        // onChange={}
        />
      </Spin>

      <div className={styles.console_options_wrapper}>
        <div className={styles.console_options_left}>
          <Button type="primary" className={styles.run_button} onClick={() => executeSQL()}>
            <Iconfont code="&#xe637;" />
            RUN
          </Button>
          {hasSaveBtn && (
            <Button type="default" className={styles.save_button} onClick={() => saveConsole()}>
              SAVE
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
          Format
        </Button>
      </div>
    </div>
  );
}

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace,
}));

export default dvaModel(Console);
