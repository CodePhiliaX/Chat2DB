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
import Tree from '../Tree';
import { TreeNodeType, ConsoleStatus, ConsoleOpenedStatus } from '@/constants';
import { IConsole, ITreeNode, ICreateConsole } from '@/typings';
import styles from './index.less';
import { approximateTreeNode, approximateList } from '@/utils';
import historyService from '@/service/history';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'],
  dispatch: any;
  cascaderOptions: any;
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

const WorkspaceLeft = memo<IProps>(function (props) {
  const { className, workspaceModel, dispatch, cascaderOptions } = props;
  const { curWorkspaceParams } = workspaceModel;


  function getConsoleList() {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: p,
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setOpenConsoleList',
          payload: res.data,
        })
      }
    })
  }

  const addConsole = (params?: ICreateConsole) => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams
    let p = {
      name: `new console`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    }
    historyService.saveConsole(params || p).then(res => {
      getConsoleList();
    })
  }

  function createConsole() {
    addConsole()
  }

  return (
    <div className={classnames(styles.box, className)}>
      <div className={styles.header}>
        <RenderSelectDatabase cascaderOptions={cascaderOptions} />
      </div>
      <RenderSaveBox></RenderSaveBox>
      <Divider className={styles.divider} />
      <RenderTableBox />
      <div className={styles.createButtonBox}>
        <Button className={styles.createButton} type="primary" onClick={createConsole}>
          <Iconfont code="&#xe63a;" />
          {i18n('common.button.createConsole')}
        </Button>
      </div>
    </div>
  );
});

interface Option {
  value: string;
  label: string;
  children?: Option[];
}

interface IProps {
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

const RenderSelectDatabase = dvaModel(function (props: IProps) {
  const { connectionModel, workspaceModel, dispatch, cascaderOptions } = props;
  const { curWorkspaceParams } = workspaceModel;
  const { curConnection } = connectionModel;
  const [currentSelectedName, setCurrentSelectedName] = useState('');
  const [cascaderLoading, setCascaderLoading] = useState(false)


  useEffect(() => {
    if (curWorkspaceParams) {
      const { databaseName, schemaName, databaseSourceName } = curWorkspaceParams;
      const currentSelectedArr = [databaseSourceName, databaseName, schemaName].filter((t) => t);
      setCurrentSelectedName(currentSelectedArr.join('/'));
    }
  }, [curWorkspaceParams]);

  const onChange: any = (valueArr: any, selectedOptions: any) => {
    let labelArr: string[] = [];
    labelArr = selectedOptions.map((t: any) => {
      return t.label;
    });

    const curWorkspaceParams = {
      dataSourceId: curConnection?.id,
      databaseSourceName: curConnection?.alias,
      databaseName: labelArr[0],
      schemaName: labelArr[1],
      databaseType: curConnection?.type,
    };

    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload: curWorkspaceParams,
    });
  };

  const dropdownRender = (menus: React.ReactNode) => <div>{menus}</div>;

  function handleRefresh() {
    setCascaderLoading(true)
    dispatch({
      type: 'workspace/fetchDatabaseAndSchema',
      payload: {
        dataSourceId: curConnection?.id,
        refresh: true
      },
      callback: () => {
        setCascaderLoading(false)
      }
    });
  }

  return (
    <div className={styles.selectDatabaseBox}>
      <Cascader
        popupClassName={styles.cascaderPopup}
        options={cascaderOptions}
        onChange={onChange}
        bordered={false}
        dropdownRender={dropdownRender}
      >
        <div className={styles.currentDatabase}>
          <div className={styles.name}>
            {currentSelectedName || <span style={{ opacity: 0.8 }}>{i18n('workspace.cascader.placeholder')}</span>}{' '}
          </div>
          <Iconfont code="&#xe608;" />
        </div>
      </Cascader>
      <div className={styles.otherOperations}>
        <div className={classnames(styles.refreshIconBox, styles.iconBox)} onClick={handleRefresh}>
          {cascaderLoading ? <Spin /> : <Iconfont code="&#xec08;" />}
        </div>
      </div>
    </div>
  );
});

const RenderTableBox = dvaModel(function (props: any) {
  const { workspaceModel, dispatch, tableLoading } = props;
  const { curWorkspaceParams, curTableList } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedTableList, setSearchedTableList] = useState<ITreeNode[] | undefined>();

  useEffect(() => {
    if (curWorkspaceParams?.dataSourceId) {
      dispatch({
        type: 'workspace/fetchGetCurTableList',
        payload: {
          ...curWorkspaceParams,
          extraParams: curWorkspaceParams,
        }
      })
    }
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
      setSearchedTableList(undefined);
    }
  }

  function onChange(value: string) {
    setSearchedTableList(approximateTreeNode(curTableList, value))
  }

  function refreshTableList() {
    if (curWorkspaceParams?.dataSourceId) {
      dispatch({
        type: 'workspace/fetchGetCurTableList',
        payload: {
          ...curWorkspaceParams,
          refresh: true,
          extraParams: curWorkspaceParams,
        }
      })
    }
  }

  return (
    <div className={styles.tableModule}>
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
              <div className={styles.modelName}>{i18n('common.text.table')}</div>
              <div className={styles.iconBox} >
                <div className={styles.refreshIcon} onClick={() => refreshTableList()}>
                  <Iconfont code="&#xec08;" />
                </div>
                <div className={styles.searchIcon} onClick={() => openSearch()}>
                  <Iconfont code="&#xe600;" />
                </div>
              </div>
            </div>
        }
      </div>
      <LoadingContent className={styles.treeBox} isLoading={tableLoading}>
        <Tree className={styles.tree} initialData={searchedTableList || curTableList}></Tree>
      </LoadingContent>
    </div>
  );
});

const RenderSaveBox = dvaModel(function (props: any) {
  const { workspaceModel, dispatch } = props;
  const { curWorkspaceParams, consoleList } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedList, setSearchedList] = useState<ITreeNode[] | undefined>();

  useEffect(() => {
    if (!curWorkspaceParams.dataSourceId) {
      return
    }
    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: {
        pageNo: 1,
        pageSize: 999,
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
        type: 'workspace/fetchGetSavedConsole',
        payload: {
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
          status: ConsoleStatus.RELEASE,
          orderByDesc: true,
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

export default dvaModel(WorkspaceLeft);
