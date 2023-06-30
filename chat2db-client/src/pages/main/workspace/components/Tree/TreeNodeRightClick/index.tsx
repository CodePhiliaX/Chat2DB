import React, { memo, useContext, useMemo, useState } from 'react';
import i18n from '@/i18n';
import classnames from 'classnames';
import styles from './index.less';
import Iconfont from '@/components/Iconfont';
import { MenuProps, message, Modal, Input, Dropdown, notification } from 'antd';
import { ExclamationCircleFilled } from '@ant-design/icons';
// import { Menu } from 'antd';
// import Menu, { IMenu, MenuItem } from '@/components/Menu';
// import { IOperationData } from '@/components/OperationTableModal';
import { TreeNodeType, DatabaseTypeCode } from '@/constants';
import { ITreeConfigItem, ITreeConfig, treeConfig } from '@/pages/main/workspace/components/Tree/treeConfig';
import { ITreeNode } from '@/typings';
// import { DatabaseContext } from '@/context/database';
import connectionServer from '@/service/connection';
import historyService from '@/service/history';
import mysqlServer from '@/service/sql';
import { OperationColumn } from '../treeConfig';
import { dataSourceFormConfigs } from '@/components/CreateConnection/config/dataSource';
import { IConnectionConfig } from '@/components/CreateConnection/config/types';
import { IWorkspaceModelType } from '@/models/workspace';
import EditDialog from '@/components/EditDialog';
import { ConsoleStatus, ConsoleOpenedStatus } from '@/constants'

type MenuItem = Required<MenuProps>['items'][number];

export type IProps = {
  className?: string;
  setIsLoading: (value: boolean) => void;
  data: ITreeNode;
  dispatch: any;
  workspaceModel: IWorkspaceModelType['state']
}

export interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

function TreeNodeRightClick(props: IProps) {
  const { className, data, setIsLoading, dispatch, workspaceModel } = props;
  // const { setCreateConsoleDialog, setOperationDataDialog, setNeedRefreshNodeTree, setEditDataSourceData } = useContext(DatabaseContext);
  const [verifyDialog, setVerifyDialog] = useState<boolean>();
  const [verifyTableName, setVerifyTableName] = useState<string>('');
  const [modalApi, modelDom] = Modal.useModal();
  const [notificationApi, notificationDom] = notification.useNotification();
  const treeNodeConfig: ITreeConfigItem = treeConfig[data.treeNodeType]
  const { getChildren, operationColumn } = treeNodeConfig;
  const { curWorkspaceParams } = workspaceModel;
  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
    return t.type === data.extraParams?.databaseType
  })!
  const OperationColumnConfig: { [key in OperationColumn]: (data: ITreeNode) => IOperationColumnConfigItem } = {
    [OperationColumn.Refresh]: (data) => {
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

        }
      }
    },
    [OperationColumn.ShiftOut]: (data) => {
      return {
        text: '移出',
        icon: '\ue62a',
        handle: () => {
          connectionServer.remove({ id: +data.key }).then(res => {
            treeConfig[TreeNodeType.DATA_SOURCES]?.getChildren!({} as any).then(res => {
              // setTreeData(res);
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
          // setOperationDataDialog(operationData)
        }
      }
    },
    [OperationColumn.CreateConsole]: (data) => {
      return {
        text: '新建查询',
        icon: '\ue619',
        handle: () => {

        }
      }
    },
    [OperationColumn.DeleteTable]: (data) => {
      return {
        text: '删除表',
        icon: '\ue6a7',
        handle: () => {
          setVerifyDialog(true);
          // modalApi.confirm({
          //   title: i18n('common.tips.deleteTable'),
          //   icon: <ExclamationCircleFilled />,
          //   content: `${i18n('common.text.tableName')}：${data.name}`,
          //   okText: i18n('common.button.delete'),
          //   okType: 'danger',
          //   cancelText: i18n('common.button.cancel'),
          //   onOk() {
          //   },
          // });
        }
      }
    },
    [OperationColumn.EditSource]: (data) => {
      return {
        text: '编辑数据源',
        icon: '\ue623',
        handle: () => {
          // setEditDataSourceData({
          //   dataType: data.dataType as any,
          //   id: +data.key
          // })
        }
      }
    },
    [OperationColumn.Top]: (data) => {
      return {
        text: data.pinned ? '取消置顶' : '置顶',
        icon: data.pinned ? '\ue61d' : '\ue627',
        handle: () => {
          handelTop()
        }
      }
    },
  }

  function handelTop() {
    const api = data.pinned ? 'deleteTablePin' : 'addTablePin'
    mysqlServer[api]({
      ...curWorkspaceParams,
      tableName: data.name
    } as any).then(res => {
      dispatch({
        type: 'workspace/fetchGetCurTableList',
        payload: {
          ...curWorkspaceParams,
          extraParams: curWorkspaceParams,
        }
      })
    })
  }

  function refresh() {
    data.children = [];
    setIsLoading(true);
    getChildren?.({
      ...data,
      ...data.extraParams
    }).then(res => {
      setTimeout(() => {
        data.children = res;
        setIsLoading(false);
      }, 200);
    })
  }

  function handleOk() {
    if (verifyTableName === data.name) {
      let p: any = {
        ...data.extraParams,
        tableName: data.name,
      }
      mysqlServer.deleteTable(p).then(res => {
        notificationApi.success(
          {
            message: '删除成功',
          }
        )
        dispatch({
          type: 'workspace/fetchGetCurTableList',
          payload: {
            ...curWorkspaceParams,
            extraParams: curWorkspaceParams,
          },
          callback: () => {
            setVerifyDialog(false);
            setVerifyTableName('');
          }
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

  const dropdowns = useMemo(() => {
    if (dataSourceFormConfig) {
      return excludeSomeOperation().map((t, i) => {
        const concrete = OperationColumnConfig[t](data);
        return {
          key: i,
          label: <div className={styles.operationItem}>
            <Iconfont className={styles.operationIcon} code={concrete.icon} />
            <div className={styles.operationTitle}>
              {concrete.text}
            </div>
          </div>,
          onClick: concrete.handle
        }
      });
    }
    return []
  }, [dataSourceFormConfig])

  return <>
    {modelDom}
    {notificationDom}
    {
      !!dropdowns.length &&
      <Dropdown
        className={className}
        menu={{
          items: dropdowns,
        }}
      >
        <div>
          <Iconfont code="&#xe601;"></Iconfont>
        </div>
      </Dropdown>
    }
    <Modal
      maskClosable={false}
      title={`删除表-${data.name}`}
      open={verifyDialog}
      onOk={handleOk}
      width={400}
      onCancel={(() => { setVerifyDialog(false) })}>
      <Input placeholder='请输入你要删除的表名' value={verifyTableName} onChange={(e) => { setVerifyTableName(e.target.value) }}></Input>
    </Modal>
  </>
}

export default memo(TreeNodeRightClick)