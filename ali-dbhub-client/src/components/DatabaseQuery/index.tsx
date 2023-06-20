import React, {
  memo,
  useState,
  useRef,
  useEffect,
  useContext,
  useMemo,
} from 'react';
import classnames from 'classnames';
import { useParams } from 'umi';
import { ISQLQueryConsole } from '@/types';
import { Button, Divider, Input, message, Modal, Select, Tooltip } from 'antd';
import { TreeNodeType, WindowTabStatus, OSType } from '@/utils/constants';
import Iconfont from '@/components/Iconfont';
import MonacoEditor, { setEditorHint, setMonacoValue } from '@/components/MonacoEditor';
import DraggableContainer from '@/components/DraggableContainer';
import SearchResult from '@/components/SearchResult';
import LoadingContent from '@/components/Loading/LoadingContent';
import mysqlServer from '@/service/mysql';
import historyServer from '@/service/history';
import { format } from 'sql-formatter';
import { OSnow } from '@/utils';
import { DatabaseContext } from '@/context/database';
import { formatParams, uuid } from '@/utils/common';
import connectToEventSource from '@/utils/eventSource';
import configService from '@/service/config';

import styles from './index.less';
const { Option } = Select;
const monaco = require('monaco-editor/esm/vs/editor/editor.api');

export interface IDatabaseQueryProps {
  activeTabKey: string;
  windowTab: ISQLQueryConsole;
}

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

interface IProps extends IDatabaseQueryProps {
  className?: string;
}

type IParams = { tableNames: string[]; ext: string; destSqlType: string };

let monacoEditorExternalList: any = {};

