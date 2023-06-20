import React, { memo, useEffect, useRef, useState, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Dropdown, Modal, Button, message } from 'antd';
import MonacoEditor from '@/components/MonacoEditor';
import { ITreeNode, IDB, IConnectionBase } from '@/types';
import mysqlServer from '@/service/mysql';
import { DatabaseTypeCode, WindowTabStatus } from '@/utils/constants';
import historyServer from '@/service/history';
import { format } from 'sql-formatter';
import { DatabaseContext } from '@/context/database';
import { TreeNodeType } from '@/utils/constants';

export interface IOperationData {
  type: string;
  nodeData?: ITreeNode;
  database?: IDB;
  connectionDetaile?: IConnectionBase;
  callback?: Function;
}

export interface IOperationTableModalProps {
  className?: string;
  // operationData: IOperationData;
  // setOperationData: Function;
}

export default memo<IOperationTableModalProps>(function OperationTableModal(
  props,
) {
  const { className } = props;
  const { model, setOperationDataDialog, setNeedRefreshNodeTree } =
    useContext(DatabaseContext);
  const { operationData } = model;

  const monacoEditor = useRef();
  const [consoleId, setConsoleId] = useState<string>();

  // useEffect(() => {
  //   addWindowTab();
  // }, [operationData])

  useEffect(() => {
    if (!operationData) {
      return;
    }
    if (operationData.type == 'new') {
      mysqlServer
        .createTableExample({ dbType: operationData.nodeData?.dataType })
        .then((res) => {
          setMonacoEditorValue(monacoEditor.current, res);
        });
    }
    if (operationData.type == 'edit') {
      mysqlServer
        .updateTableExample({ dbType: operationData.database?.databaseType! })
        .then((res) => {
          setMonacoEditorValue(monacoEditor.current, res);
        });
    }
    if (operationData.type == 'export') {
      let p = {
        tableName: operationData.nodeData?.name!,
        dataSourceId: operationData.nodeData?.dataSourceId!,
        databaseName: operationData.nodeData?.databaseName!,
        schemaName: operationData.nodeData?.schemaName

      };
      mysqlServer.exportCreateTableSql(p).then((res) => {
        setMonacoEditorValue(monacoEditor.current, res);
      });
    }
  }, [operationData]);

  // const addWindowTab = () => {
  //   if (!operationData) {
  //     return
  //   }
  //   let p = {
  //     name: '弹窗',
  //     type: operationData.nodeData?.dataType,
  //     dataSourceId: operationData.nodeData?.dataSourceId,
  //     databaseName: operationData.nodeData?.databaseName,
  //     status: WindowTabStatus.DRAFT,
  //     tabOpened: 'n',
  //     ddl: ''
  //   }
  //   historyServer.saveWindowTab(p).then(res => {
  //     setConsoleId(res)
  //   })
  // };

  function handleOk() {
    if (!operationData) {
      return;
    }
    if (operationData.type == 'new' || operationData.type == 'edit') {
      let p: any = {
        consoleId,
        sql: getMonacoEditorValue(monacoEditor.current),
        dataSourceId: operationData.nodeData?.dataSourceId,
        databaseName: operationData.nodeData?.databaseName,
        schemaName: operationData.nodeData?.schemaName,
      };
      if (operationData.type == 'edit') {
        p.tableName = operationData.nodeData?.name;
      }
      mysqlServer.executeTable(p).then((res) => {
        message.success('更新成功');
        setNeedRefreshNodeTree({
          databaseName: operationData.nodeData?.databaseName,
          dataSourceId: operationData.nodeData?.dataSourceId,
          nodeType: TreeNodeType.TABLES,
        });
        setTimeout(() => {
          setOperationDataDialog(false);
        }, 0);
        // operationData.callback && operationData.callback(operationData.database)
      });
    } else {
      setOperationDataDialog(false);
    }
  }

  function handleCancel() {
    setOperationDataDialog(false);
  }

  function setMonacoEditorValue(monacoEditor: any, value: string) {
    const model = monacoEditor.getModel(monacoEditor);
    model.setValue(format(value, {}));
  }
  const getMonacoEditorValue = (monacoEditor: any) => {
    const model = monacoEditor.getModel(monacoEditor);
    const value = model.getValue();
    return value;
  };

  const getEditor = (editor: any) => {
    monacoEditor.current = editor;
  };

  function renderTitle() {
    if (!operationData) {
      return;
    }
    if (operationData.type == 'new') {
      return '新建表';
    }
    if (operationData.type == 'edit') {
      return '设计表结构';
    }
    if (operationData.type == 'export') {
      return '建表语句';
    }
  }

  function renderOkText() {
    if (!operationData) {
      return;
    }
    switch (operationData.type) {
      case 'edit':
        return '修改';
      case 'new':
        return '添加';
      case 'export':
        return '关闭';
      default:
        return '确定';
    }
  }

  if (!operationData) {
    return <></>;
  }

  return (
    <Modal
      className={classnames(className, styles.box)}
      title={renderTitle()}
      open={true}
      onOk={handleOk}
      onCancel={handleCancel}
      width="60vw"
      maskClosable={false}
      footer={
        <>
          {operationData.type !== 'export' && (
            <Button onClick={handleCancel} className={styles.cancel}>
              取消
            </Button>
          )}
          <Button type="primary" onClick={handleOk} className={styles.cancel}>
            {renderOkText()}
          </Button>
        </>
      }
    >
      <div className={styles.monacoEditor}>
        <MonacoEditor id="edit" getEditor={getEditor} />
      </div>
    </Modal>
  );
});
