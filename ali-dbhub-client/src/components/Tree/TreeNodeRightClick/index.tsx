import React, { memo, useContext, useState } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import Iconfont from '../../Iconfont';
import { MenuProps, message } from 'antd';
import { Modal, Input } from 'antd';
// import { Menu } from 'antd';
import Menu, { IMenu, MenuItem } from '@/components/Menu';
import { IOperationData } from '@/components/OperationTableModal';
import { TreeNodeType, DatabaseTypeCode } from '@/utils/constants';
import { ITreeConfigItem, ITreeConfig, treeConfig } from '@/components/Tree/treeConfig';
import { ITreeNode } from '@/types';
import { DatabaseContext } from '@/context/database';
import connectionServer from '@/service/connection';
import mysqlServer from '@/service/mysql';
import { OperationColumn } from '../treeConfig';
import { dataSourceFormConfigs } from '@/config/dataSource';
import { IDataSourceForm } from '@/config/types';


type MenuItem = Required<MenuProps>['items'][number];

export type IProps = {
  className?: string;
  setIsLoading: (value: boolean) => void;
  data: ITreeNode;
  setTreeData: Function;
}

export interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

function TreeNodeRightClick(props: IProps) {
  const { className, setTreeData, data, setIsLoading } = props;
  const { setCreateConsoleDialog, setOperationDataDialog, setNeedRefreshNodeTree, setEditDataSourceData } = useContext(DatabaseContext);
  const [verifyDialog, setVerifyDialog] = useState<boolean>();
  const [verifyTableName, setVerifyTableName] = useState<string>('');
  const treeNodeConfig: ITreeConfigItem = treeConfig[data.nodeType]
  const { getChildren, operationColumn } = treeNodeConfig;
  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IDataSourceForm) => {
    return t.type === data.dataType
  })!
  const OperationColumnConfig: { [key in OperationColumn]: (data: ITreeNode) => IOperationColumnConfigItem } = {
    [OperationColumn.REFRESH]: (data) => {
      return {
        text: '刷新',
        icon: '\uec08',
        handle: () => {
          refresh();
        }
      }
    },
    [OperationColumn.ExportDDL]: (data) => {
      return {
        text: '导出ddl',
        icon: '\ue613',
        handle: () => {
          const operationData: IOperationData = {
            type: 'export',
            nodeData: data
          }
          if (operationData.type === 'export') {
            setOperationDataDialog(operationData);
          }
        }
      }
    },
    [OperationColumn.ShiftOut]: (data) => {
      return {
        text: '移出',
        icon: '\ue62a',
        handle: () => {
          connectionServer.remove({ id: +data.key }).then(res => {
            treeConfig[TreeNodeType.DATASOURCES]?.getChildren!({} as any).then(res => {
              setTreeData(res);
            })
          })
        }
      }
    },
    [OperationColumn.CreateTable]: (data) => {
      return {
        text: '新建表',
        icon: '\ue6b6',
        handle: () => {
          const operationData: IOperationData = {
            type: 'new',
            nodeData: data
          }
          setOperationDataDialog(operationData)
        }
      }
    },
    [OperationColumn.CreateConsole]: (data) => {
      return {
        text: '新建查询',
        icon: '\ue619',
        handle: () => {
          console.log(data)
          setCreateConsoleDialog({
            dataSourceId: data.dataSourceId!,
            dataSourceName: data.dataSourceName!,
            databaseName: data.databaseName!,
            schemaName: data.schemaName!,
            databaseType: data.dataType! as DatabaseTypeCode
          })
        }
      }
    },
    [OperationColumn.DeleteTable]: (data) => {
      return {
        text: '删除表',
        icon: '\ue6a7',
        handle: () => {
          setCreateConsoleDialog({
            dataSourceId: data.dataSourceId!,
            dataSourceName: data.dataSourceName!,
            databaseName: data.databaseName!,
            schemaName: data.schemaName!,
            databaseType: data.dataType! as DatabaseTypeCode
          })
        }
      }
    },
    [OperationColumn.EditSource]: (data) => {
      return {
        text: '编辑数据源',
        icon: '\ue623',
        handle: () => {
          setEditDataSourceData({
            dataType: data.dataType as any,
            id: +data.key
          })
        }
      }
    }
  }

  function refresh() {
    data.children = [];
    setIsLoading(true);
    getChildren?.(data).then(res => {
      setTimeout(() => {
        data.children = res;
        setIsLoading(false);
      }, 200);
    })
  }

  function closeMenu() {
    // TODO: 关闭下拉弹窗 有木有更好的方法
    const customDropdown: any = document.getElementsByClassName('custom-dropdown');
    for (let i = 0; i < customDropdown.length; i++) {
      customDropdown[i].classList.add('custom-dropdown-hidden')
    }
  }

  function handleOk() {
    let p = {
      tableName: verifyTableName,
      dataSourceId: data.dataSourceId!,
      databaseName: data.databaseName!
    }
    if (verifyTableName === data.tableName) {
      mysqlServer.deleteTable(p).then(res => {
        setVerifyDialog(false);
        setNeedRefreshNodeTree({
          databaseName: data.databaseName,
          dataSourceId: data.dataSourceId,
          nodeType: TreeNodeType.TABLES
        })
      })
    } else {
      message.error('输入的表名与要删除的表名不一致，请再次确认')
    }
  }

  function excludeSomeOperation() {
    const excludes = dataSourceFormConfig.baseInfo.excludes
    const newOperationColumn: OperationColumn[] = []
    operationColumn?.map((item: OperationColumn) => {
      let flag = false
      excludes?.map(t => {
        if (item === t) {
          flag = true
        }
      })
      if (!flag) {
        newOperationColumn.push(item)
      }
    })
    return newOperationColumn
  }

  return <>
    <div className={styles.menuBox}>
      <Menu>
        {
          excludeSomeOperation()?.map((item, index) => {
            const concrete = OperationColumnConfig[item](data);
            return <MenuItem key={index} onClick={() => { closeMenu(); concrete.handle(); }}>
              {concrete.text}
              <Iconfont code={concrete.icon} />
            </MenuItem>
          })
        }
      </Menu>
    </div>
    <Modal
      maskClosable={false}
      title="删除确认"
      open={verifyDialog}
      onOk={handleOk}
      width={400}
      onCancel={(() => { setVerifyDialog(false) })}>
      <Input placeholder='请输入你要删除的表名' value={verifyTableName} onChange={(e) => { setVerifyTableName(e.target.value) }}></Input>
    </Modal>
  </>
}

export default memo(TreeNodeRightClick)