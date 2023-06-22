import { formatParams, uuid } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import { Button, Spin } from 'antd';
import React, { ForwardedRef, useEffect, useMemo, useRef, useState } from 'react';
import ChatInput from './ChatInput';
import Editor, { IExportRefFunction } from './MonacoEditor';
import { format } from 'sql-formatter';
import sqlServer from '@/service/sql';
import historyServer from '@/service/history';
import MonacoEditor from 'react-monaco-editor';
import { useReducerContext } from '@/pages/main/workspace/index'

import styles from './index.less';
import Loading from '../Loading/Loading';
import { DatabaseTypeCode } from '@/constants/database';

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

interface IProps {
  /** 是否开启AI输入 */
  hasAiChat: boolean;
  /** 是否可以开启SQL转到自然语言的相关ai操作 */
  hasAi2Lang: boolean;
  value?: string;
  executeParams: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    consoleId: number;
    schemaName?: string;
    consoleName: string;
  }
}

function Console(props: IProps) {
  const { hasAiChat = true, value, executeParams } = props;
  const uid = useMemo(() => uuid(), []);
  const chatResult = useRef('');
  const editorRef = useRef<IExportRefFunction>();
  const [context, setContext] = useState<string>();
  const [isLoading, setIsLoading] = useState(false);
  const { state, dispatch } = useReducerContext();
  const { currentWorkspaceData } = state;

  useEffect(() => {
    setContext(value);
  }, [value])

  const onPressChatInput = (value: string) => {
    const params = formatParams({
      message: value,
    });

    // setIsLoading(true);

    const handleMessage = (message: string) => {
      try {
        const isEOF = message === '[DONE]';
        if (isEOF) {
          closeEventSource();
          // setContext(context + '\n' + chatResult.current + '\n\n\n');
          setIsLoading(false);
          return;
        }
        chatResult.current += JSON.parse(message).content;
        setContext((prevData) => prevData + JSON.parse(message).content);
      } catch (error) {
        console.log('handleMessage', error);
      }
    };

    const handleError = (error: any) => {
      console.error('Error:', error);
    };

    const closeEventSource = connectToEventSource({
      url: `/api/ai/chat1?${params}`,
      uid,
      onMessage: handleMessage,
      onError: handleError,
    });
  };

  const executeSQL = () => {
    let sqlContent = editorRef?.current?.getCurrentSelectContent();
    if (!sqlContent) {
      sqlContent = editorRef?.current?.getAllContent();
    }
    if (!sqlContent) {
      return;
    }

    let p = {
      sql: sqlContent,
      ...executeParams,
    };
    sqlServer.executeSql(p).then((res) => {
      console.log(res)
      let p = {
        ...executeParams,
        ddl: sqlContent
      };
      historyServer.createHistory(p);
      // setManageResultDataList(res);
    }).catch((error) => {
      // setManageResultDataList([]);
    });
  };

  const saveWindowTab = () => {
    // let p = {
    //   id: windowTab.consoleId,
    //   name: windowTab?.name,
    //   type: windowTab.DBType,
    //   dataSourceId: +params.id,
    //   databaseName: windowTab.databaseName,
    //   status: WindowTabStatus.RELEASE,
    //   ddl: getMonacoEditorValue(),
    // };
    // historyServer.updateWindowTab(p).then((res) => {
    //   message.success('保存成功');
    // });
  };

  const addAction = [
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
  ];

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
          ref={editorRef}
          value={context}
          onChange={(v) => setContext(v)}
          className={hasAiChat ? styles.console_editor_with_chat : styles.console_editor}
          addAction={addAction}
        />
      </Spin>

      <div className={styles.console_options_wrapper}>
        <div>
          <Button type="primary" style={{ marginRight: '10px' }} onClick={executeSQL}>
            RUN
          </Button>
          <Button type="default" onClick={saveWindowTab}>
            SAVE
          </Button>
        </div>
        <Button
          type="text"
          onClick={() => {
            const contextTmp = editorRef?.current?.getAllContent();
            setContext(format(contextTmp || ''));
          }}
        >
          Format
        </Button>
      </div>
    </div>
  );
}

export default Console;
