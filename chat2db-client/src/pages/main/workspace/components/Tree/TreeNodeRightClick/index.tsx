import React, { memo, useMemo, useRef, useState } from 'react';
import { message, Modal, Input, Dropdown, notification } from 'antd';
import classnames from 'classnames';
import i18n from '@/i18n';
import styles from './index.less';
import Iconfont from '@/components/Iconfont';
import { TreeNodeType, CreateTabIntroType, WorkspaceTabType, OperationColumn } from '@/constants';
import { ITreeConfigItem, treeConfig } from '@/pages/main/workspace/components/Tree/treeConfig';
import { ITreeNode } from '@/typings';
import connectionServer from '@/service/connection';
import mysqlServer from '@/service/sql';
import { dataSourceFormConfigs } from '@/components/ConnectionEdit/config/dataSource';
import { IConnectionConfig } from '@/components/ConnectionEdit/config/types';
import { ICurWorkspaceParams } from '@/models/workspace';
import MonacoEditor, { IExportRefFunction } from '@/components/Console/MonacoEditor';

export type IProps = {
  className?: string;
  setIsLoading: (value: boolean) => void;
  data: ITreeNode;
  dispatch: any;
  curWorkspaceParams: ICurWorkspaceParams;
  children?: any;
  trigger?: any;
};

export interface IOperationColumnConfigItem {
  text: string;
  icon: string;
  handle: () => void;
}

