import React, { memo, useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import classnames from 'classnames';
import { connect } from 'umi';
import lodash from 'lodash';
import Iconfont from '@/components/Iconfont';
import CustomLayout from '@/components/CustomLayout';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { IMainPageType } from '@/models/mainPage';
import { Cascader, Spin, Modal, Tag, Divider, ConfigProvider, Input } from 'antd';
import { databaseMap, TreeNodeType, DatabaseTypeCode } from '@/constants';
import { treeConfig } from '../Tree/treeConfig';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import styles from './index.less';
import i18n from '@/i18n';
import CreateDatabase, { ICreateDatabaseRef } from '@/components/CreateDatabase';
import { getCurrentWorkspaceDatabase } from '@/utils/localStorage';
import { registerIntelliSenseDatabase } from '@/utils/IntelliSense';

interface IProps {
  className?: string;
  connectionModel: IConnectionModelType['state'];
  workspaceModel: IWorkspaceModelType['state'];
  mainPageModel: IMainPageType['state'];
  dispatch: any;
}

interface IOption {
  label: string | React.ReactNode;
  value: number | string;
}

// 不支持创建数据库的数据库类型
const notSupportCreateDatabaseType = [DatabaseTypeCode.H2];

// 不支持创建schema的数据库类型
const notSupportCreateSchemaType = [DatabaseTypeCode.ORACLE];

const WorkspaceHeader = memo<IProps>((props) => {
  const { connectionModel, workspaceModel, mainPageModel, dispatch } = props;
  const { connectionList, curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const { curPage } = mainPageModel;
  const [cascaderLoading, setCascaderLoading] = useState(false);
  const [noConnectionModal, setNoConnectionModal] = useState(false);
  const [connectionOptions, setConnectionOptions] = useState<IOption[]>([]);
  const [curDBOptions, setCurDBOptions] = useState<IOption[]>([]);
  const [curSchemaOptions, setCurSchemaOptions] = useState<IOption[]>([]);
  const [isRefresh, setIsRefresh] = useState(false);
  const [openDBCascaderDropdown, setOpenDBCascaderDropdown] = useState<false | undefined>(undefined);
  const [openSchemaCascaderDropdown, setOpenSchemaCascaderDropdown] = useState<false | undefined>(undefined);
  const createDatabaseRef = React.useRef<ICreateDatabaseRef>(null);
  const localStorageWorkspaceDatabase = getCurrentWorkspaceDatabase();
  const [searchCurDBOptions, setSearchCurDBOptions] = useState<IOption[] | null>(null);
  const [searchCurSchemaOptions, setSearchCurSchemaOptions] = useState<IOption[] | null>(null);
  const dbOptionsSearchRef = React.useRef<any>(null);
  const schemaOptionsSearchRef = React.useRef<any>(null);

  useEffect(() => {
    if (openDBCascaderDropdown === false) {
      setOpenDBCascaderDropdown(undefined);
    }
    if (openSchemaCascaderDropdown === false) {
      setOpenSchemaCascaderDropdown(undefined);
    }
  }, [openDBCascaderDropdown, openSchemaCascaderDropdown]);

  useEffect(() => {
    if (curPage !== 'workspace') {
      return;
    }
    // 如果没有curConnection默认选第一个
    if (!curConnection?.id && connectionList.length) {
      if (
        localStorageWorkspaceDatabase.dataSourceId &&
        connectionList.find((t: any) => Number(t.id) === Number(localStorageWorkspaceDatabase.dataSourceId))
      ) {
        connectionChange([localStorageWorkspaceDatabase.dataSourceId]);
        return;
      }
      connectionChange([connectionList[0].id]);
      return;
    }
    // 如果都有的话
    if (curConnection?.id && connectionList.length) {
      // 如果curConnection不再connectionList里，也是默认选第一个
      const flag = connectionList.findIndex((t: any) => t.id === curConnection?.id);
      if (flag === -1) {
        if (
          localStorageWorkspaceDatabase.dataSourceId &&
          connectionList.find((t: any) => Number(t.id) === Number(localStorageWorkspaceDatabase.dataSourceId))
        ) {
          connectionChange([localStorageWorkspaceDatabase.dataSourceId]);
          return;
        }
        connectionChange([connectionList[0].id]);
        return;
      }

      // 如果切换了curConnection 导致curWorkspaceParams与curConnection不同
      if (curWorkspaceParams.dataSourceId !== curConnection?.id) {
        setCurWorkspaceParams({
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
          databaseType: curConnection.type,
        });
        setCurDBOptions([]);
        setCurSchemaOptions([]);
      }

      // 获取database列表
      getDatabaseList(isRefresh);
      setIsRefresh(false);
    }
    setConnectionOptions(
      connectionList?.map((t) => {
        return {
          value: t.id,
          label: (
            <div style={{ display: 'flex' }}>
              <Iconfont className={styles.databaseTypeIcon} code={databaseMap[t.type]?.icon} />
              <div className={styles.text}>{t.alias}</div>
            </div>
          ),
        };
      }),
    );
  }, [connectionList, curConnection, curPage]);

  useUpdateEffect(() => {
    if (!connectionList.length) {
      dispatch({
        type: 'workspace/setCurWorkspaceParams',
        payload: {},
      });
      dispatch({
        type: 'connection/setCurConnection',
        payload: {},
      });
    }
  }, [connectionList]);

  function getDatabaseList(refresh = false) {
    setCascaderLoading(true);
    if (!curConnection?.id) {
      return;
    }
    treeConfig[TreeNodeType.DATA_SOURCE]
      .getChildren?.({
        dataSourceId: curConnection.id,
        refresh,
        extraParams: {
          databaseType: curConnection.type,
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
        },
      })
      .then((res: any) => {
        const dbList =
          res?.map((t) => {
            return {
              value: t.key,
              label: t.name,
            };
          }) || [];

        setCurDBOptions(dbList);
        registerIntelliSenseDatabase(
          res.map((t: any) => ({ name: t.name, dataSourceName: t.extraParams.dataSourceName })),
        );
        let databaseName = '';
        if (dbList.find((t: any) => t.value === localStorageWorkspaceDatabase.databaseName)) {
          databaseName = localStorageWorkspaceDatabase.databaseName!;
        } else {
          // 如果是切换那么就默认取列表的第一个database， 如果不是切换那么就取缓存的，如果缓存没有还是取列表第一个（这里是兜底，如果原先他并没有database，后来他加了database，如果还是取缓存的空就不对了）
          databaseName =
            curWorkspaceParams.dataSourceId !== curConnection?.id
              ? dbList[0]?.label
              : curWorkspaceParams.databaseName || dbList[0]?.label;
        }
        databaseChange([databaseName], [{ label: databaseName }], refresh);
        // getSchemaList(databaseName, refresh);
      })
      .catch(() => {
        setCascaderLoading(false);
      });
  }

  function getSchemaList(databaseName: string | null | undefined, refresh = false) {
    if (!curConnection?.id) {
      return;
    }
    treeConfig[TreeNodeType.DATABASE]
      .getChildren?.({
        dataSourceId: curConnection.id,
        databaseName: databaseName,
        refresh,
        extraParams: {
          databaseName: databaseName,
          databaseType: curConnection.type,
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.name,
        },
      })
      .then((res: any) => {
        const schemaList =
          res?.map((t) => {
            return {
              value: t.key,
              label: t.name,
            };
          }) || [];
        setCurSchemaOptions(schemaList);

        let schemaName = '';
        if (schemaList.find((t: any) => t.value === localStorageWorkspaceDatabase.schemaName)) {
          schemaName = localStorageWorkspaceDatabase.schemaName!;
        } else {
          schemaName =
            curWorkspaceParams.dataSourceId !== curConnection?.id
              ? schemaList[0]?.label
              : curWorkspaceParams.schemaName || schemaList[0]?.label;
        }
        // schemaChange([schemaName], [{ label: schemaName }]);
        const data: any = {
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
          databaseType: curConnection.type,
          databaseName: databaseName || null,
          schemaName: schemaName || null,
        };

        setCurWorkspaceParams(data);
      })
      .catch(() => {
        setCurWorkspaceParams({
          dataSourceId: curConnection.id,
          dataSourceName: curConnection.alias,
          databaseType: curConnection.type,
          databaseName: databaseName || null,
        });
      })
      .finally(() => {
        setCascaderLoading(false);
      });
  }

  function setCurWorkspaceParams(payload: IWorkspaceModelType['state']['curWorkspaceParams']) {
    if (lodash.isEqual(curWorkspaceParams, payload)) {
      return;
    }

    dispatch({
      type: 'workspace/setCurWorkspaceParams',
      payload,
    });
  }

  const getConnectionList = () => {
    setCascaderLoading(true);
    setIsRefresh(true);
    dispatch({
      type: 'connection/fetchConnectionList',
      payload: {
        refresh: true,
      },
    });
  };

  // 连接切换
  function connectionChange(id: any) {
    connectionList.map((t) => {
      if (t.id === id[0] && curWorkspaceParams.dataSourceId !== id[0]) {
        dispatch({
          type: 'connection/setCurConnection',
          payload: t,
        });
      }
    });
  }

  // 数据库切换
  function databaseChange(valueArr: any, selectedOptions: any, refresh) {
    // if (selectedOptions[0].label !== curWorkspaceParams.databaseName) {
    getSchemaList(selectedOptions[0].label, refresh);
    // }
  }

  // schema切换
  function schemaChange(valueArr: any, selectedOptions: any) {
    // if (selectedOptions[0].label !== curWorkspaceParams.schemaName) {
    setCurWorkspaceParams({ ...curWorkspaceParams, schemaName: selectedOptions[0].value });
    // }
  }

  function handleRefresh() {
    getConnectionList();
    setSearchCurDBOptions(null);
    setSearchCurSchemaOptions(null);
    schemaOptionsSearchRef.current?.setValue('');
    dbOptionsSearchRef.current?.setValue('');
  }

  function handleSearchDB(value: string) {
    setSearchCurDBOptions(
      value
        ? curDBOptions.filter((t: any) => {
            const reg = new RegExp(value, 'i');
            return reg.test(t?.label || '');
          })
        : null,
    );
  }

  function handleSearchSchema(value: string) {
    setSearchCurSchemaOptions(
      value
        ? curSchemaOptions.filter((t: any) => {
            const reg = new RegExp(value, 'i');
            return reg.test(t?.label || '');
          })
        : null,
    );
  }

  return (
    <>
      {!!connectionList.length && (
        <ConfigProvider
          theme={{
            token: {
              motion: false,
            },
          }}
        >
          <div className={styles.workspaceHeader}>
            <div className={styles.workspaceHeaderLeft}>
              <Cascader
                popupClassName={styles.cascaderPopup}
                options={connectionOptions}
                onChange={connectionChange}
                bordered={false}
                value={[curConnection?.id || '']}
              >
                <div className={styles.crumbsItem}>
                  <Iconfont
                    className={styles.databaseTypeIcon}
                    code={databaseMap[curWorkspaceParams.databaseType]?.icon}
                  />
                  <div className={styles.text}>{curWorkspaceParams.dataSourceName}</div>
                  <div className={styles.pullDownArrow}>
                    <Iconfont code="&#x100be;" />
                  </div>
                </div>
              </Cascader>

              {/* {!!curDBOptions?.length && <Iconfont className={styles.arrow} code="&#xe641;" />} */}
              {!!curDBOptions?.length && (
                <Cascader
                  popupClassName={styles.cascaderPopup}
                  options={searchCurDBOptions || curDBOptions}
                  open={openDBCascaderDropdown}
                  dropdownRender={(menu) => {
                    return (
                      <div>
                        <SearchHeader ref={dbOptionsSearchRef} handleSearch={handleSearchDB} />
                        <Divider style={{ margin: 0 }} />

                        {menu}

                        <Divider style={{ margin: 0 }} />

                        {
                          // 不支持创建数据库的数据库类型
                          !notSupportCreateDatabaseType.includes(curWorkspaceParams?.databaseType) && (
                            <div
                              className={styles.dropdownFooter}
                              onClick={() => {
                                setOpenDBCascaderDropdown(false);
                                createDatabaseRef.current?.setOpen(true, 'database');
                              }}
                            >
                              <Iconfont code="&#xe631;" />
                              {i18n('common.Button.addDatabase')}
                            </div>
                          )
                        }
                      </div>
                    );
                  }}
                  onChange={databaseChange as any}
                  bordered={false}
                  value={[curWorkspaceParams?.databaseName || '']}
                >
                  <div className={styles.crumbsItem}>
                    <Iconfont className={styles.databaseTypeIcon} code="&#xe62c;" />
                    <div className={styles.text}>{curWorkspaceParams.databaseName}</div>
                    <div className={styles.pullDownArrow}>
                      <Iconfont code="&#x100be;" />
                    </div>
                  </div>
                </Cascader>
              )}
              {/* {!!curSchemaOptions.length && <Iconfont className={styles.arrow} code="&#xe641;" />} */}
              {!!curSchemaOptions.length && (
                <Cascader
                  popupClassName={styles.cascaderPopup}
                  options={searchCurSchemaOptions || curSchemaOptions}
                  onChange={schemaChange}
                  bordered={false}
                  open={openSchemaCascaderDropdown}
                  value={[curWorkspaceParams?.schemaName || '']}
                  dropdownRender={(menu) => {
                    return (
                      <div>
                        <SearchHeader ref={schemaOptionsSearchRef} handleSearch={handleSearchSchema} />
                        <Divider style={{ margin: 0 }} />
                        {menu}
                        <Divider style={{ margin: 0 }} />
                        {
                          // 不支持创建schema的数据库类型
                          !notSupportCreateSchemaType.includes(curWorkspaceParams?.databaseType) && (
                            <div
                              className={styles.dropdownFooter}
                              onClick={() => {
                                setOpenSchemaCascaderDropdown(false);
                                createDatabaseRef.current?.setOpen(true, 'schema');
                              }}
                            >
                              <Iconfont code="&#xe631;" />
                              {i18n('common.Button.addSchema')}
                            </div>
                          )
                        }
                      </div>
                    );
                  }}
                >
                  <div className={styles.crumbsItem}>
                    <Iconfont className={styles.databaseTypeIcon} code="&#xe696;" />
                    <div className={styles.text}>{curWorkspaceParams.schemaName}</div>
                    <div className={styles.pullDownArrow}>
                      <Iconfont code="&#x100be;" />
                    </div>
                  </div>
                </Cascader>
              )}
              <div className={styles.refreshBox} onClick={handleRefresh}>
                {cascaderLoading ? (
                  <Spin className={styles.spin} />
                ) : (
                  <Iconfont className={styles.typeIcon} code="&#xec08;" />
                )}
              </div>
            </div>
            <div className={classnames(styles.connectionTag, styles.workspaceHeaderCenter)}>
              {curConnection?.id && curConnection?.environment?.shortName && (
                <Tag color={curConnection?.environment?.color?.toLocaleLowerCase()}>
                  {curConnection?.environment?.shortName}
                </Tag>
              )}
            </div>
            <div className={styles.workspaceHeaderRight}>
              <CustomLayout />
            </div>
          </div>
        </ConfigProvider>
      )}

      <Modal open={noConnectionModal} closeIcon={<></>} keyboard={false} maskClosable={false} footer={false}>
        <div className={styles.noConnectionModal}>
          <div className={styles.mainText}>{i18n('connection.tips.noConnection')}</div>
          <Iconfont className={styles.icon} code="&#xe638;" />
          <div
            className={styles.createButton}
            onClick={() => {
              setNoConnectionModal(false);
              dispatch({
                type: 'mainPage/updateCurPage',
                payload: 'connections',
              });
            }}
          >
            {i18n('connection.button.createConnection')}
          </div>
        </div>
      </Modal>
      <CreateDatabase
        executedCallback={handleRefresh}
        curWorkspaceParams={curWorkspaceParams}
        ref={createDatabaseRef}
      />
    </>
  );
});

const SearchHeader = forwardRef((props: { handleSearch: (value: string) => void },ref: any) => {
  const [value, setValue] = useState('');

  useUpdateEffect(() => {
    props.handleSearch(value);
  }, [value]);

  useImperativeHandle(ref, () => ({
    setValue,
  }));

  return (
    <div className={styles.searchHeader} ref={ref}>
      <div className={styles.searchIconBox}>
        <Iconfont className={styles.searchIcon} code="&#xe600;" />
      </div>
      <Input
        value={value}
        onChange={(e) => {
          setValue(e.target.value);
        }}
        className={styles.searchHeaderInput}
        placeholder={i18n('common.text.search')}
        bordered={false}
        onPressEnter={() => {
          props.handleSearch(value);
        }}
      />
    </div>
  );
});

export default connect(
  ({
    connection,
    workspace,
    mainPage,
  }: {
    connection: IConnectionModelType;
    workspace: IWorkspaceModelType;
    mainPage: IMainPageType;
  }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    mainPageModel: mainPage,
  }),
)(WorkspaceHeader);
