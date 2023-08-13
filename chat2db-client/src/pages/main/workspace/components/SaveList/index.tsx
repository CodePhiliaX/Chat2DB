import React, { memo, useState, useEffect, useRef, useContext, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Cascader, Divider, Input, Dropdown, Button, Spin } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import historyServer from '@/service/history';
import { ConsoleStatus, ConsoleOpenedStatus } from '@/constants';
import { IConsole, ITreeNode } from '@/typings';
import styles from './index.less';
import { approximateList } from '@/utils';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'],
  dispatch: any;
  tableLoading: boolean;
  databaseLoading: boolean;
}

const dvaModel = connect(
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    tableLoading: loading.effects['workspace/fetchGetCurTableList'],
    databaseLoading: loading.effects['workspace/fetchDatabaseAndSchema'],
  }),
);

const SaveList = dvaModel(function (props: any) {
  const { workspaceModel, dispatch } = props;
  const { curWorkspaceParams, consoleList } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedList, setSearchedList] = useState<ITreeNode[] | undefined>();

  useEffect(() => {
    if (!curWorkspaceParams.dataSourceId || !(curWorkspaceParams?.databaseName || curWorkspaceParams?.schemaName)) {
      return
    }
    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: {
        pageNo: 1,
        pageSize: 999,
        orderByDesc: true,
        status: ConsoleStatus.RELEASE,
        ...curWorkspaceParams,
      },
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setConsoleList',
          payload: res.data,
        })
      }
    });
  }, [curWorkspaceParams]);


  useEffect(() => {
    if (searching) {
      inputRef.current!.focus({
        cursor: 'start',
      });
    }
  }, [searching])


  function openSearch() {
    setSearching(true);
  }

  function onBlur() {
    if (!inputRef.current.input.value) {
      setSearching(false);
      setSearchedList(undefined);
    }
  }

  function onChange(value: string) {
    setSearchedList(approximateList(consoleList, value,))
  }

  function openConsole(data: IConsole) {

    let p: any = {
      id: data.id,
      tabOpened: ConsoleOpenedStatus.IS_OPEN
    };
    historyServer.updateSavedConsole(p).then((res) => {

      dispatch({
        type: 'workspace/setCurConsoleId',
        payload: data.id,
      });

      dispatch({
        type: 'workspace/fetchGetSavedConsole',
        payload: {
          orderByDesc: false,
          tabOpened: ConsoleOpenedStatus.IS_OPEN,
          ...curWorkspaceParams
        },
        callback: (res: any) => {
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: res.data,
          })
        }
      })
    });
  }

  function deleteSaved(data: IConsole) {
    let p: any = {
      id: data.id,
    };
    historyServer.deleteSavedConsole(p).then((res) => {
      dispatch({
        type: 'workspace/fetchGetSavedConsole',
        payload: {
          orderByDesc: true,
          tabOpened: ConsoleOpenedStatus.IS_OPEN,
          ...curWorkspaceParams
        },
        callback: (res: any) => {
          dispatch({
            type: 'workspace/setOpenConsoleList',
            payload: res.data,
          })
        }
      })
      dispatch({
        type: 'workspace/fetchGetSavedConsole',
        payload: {
          orderByDesc: true,
          status: ConsoleStatus.RELEASE,
          ...curWorkspaceParams
        },
        callback: (res: any) => {
          dispatch({
            type: 'workspace/setConsoleList',
            payload: res.data,
          })
        }
      })
    });
  }

  return (
    <div className={styles.saveModule}>
      <div className={styles.leftModuleTitle}>
        {
          searching ?
            <div className={styles.leftModuleTitleSearch}>
              <Input
                ref={inputRef}
                size="small"
                placeholder={i18n('common.text.search')}
                prefix={<Iconfont code="&#xe600;" />}
                onBlur={onBlur}
                onChange={(e) => onChange(e.target.value)}
                allowClear
              />
            </div>
            :
            <div className={styles.leftModuleTitleText}>
              <div className={styles.modelName}>{i18n('workspace.title.saved')}</div>
              <div className={styles.iconBox} >
                {/* <div className={styles.refreshIcon} onClick={() => refreshTableList()}>
                  <Iconfont code="&#xec08;" />
                </div> */}
                <div className={styles.searchIcon} onClick={() => openSearch()}>
                  <Iconfont code="&#xe600;" />
                </div>
              </div>
            </div>
        }
      </div>
      <div className={styles.saveBoxList}>
        <LoadingContent data={consoleList} handleEmpty>
          {(searchedList || consoleList)?.map((t: IConsole) => {
            return (
              <div
                onDoubleClick={() => {
                  openConsole(t)
                }}
                key={t.id}
                className={styles.saveItem}
              >
                <div className={styles.saveItemText}>
                  <span dangerouslySetInnerHTML={{ __html: t.name }} />
                </div>
                <Dropdown
                  menu={{
                    items: [
                      {
                        key: 'open',
                        label: i18n('common.button.open'),
                        onClick: () => {
                          openConsole(t)
                        }
                      },
                      {
                        key: 'delete',
                        label: i18n('common.button.delete'),
                        onClick: () => {
                          deleteSaved(t)
                        },
                      },
                    ],
                  }}
                >
                  <div className={styles.moreButton}>
                    <Iconfont code="&#xe601;"></Iconfont>
                  </div>
                </Dropdown>

              </div>
            );
          })}
        </LoadingContent>
      </div >
    </div >
  );
});

export default dvaModel(SaveList);