function TreeNodeRightClick(props: IProps) {
  const { className, data, setIsLoading, dispatch, curWorkspaceParams, children } = props;
  const [verifyDialog, setVerifyDialog] = useState<boolean>();
  const [verifyTableName, setVerifyTableName] = useState<string>('');
  const [modalApi, modelDom] = Modal.useModal();
  const [notificationApi, notificationDom] = notification.useNotification();
  const treeNodeConfig: ITreeConfigItem = treeConfig[data.treeNodeType];
  const { getChildren, operationColumn } = treeNodeConfig;
  const [monacoVerifyDialog, setMonacoVerifyDialog] = useState(false);
  const dataSourceFormConfig = dataSourceFormConfigs.find((t: IConnectionConfig) => {
    return t.type === data.extraParams?.databaseType;
  })!;
  const monacoEditorRef = useRef<IExportRefFunction>(null);
  const OperationColumnConfig: { [key in OperationColumn]: (data: ITreeNode) => IOperationColumnConfigItem } = {
    [OperationColumn.Refresh]: () => {
      return {
        text: i18n('common.button.refresh'),
        icon: '\uec08',
        handle: () => {
          refresh();
        },
      };
    },
    [OperationColumn.EditTableData]: () => {
      return {
        text: i18n('workspace.menu.editTableData'),
        icon: '\ue7b5',
        handle: () => {
          openEditTableData();
        },
      };
    },
    [OperationColumn.ViewDDL]: () => {
      return {
        text: i18n('workspace.menu.ViewDDL'),
        icon: '\ue665',
        handle: () => {
          mysqlServer
            .exportCreateTableSql({
              ...curWorkspaceParams,
              tableName: data.key,
            } as any)
            .then((res) => {
              setMonacoVerifyDialog(true);
              setTimeout(() => {
                monacoEditorRef.current?.setValue(res, 'cover');
              }, 0);
            });
        },
      };
    },
    [OperationColumn.ShiftOut]: (data) => {
      return {
        text: '移出',
        icon: '\ue62a',
        handle: () => {
          connectionServer.remove({ id: +data.key }).then(() => {
            treeConfig[TreeNodeType.DATA_SOURCES]?.getChildren!({} as any).then(() => {
              // setTreeData(res);
            });
          });
        },
      };
    },
    [OperationColumn.CreateTable]: (data) => {
      return {
        text: '新建表',
        icon: '\ue6b6',
        handle: () => {
          const operationData = {
            type: 'new',
            nodeData: data,
          };
          // setOperationDataDialog(operationData)
        },
      };
    },
    [OperationColumn.CreateConsole]: () => {
      return {
        text: '新建查询',
        icon: '\ue619',
        handle: () => {},
      };
    },
    [OperationColumn.DeleteTable]: () => {
      return {
        text: i18n('workspace.menu.deleteTable'),
        icon: '\ue6a7',
        handle: () => {
          setVerifyDialog(true);
        },
      };
    },
    [OperationColumn.EditTable]: (data) => {
      return {
        text: i18n('workspace.menu.editTable'),
        icon: '\ue602',
        handle: () => {
          dispatch({
            type: 'workspace/setCreateTabIntro',
            payload: {
              type: CreateTabIntroType.EditorTable,
              workspaceTabType: WorkspaceTabType.EditTable,
              treeNodeData: {
                ...data,
                name: data.key,
              },
            },
          });
        },
      };
    },
    [OperationColumn.EditSource]: () => {
      return {
        text: '编辑数据源',
        icon: '\ue623',
        handle: () => {
          // setEditDataSourceData({
          //   dataType: data.dataType as any,
          //   id: +data.key
          // })
        },
      };
    },
    [OperationColumn.Top]: (_data) => {
      return {
        text: _data.pinned ? i18n('workspace.menu.unPin') : i18n('workspace.menu.pin'),
        icon: _data.pinned ? '\ue61d' : '\ue627',
        handle: handleTop,
      };
    },
    [OperationColumn.CopyName]: (_data) => {
      return {
        text: i18n('common.button.copy'),
        icon: '\uec7a',
        handle: () => {
          navigator.clipboard.writeText(_data.key.toString());
        },
      };
    },
  };

  function handleTop() {
    const api = data.pinned ? 'deleteTablePin' : 'addTablePin';
    mysqlServer[api]({
      ...curWorkspaceParams,
      tableName: data.key,
    } as any).then(() => {
      dispatch({
        type: 'workspace/fetchGetCurTableList',
        payload: {
          ...curWorkspaceParams,
          extraParams: curWorkspaceParams,
          refresh: true,
        },
        callback: () => {
          message.success(i18n('common.text.submittedSuccessfully'));
        },
      });
    });
  }

  function refresh() {
    setIsLoading(true);
    const params = {
      ...data.extraParams,
      extraParams:{
        ...data.extraParams
      },
      refresh: true,
    }
    getChildren?.(params).then((res) => {
      data.children = res as any;
    })
    .finally(() => {
      setIsLoading(false);
    })
  }

  function openEditTableData() {
    const payload = {
      type: CreateTabIntroType.EditTableData,
      workspaceTabType: WorkspaceTabType.EditTableData,
      treeNodeData: {
        ...data,
        name: data.key,
      },
    };
    dispatch({
      type: 'workspace/setCreateTabIntro',
      payload,
    });
  }

  function handleOk() {
    if (verifyTableName === data.key) {
      const p: any = {
        ...data.extraParams,
        tableName: data.key,
      };
      mysqlServer.deleteTable(p).then(() => {
        // notificationApi.success(
        //   {
        //     message: i18n('common.text.successfullyDelete'),
        //   }
        // )
        message.success(i18n('common.text.successfullyDelete'));
        dispatch({
          type: 'workspace/fetchGetCurTableList',
          payload: {
            ...curWorkspaceParams,
            extraParams: curWorkspaceParams,
          },
          callback: () => {
            setVerifyDialog(false);
            setVerifyTableName('');
          },
        });
      });
    } else {
      message.error(i18n('workspace.tips.affirmDeleteTable'));
    }
  }

  function excludeSomeOperation() {
    const excludes = dataSourceFormConfig.baseInfo.excludes;
    const newOperationColumn: OperationColumn[] = [];
    operationColumn?.map((item: OperationColumn) => {
      let flag = false;
      excludes?.map((t) => {
        if (item === t) {
          flag = true;
        }
      });
      if (!flag) {
        newOperationColumn.push(item);
      }
    });
    return newOperationColumn;
  }

  const dropdowns = useMemo(() => {
    if (dataSourceFormConfig) {
      return excludeSomeOperation().map((t, i) => {
        const concrete = OperationColumnConfig[t](data);
        return {
          key: i,
          label: (
            <div className={classnames(styles.operationItem)}>
              <Iconfont className={styles.operationIcon} code={concrete?.icon} />
              <div className={styles.operationTitle}>{concrete.text}</div>
            </div>
          ),
          onClick: concrete.handle,
        };
      });
    }
    return [];
  }, [dataSourceFormConfig]);

  return (
    <>
      {modelDom}
      {notificationDom}
      <Dropdown
        overlayStyle={{
          zIndex: 1080,
        }}
        className={className}
        menu={{
          items: dropdowns,
        }}
        trigger={props.trigger || ['hover']}
      >
        {children || (
          <div>
            <Iconfont code="&#xe601;" />
          </div>
        )}
      </Dropdown>
      <Modal
        maskClosable={false}
        title={`${i18n('workspace.menu.deleteTable')}-${data.key}`}
        open={verifyDialog}
        onOk={handleOk}
        width={400}
        onCancel={() => {
          setVerifyDialog(false);
        }}
      >
        <Input
          placeholder={i18n('workspace.menu.deleteTablePlaceHolder')}
          value={verifyTableName}
          onChange={(e) => {
            setVerifyTableName(e.target.value);
          }}
        />
      </Modal>
      {/* 这里后续肯定是要提出去的 */}
      {monacoVerifyDialog && (
        <Modal
          maskClosable={false}
          title={`${data.key}-DDL`}
          open={monacoVerifyDialog}
          width="650px"
          onCancel={() => {
            setMonacoVerifyDialog(false);
          }}
          footer={false}
        >
          <div className={styles.monacoEditorBox}>
            <MonacoEditor id="edit-dialog" ref={monacoEditorRef} />
          </div>
        </Modal>
      )}
    </>
  );
}

export default memo(TreeNodeRightClick);