const initModal = {
  open: false,
  title: '',
  handleOk: () => {},
  handleCancel: () => {},
  content: <></>,
};
export default function DatabaseQuery(props: IProps) {
  const { model, setDblclickNodeData, setShowSearchResult } = useContext(DatabaseContext);
  const { activeTabKey, windowTab } = props;
  const params: { id: string; type: string } = useParams();
  const [manageResultDataList, setManageResultDataList] = useState<any>([]);

  const tableListRef = useRef<Array<{ label: string; value: string }>>([]);
  const uid = useRef<string>(uuid());
  const monacoEditor = useRef<any>(null);
  const volatileRef = useRef<any>(null);
  const monacoHint = useRef<any>(null);
  const { dblclickNodeData, showSearchResult } = model;
  const extendParams = useRef<IParams>({
    tableNames: [],
    ext: '',
    destSqlType: '',
  });
  const [modalConfig, setModalConfig] = useState(initModal);
  // const [aiDropVisible, setAiDropVisible] = useState(false);
  const isActive = windowTab.consoleId === +activeTabKey;

  useEffect(() => {
    if (!isActive) {
      return;
    }
    connectConsole();
    getTableList();
  }, [activeTabKey]);

  useEffect(() => {
    if (!dblclickNodeData) {
      return;
    }
    if (
      dblclickNodeData.databaseName !== windowTab.databaseName ||
      dblclickNodeData.dataSourceId !== windowTab.dataSourceId
    ) {
      return;
    }
    const nodeData = dblclickNodeData;

    if (nodeData && windowTab.consoleId === +activeTabKey) {
      const model = monacoEditor.current.getModel(monacoEditor.current);
      let value = model.getValue();
      if (nodeData.nodeType === TreeNodeType.TABLE) {
        if (value == 'SELECT * FROM' || value == 'SELECT * FROM ') {
          model.setValue(`SELECT * FROM ${nodeData.name};`);
        } else {
          model.setValue(`${value}\nSELECT * FROM ${nodeData.name};`);
        }
      } else if (nodeData.nodeType == TreeNodeType.COLUMN) {
        if (value == 'SELECT * FROM' || value == 'SELECT * FROM ') {
          model.setValue(
            `SELECT * FROM ${nodeData?.databaseName} WHERE ${nodeData.name} = ''`,
          );
        } else {
          model.setValue(
            `${value}\nSELECT * FROM ${nodeData?.databaseName} WHERE ${nodeData.name} = ''`,
          );
        }
      }
      setDblclickNodeData(null);
    }
  }, [dblclickNodeData]);

  const connectConsole = () => {
    const { consoleId, dataSourceId, databaseName } = windowTab || {};
    mysqlServer.connectConsole({
      consoleId,
      dataSourceId,
      databaseName,
    });
  };

  const getTableList = () => {
    const p = {
      dataSourceId: windowTab.dataSourceId!,
      databaseName: windowTab.databaseName!,
      pageNo: 1,
      pageSize: 999,
    };

    mysqlServer.getList(p).then((res) => {
      const tableList = res.data?.map((item) => {
        return {
          name: item.name,
          key: item.name,
        };
      });
      disposalEditorHintData(tableList);
    });
  };

  const disposalEditorHintData = (tableList: any) => {
    try {
      monacoHint.current?.dispose();
      const myEditorHintData: any = {};
      tableList?.map((item: any) => {
        myEditorHintData[item.name] = [];
      });
      monacoHint.current = setEditorHint(myEditorHintData);


      const timer = setTimeout(() => {
        
      }, 1000);

      clearInterval(timer)
      
    } catch {}
  };

  const getEditor = (editor: any) => {
    monacoEditor.current = editor;
    monacoEditorExternalList[activeTabKey] = editor;
    const model = editor.getModel(editor);
    model.setValue(
      localStorage.getItem(
        `window-sql-${windowTab.dataSourceId}-${windowTab.databaseName}-${windowTab.consoleId}`,
      ) ||
        windowTab.ddl ||
        '',
    );
  };

  const callback = () => {
    monacoEditor.current && monacoEditor.current.layout();
  };

  /** 获取编辑器整体值 */
  const getMonacoEditorValue = () => {
    if (monacoEditor?.current?.getModel) {
      const model = monacoEditor?.current.getModel(monacoEditor?.current);
      const value = model.getValue();
      return value;
    }
  };

  /** 获取选中区域的值 */
  const getSelectionVal = () => {
    const selection = monacoEditor.current.getSelection(); // 获取光标选中的值
    const { startLineNumber, endLineNumber, startColumn, endColumn } =
      selection;
    const model = monacoEditor.current.getModel(monacoEditor.current);
    const value = model.getValueInRange({
      startLineNumber,
      startColumn,
      endLineNumber,
      endColumn,
    });
    return value;
  };

  const executeSql = () => {
    setShowSearchResult(true);
    const sql = getSelectionVal() || getMonacoEditorValue();
    if (!sql) {
      message.warning('请输入SQL语句');
      return;
    }
    let p = {
      sql,
      type: windowTab.DBType,
      consoleId: +windowTab.consoleId,
      dataSourceId: windowTab?.dataSourceId as number,
      databaseName: windowTab?.databaseName,
      schemaName: windowTab?.schemaName,
    };
    setManageResultDataList(null);
    mysqlServer
      .executeSql(p)
      .then((res) => {
        let p = {
          dataSourceId: windowTab?.dataSourceId,
          databaseName: windowTab?.databaseName,
          name: windowTab?.name,
          ddl: sql,
          type: windowTab.DBType,
        };
        historyServer.createHistory(p);
        setManageResultDataList(res);
      })
      .catch((error) => {
        setManageResultDataList([]);
      });
  };

  const saveWindowTabTab = () => {
    let p = {
      id: windowTab.consoleId,
      name: windowTab?.name,
      type: windowTab.DBType,
      dataSourceId: +params.id,
      databaseName: windowTab.databaseName,
      status: WindowTabStatus.RELEASE,
      ddl: getMonacoEditorValue(),
    };
    historyServer.updateWindowTab(p).then((res) => {
      message.success('保存成功');
    });
  };

  const formatValue = () => {
    const model = monacoEditor.current.getModel(monacoEditor.current);
    const value = model.getValue();
    setMonacoValue(monacoEditor.current,format(value, {}))
  };

  const onClickChatbot = () => {
    const sql = getSelectionVal() || getMonacoEditorValue();
    if (!sql) {
      message.warning('请选择语句');
      return;
    }

    chat2SQL(IPromptType.ChatRobot);
  };

  const monacoEditorChange = () => {
    localStorage.setItem(
      `window-sql-${windowTab.dataSourceId}-${windowTab.databaseName}-${windowTab.consoleId}`,
      getMonacoEditorValue(),
    );
  };

  const chat2SQL = (promptType: IPromptType) => {
    const sentence = getSelectionVal();

    // 自然语言转化SQL
    const model = monacoEditor.current.getModel(monacoEditor.current);
    const preValue = model.getValue();

    model.setValue(
      `${preValue}\n\n--- BEGIN ---\n${sentence}\n--- ${IPromptTypeText[promptType]} ---\n`,
    );

    const { dataSourceId, databaseName } = windowTab || {};
    const { tableNames: tableList, ext, destSqlType } = extendParams.current;
    const tableNames = tableList
      .map((table) => `tableNames=${table}`)
      .join('&');
    const params =
      formatParams({
        dataSourceId,
        databaseName,
        promptType,
        message: sentence,
        ext,
        destSqlType,
      }) + tableNames;

    const handleMessage = (message: string) => {
      const isEOF = message === '[DONE]';

      // 获取当前编辑器的model
      const model = monacoEditor.current.getModel();
      // 获取model的行数
      const lineCount = model.getLineCount();
      // 在文档的末尾添加内容
      model.applyEdits([
        {
          range: new monaco.Range(
            lineCount,
            model.getLineMaxColumn(lineCount),
            lineCount,
            model.getLineMaxColumn(lineCount),
          ),
          text: isEOF ? '\n--- END --- \n' : JSON.parse(message).content,
        },
      ]);

      if (isEOF) {
        closeEventSource();
      }
    };

    const handleError = (error: any) => {
      console.error('Error:', error);
    };

    // 连接到 EventSourcePolyfill 并设置回调函数

    const urlBase =
      promptType === IPromptType.ChatRobot ? '/api/ai/chat1' : '/api/ai/chat';
    const closeEventSource = connectToEventSource({
      url: `${urlBase}?${params}`,
      uid: uid.current,
      onMessage: handleMessage,
      onError: handleError,
    });
    extendParams.current = { tableNames: [], ext: '', destSqlType: '' };
  };

  const handleAIRelativeOperation = async (
    type: 'lang2SQL' | 'explainSQL' | 'optimizeSQL' | 'changeSQL',
  ) => {
    const sentence = getSelectionVal();
    if (!sentence) {
      message.warning('请选中需要处理的语句');
      return;
    }

    const { apiKey } = await configService.getChatGptSystemConfig();
    if(!apiKey){
      message.warning('请在设置中配置AI的apiKey！')
      return;
    }

    switch (type) {
      case 'lang2SQL':
        lang2SQL('withParams');
        break;
      case 'explainSQL':
        explainSQL('withParams');
        break;
      case 'optimizeSQL':
        optimizeSQL('withParams');
        break;
      case 'changeSQL':
        changeSQL('withParams');
        break;
      default:
        break;
    }
  };

  /**
   * 自然语言转化SQL
   */
  const lang2SQL = async (type?: 'withParams') => {
    if (!type) {
      chat2SQL(IPromptType.NL_2_SQL);
    } else {
      // ---拉取下数据库表----
      try {
        const p = {
          dataSourceId: windowTab.dataSourceId!,
          databaseName: windowTab.databaseName!,
          pageNo: 1,
          pageSize: 999,
        };
        let res = await mysqlServer.getList(p);
        tableListRef.current = res.data?.map((item) => ({
          label: item.name,
          value: item.name,
        }));
      } catch (error) {}
      // --------

      setModalConfig({
        open: true,
        title: '请选择表',
        handleOk: () => {
          chat2SQL(IPromptType.NL_2_SQL);
          setModalConfig(initModal);
        },
        handleCancel: () => {
          setModalConfig(initModal);
        },
        content: (
          <Select
            key={IPromptType.NL_2_SQL}
            mode="tags"
            style={{ width: '100%' }}
            placeholder="请输入想要查询的表"
            onChange={(values) => {
              extendParams.current = {
                tableNames: values,
                ext: '',
                destSqlType: '',
              };
            }}
            options={tableListRef.current}
          />
        ),
      });
    }
  };
  /**
   * 解释SQL
   */
  const explainSQL = (type?: 'withParams') => {
    if (!type) {
      chat2SQL(IPromptType.SQL_EXPLAIN);
    } else {
      setModalConfig({
        open: true,
        title: '请输入其他附加信息',
        handleOk: () => {
          chat2SQL(IPromptType.SQL_EXPLAIN);
          setModalConfig(initModal);
        },
        handleCancel: () => {
          setModalConfig(initModal);
        },
        content: (
          <Input
            key={IPromptType.SQL_EXPLAIN}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
              extendParams.current = {
                tableNames: [],
                ext: e.target.value,
                destSqlType: '',
              };
            }}
            placeholder="例如：解释SQL查询的目的"
          />
        ),
      });
      // Modal.confirm({
      //   title: '输入额外参数信息',
      //   content: (
      //     <Input
      //       onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
      //         extendParams.current.ext = e.target.value;

      //       }}
      //     />
      //   ),
      //   onOk: () => chat2SQL(IPromptType.SQL_EXPLAIN),
      // });
    }
  };

  /**
   * 优化SQL
   */
  const optimizeSQL = (type?: 'withParams') => {
    if (!type) {
      chat2SQL(IPromptType.SQL_OPTIMIZER);
    } else {
      setModalConfig({
        open: true,
        title: '请输入其他附加信息',
        handleOk: () => {
          chat2SQL(IPromptType.SQL_OPTIMIZER);
          setModalConfig(initModal);
        },
        handleCancel: () => {
          setModalConfig(initModal);
        },
        content: (
          <Input
            key={IPromptType.SQL_OPTIMIZER}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
              extendParams.current = {
                tableNames: [],
                ext: e.target.value,
                destSqlType: '',
              };
            }}
            placeholder="例如：提供索引优化建议"
          />
        ),
      });
      // Modal.confirm({
      //   title: '输入额外参数信息',
      //   content: (
      //     <Input
      //       onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
      //         extendParams.current.ext = e.target.value;
      //       }}
      //     />
      //   ),
      //   onOk: () => {
      //     chat2SQL(IPromptType.SQL_OPTIMIZER);
      //   },
      // });
    }
  };

  const changeSQL = (type?: 'withParams') => {
    if (!type) {
      chat2SQL(IPromptType.SQL_2_SQL);
    } else {
      setModalConfig({
        open: true,
        title: '请输入其他附加信息',
        handleOk: () => {
          chat2SQL(IPromptType.SQL_2_SQL);
          setModalConfig(initModal);
        },
        handleCancel: () => {
          setModalConfig(initModal);
        },
        content: (
          <>
            <Input
              addonBefore="目标数据库类型"
              key={IPromptType.SQL_2_SQL}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                extendParams.current = {
                  ...extendParams.current,
                  destSqlType: e.target.value,
                };
              }}
              placeholder="例如: MySQL"
              style={{ marginBottom: 10 }}
            />
            <Input
              addonBefore="其他附加条件 "
              key={IPromptType.SQL_2_SQL + 'ext'}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                extendParams.current = {
                  ...extendParams.current,
                  ext: e.target.value,
                };
              }}
              placeholder="例如：使用On Conflict语法来替代的Merge Into"
            />
          </>
        ),
      });
      // Modal.confirm({
      //   title: '请填写对应的数据库名',
      //   content: (
      //     <Input
      //       onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
      //         extendParams.current.destSqlType = e.target.value;
      //       }}
      //     />
      //   ),
      //   onOk: () => {
      //     chat2SQL(IPromptType.SQL_2_SQL);
      //   },
      // });
    }
  };

  const optBtn = [
    /** 基础SQL命令 */
    [
      {
        name: OSnow === OSType.WIN ? '执行 Ctrl + Enter' : '执行 CMD + Enter',
        icon: '\ue626',
        onClick: executeSql,
      },
      {
        name: OSnow === OSType.WIN ? '保存 Ctrl + S' : '保存 CMD + S',
        icon: '\ue645',
        onClick: saveWindowTabTab,
      },
      { name: '格式化', icon: '\ue7f8', onClick: formatValue },
      {
        name: 'chatbot',
        icon: '\ue70e',
        onClick: onClickChatbot,
      },
    ],
    /** 自然语言转化SQL */
    [
      // { name: '自然语言转SQL', icon: '\ue626', onClick: () => lang2SQL() },
      {
        name: '自然语言转SQL',
        icon: '\ue626',
        onClick: () => handleAIRelativeOperation('lang2SQL'),
      },
    ],
    // /** 解释SQL */
    [
      // { name: 'SQL解释', icon: '\ue626', onClick: () => explainSQL() },
      {
        name: 'SQL解释',
        icon: '\ue626',
        onClick: () => handleAIRelativeOperation('explainSQL'),
      },
    ],
    // /** 优化SQL */
    [
      // { name: 'SQL优化', icon: '\ue626', onClick: () => optimizeSQL() },
      {
        name: 'SQL优化',
        icon: '\ue626',
        onClick: () => handleAIRelativeOperation('optimizeSQL'),
      },
    ],
    // /** SQL转化 */
    [
      // { name: 'SQL转化', icon: '\ue626', onClick: () => changeSQL() },
      {
        name: 'SQL转化',
        icon: '\ue626',
        onClick: () => handleAIRelativeOperation('changeSQL'),
      },
    ],
  ];

  const renderOptBtn = () => {
    let dom = [];
    for (let i = 0; i < optBtn.length; i++) {
      const optList = optBtn[i];
      let tmpDom: Array<React.ReactNode> = [];
      if (i === 0) {
        tmpDom = (optList || []).map((item: any, index) => (
          <Tooltip key={index} placement="bottom" title={item.name}>
            <Iconfont
              code={item.icon}
              className={styles.icon}
              onClick={item.onClick}
            />
          </Tooltip>
        ));
      } else {
        tmpDom = (optList || []).map((item: any, index) => (
          <Button
            key={index}
            type="link"
            onClick={item.onClick}
            className={styles['ai-btn']}
          >
            {item.name}
          </Button>
        ));
      }
      tmpDom.push(<Divider key={'divider'} type="vertical" />);
      dom.push([...tmpDom]);
    }
    // dom.push(
    //   <Select
    //     onMouseEnter={()=>{setAiDropVisible(true)}}
    //     onMouseLeave={()=>{setAiDropVisible(false)}}
    //     dropdownVisible={aiDropVisible}
    //     dropdownMatchSelectWidth={false}
    //     dropdownRender={() => <></>}
    //   >
    //     <Option value="1">Option 1</Option>
    //     <Option value="2">Option 2</Option>
    //     <Option value="3">Option 3</Option>
    //   </Select>,
    // );
    return dom;
    // return <Select />;
  };

  return (
    <>
      <DraggableContainer
        className={classnames(styles.databaseQuery)}
        callback={callback}
        showLine={showSearchResult}
        direction="row"
        volatileDom={{
          volatileRef: volatileRef,
          volatileIndex: 1,
        }}
      >
        <div className={styles.console}>
          <div className={styles.operatingArea}>
            <div className={styles.left}>{renderOptBtn()}</div>
            <div className={styles.right}>
              <span>dataSourceName: {windowTab.dataSourceName}</span>
              <span>database: {windowTab.databaseName}</span>
            </div>
          </div>
          <div className={styles.monacoEditor}>
            <MonacoEditor
              isActive={isActive}
              onSave={saveWindowTabTab}
              onExecute={executeSql}
              onChange={monacoEditorChange}
              id={windowTab.consoleId!}
              getEditor={getEditor}
            />
          </div>
        </div>
        <div
          ref={volatileRef}
          style={{ display: showSearchResult ? 'block' : 'none' }}
          className={styles.searchResult}
        >
          <LoadingContent data={manageResultDataList} handleEmpty>
            <SearchResult manageResultDataList={manageResultDataList} />
          </LoadingContent>
        </div>
      </DraggableContainer>

      {modalConfig?.open && (
        <Modal
          title={modalConfig.title}
          open={modalConfig.open}
          onOk={modalConfig.handleOk}
          onCancel={modalConfig.handleCancel}
          maskClosable={false}
          okText="确认"
          cancelText="取消"
        >
          <div className={styles.modalBox}>{modalConfig.content}</div>
        </Modal>
      )}
    </>
  );
}
