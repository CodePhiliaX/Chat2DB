import React, { memo, useState, useEffect, useRef, useContext, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Cascader, Divider, Input, Dropdown, Button } from 'antd';
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
}

const dvaModel = connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);

const WorkspaceLeft = memo<IProps>(function (props) {
  const { className, workspaceModel, dispatch } = props;
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
        <RenderSelectDatabase />
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

function handleDatabaseAndSchema(databaseAndSchema: IWorkspaceModelType['state']['databaseAndSchema']) {
  let newCascaderOptions: Option[] = [];
  if (databaseAndSchema?.databases) {
    newCascaderOptions = (databaseAndSchema?.databases || []).map((t) => {
      let schemasList: Option[] = [];
      if (t.schemas) {
        schemasList = t.schemas.map((t) => {
          return {
            value: t.name,
            label: t.name,
          };
        });
      }
      return {
        value: t.name,
        label: t.name,
        children: schemasList,
      };
    });
  } else if (databaseAndSchema?.schemas) {
    newCascaderOptions = (databaseAndSchema?.schemas || []).map((t) => {
      return {
        value: t.name,
        label: t.name,
      };
    });
  }
  return newCascaderOptions;
}

const RenderSelectDatabase = dvaModel(function (props: IProps) {
  const { connectionModel, workspaceModel, dispatch } = props;
  const { databaseAndSchema, curWorkspaceParams } = workspaceModel;
  const { curConnection } = connectionModel;
  const [currentSelectedName, setCurrentSelectedName] = useState('');

  useEffect(() => {
    if (curConnection?.id) {
      dispatch({
        type: 'workspace/fetchDatabaseAndSchema',
        payload: {
          dataSourceId: curConnection.id,
        },
      });
    }
  }, [curConnection]);

  const cascaderOptions = useMemo(() => {
    if (!databaseAndSchema) {
      return
    }
    const res = handleDatabaseAndSchema(databaseAndSchema);
    if (!curWorkspaceParams?.dataSourceId || curWorkspaceParams?.dataSourceId !== curConnection?.id) {
      // 如果databaseAndSchema 发生切变 并且没选中确切的database时，需要默认选中第一个
      const curWorkspaceParams = {
        dataSourceId: curConnection?.id,
        databaseSourceName: curConnection?.alias,
        databaseName: res?.[0]?.value,
        schemaName: res?.[0]?.children?.[0]?.value,
        databaseType: curConnection?.type,
      };
      dispatch({
        type: 'workspace/setCurWorkspaceParams',
        payload: curWorkspaceParams,
      });
    }
    return res;
  }, [databaseAndSchema]);

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
      {/* <div className={styles.otherOperations}>
        <div className={styles.iconBox}>
          <Iconfont code="&#xec08;" />
        </div>
      </div> */}
    </div>
  );
});

const RenderTableBox = dvaModel(function (props: any) {
  const { workspaceModel, dispatch } = props;
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
      <LoadingContent className={styles.treeBox} data={curTableList} handleEmpty>
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
                <div dangerouslySetInnerHTML={{ __html: t.name }} />
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
