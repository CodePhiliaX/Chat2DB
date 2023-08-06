import React, { memo, useContext, useMemo, useState, useRef } from 'react';
import i18n from '@/i18n';
import classnames from 'classnames';
import styles from './index.less';
import Iconfont from '@/components/Iconfont';
import { MenuProps, message, Modal, Input, Dropdown, notification } from 'antd';
import { TreeNodeType, DatabaseTypeCode } from '@/constants';
import { ITreeConfigItem, ITreeConfig, treeConfig } from '@/pages/main/workspace/components/Tree/treeConfig';
import { ITreeNode } from '@/typings';
import connectionServer from '@/service/connection';
import historyService from '@/service/history';
import mysqlServer from '@/service/sql';
import { OperationColumn } from '../treeConfig';
import { dataSourceFormConfigs } from '@/components/CreateConnection/config/dataSource';
import { IConnectionConfig } from '@/components/CreateConnection/config/types';
import { IWorkspaceModelType } from '@/models/workspace';
import EditDialog from '@/components/EditDialog';
import { ConsoleStatus, ConsoleOpenedStatus } from '@/constants';
import MonacoEditor, { IExportRefFunction, IRangeType } from '@/components/Console/MonacoEditor';

type MenuItem = Required<MenuProps>['items'][number];

export type IProps = {
  className?: string;
  setIsLoading: (value: boolean) => void;
  data: ITreeNode;
  dispatch: any;
  workspaceModel: IWorkspaceModelType['state'];
}

export interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

function TreeNodeRightClick(props: IProps) {
  const { className, data, setIsLoading, dispatch, workspaceModel } = props;
  const [verifyDialog, setVerifyDialog] = useState<boolean>();
  const [verifyTableName, setVerifyTableName] = useState<string>('');
  const [modalApi, modelDom] = Modal.useModal();
  const [notificationApi, notificationDom] = notification.useNotification();
  const treeNodeConfig: ITreeConfigItem = treeConfig[data.treeNodeType]
  const { getChildren, operationColumn } = treeNodeConfig;
  const { curWorkspaceParams } = workspaceModel;
  const [monacoVerifyDialog, setMonacoVerifyDialog] = useState(false);
  const [monacoDefaultValue, setMonacoDefaultValue] = useState('');
  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
    return t.type === data.extraParams?.databaseType
  })!
  const OperationColumnConfig: { [key in OperationColumn]: (data: ITreeNode) => IOperationColumnConfigItem } = {
    [OperationColumn.Refresh]: (data) => {
      return {
        text: i18n('common.button.refresh'),
        icon: '\uec08',
        handle: () => {
          refresh();
        }
      }
    },
    [OperationColumn.ExportDDL]: (data) => {
      return {
        text: i18n('workspace.menu.exportDDL'),
        icon: '\ue613',
        handle: () => {
          mysqlServer.exportCreateTableSql({
            ...curWorkspaceParams,
            tableName: data.key
          } as any).then(res => {
            setMonacoDefaultValue(res);
            setMonacoVerifyDialog(true);
          })
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
          const operationData = {
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
        text: i18n('workspace.menu.deleteTable'),
        icon: '\ue6a7',
        handle: () => {
          setVerifyDialog(true);
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
        text: data.pinned ? i18n('workspace.menu.unPin') : i18n('workspace.menu.pin'),
        icon: data.pinned ? '\ue61d' : '\ue627',
        handle: handelTop
      }
    },
  }

  function handelTop() {
    const api = data.pinned ? 'deleteTablePin' : 'addTablePin'
    mysqlServer[api]({
      ...curWorkspaceParams,
      tableName: data.key
    } as any).then(res => {
      dispatch({
        type: 'workspace/fetchGetCurTableList',
        payload: {
          ...curWorkspaceParams,
          extraParams: curWorkspaceParams,
        },
        callback: () => {
          message.success(i18n('common.text.submittedSuccessfully'))
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
      data.children = res;
      setIsLoading(false);
    })
  }

  function handleOk() {
    if (verifyTableName === data.key) {
      let p: any = {
        ...data.extraParams,
        tableName: data.key,
      }
      mysqlServer.deleteTable(p).then(res => {
        // notificationApi.success(
        //   {
        //     message: i18n('common.text.successfullyDelete'),
        //   }
        // )
        message.success(i18n('common.text.successfullyDelete'))
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
      message.error(i18n('workspace.tips.affirmDeleteTable'))
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
            <Iconfont className={styles.operationIcon} code={concrete?.icon} />
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
      title={`${i18n('workspace.menu.deleteTable')}-${data.key}`}
      open={verifyDialog}
      onOk={handleOk}
      width={400}
      onCancel={(() => { setVerifyDialog(false) })}
    >
      <Input placeholder={i18n('workspace.menu.deleteTablePlaceHolder')} value={verifyTableName} onChange={(e) => { setVerifyTableName(e.target.value) }}></Input>
    </Modal>
    {/* 这里后续肯定是要提出去的 */}
    {
      monacoVerifyDialog &&
      <Modal
        maskClosable={false}
        title={`${data.key}-DDL`}
        open={monacoVerifyDialog}
        width="600px"
        onCancel={(() => { setMonacoVerifyDialog(false) })}
        footer={false}
      >
        <div className={styles.monacoEditorBox}>
          <MonacoEditor
            id='edit-dialog'
            appendValue={{
              text: monacoDefaultValue,
              range: 'reset'
            }}
          ></MonacoEditor>
        </div>
      </Modal>
    }
  </>
}

export default memo(TreeNodeRightClick)