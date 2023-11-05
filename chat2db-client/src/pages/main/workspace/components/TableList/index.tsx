import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Input, Cascader, Dropdown, MenuProps, Pagination, Select, ConfigProvider } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import Tree from '../Tree';
import { treeConfig } from '../Tree/treeConfig';
import { TreeNodeType, WorkspaceTabType, ConsoleStatus, ConsoleOpenedStatus, OperationColumn } from '@/constants';
import { approximateTreeNode } from '@/utils';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { v4 as uuidV4 } from 'uuid';
import { IPagingData, ITreeNode } from '@/typings';
import { ExportTypeEnum } from '@/typings/resultTable';
import historyService from '@/service/history';
import { debounce } from 'lodash';
import { dataSourceFormConfigs } from '@/components/ConnectionEdit/config/dataSource';
import styles from './index.less';
import ImportBlock from '@/components/ImportBlock';

interface IOption {
  value: TreeNodeType;
  label: string;
}

const optionsList: IOption[] = [
  {
    value: TreeNodeType.TABLES,
    label: i18n('common.text.table'),
  },
  { value: TreeNodeType.VIEWS, label: i18n('workspace.tree.view') },
  { value: TreeNodeType.FUNCTIONS, label: i18n('workspace.tree.function') },
  { value: TreeNodeType.PROCEDURES, label: i18n('workspace.tree.procedure') },
  { value: TreeNodeType.TRIGGERS, label: i18n('workspace.tree.trigger') },
];

const defaultPaddingData = {
  total: 0,
  pageSize: 100,
  pageNo: 1,
};

const dvaModel = connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);

const TableList = dvaModel((props: any) => {
  const { workspaceModel, dispatch } = props;
  const { curWorkspaceParams } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedTableList, setSearchedTableList] = useState<ITreeNode[] | undefined>();
  const isReady =
    curWorkspaceParams?.dataSourceId &&
    (curWorkspaceParams?.databaseName ||
      curWorkspaceParams?.schemaName ||
      (curWorkspaceParams?.databaseName === null && curWorkspaceParams?.schemaName == null));
  const [curType, setCurType] = useState<IOption>(optionsList[0]);
  const [curList, setCurList] = useState<ITreeNode[]>([]);
  const [tableLoading, setTableLoading] = useState<boolean>(false);
  const [pagingData, setPagingData] = useState<IPagingData>(defaultPaddingData);
  const [searchKey, setSearchKey] = useState<string>('');
  const leftModuleTitleRef = useRef<any>(null);
  const treeBoxRef = useRef<any>(null);
  const controllerRef = useRef<AbortController>();

  // 导出表结构
  const handleExport = (exportType: ExportTypeEnum) => {
    props.onExport && props.onExport(exportType);
  };

  const items: MenuProps['items'] = useMemo(() => {
    const list = [
      {
        label: (
          <div className={styles.operationItem}>
            <Iconfont className={styles.operationIcon} code="&#xec83;" />
            <div className={styles.operationTitle}>{i18n('common.button.createConsole')}</div>
          </div>
        ),
        key: 'createConsole',
        onClick: () => {
          addConsole();
        },
      },
      {
        label: (
          <div className={styles.operationItem}>
            <Iconfont className={styles.operationIcon} code="&#xe792;" />
            <div className={styles.operationTitle}>{i18n('editTable.button.createTable')}</div>
          </div>
        ),
        key: 'createTable',
        onClick: () => {
          dispatch({
            type: 'workspace/setCreateConsoleIntro',
            payload: {
              id: uuidV4(),
              type: WorkspaceTabType.CreateTable,
              title: 'create-table',
              uniqueData: {},
            },
          });
        },
      },
      {
        label: (
          <div className={styles.operationItem}>
            <Iconfont className={styles.operationIcon} code="&#xe613;" />
            <div className={styles.operationTitle}>{i18n('editTable.button.importTable')}</div>
          </div>
        ),
        key: 'importTable',
        children: [
          {
            label: (
              <div className={classnames(styles.operationItem, styles.importTableOperationItem)}>
                <Iconfont className={styles.operationIcon} code="&#xe7ba;" />
                <div className={styles.operationTitle}>{i18n('common.button.exportWord')}</div>
              </div>
            ),
            key: '1',
            onClick: () => {
              handleExport(ExportTypeEnum.WORD);
            },
          },
          {
            label: (
              <div className={classnames(styles.operationItem, styles.importTableOperationItem)}>
                <Iconfont className={styles.operationIcon} code="&#xe7b7;" />
                <div className={styles.operationTitle}>{i18n('common.button.exportExcel')}</div>
              </div>
            ),
            key: '2',
            onClick: () => {
              handleExport(ExportTypeEnum.EXCEL);
            },
          },
          {
            label: (
              <div className={classnames(styles.operationItem, styles.importTableOperationItem)}>
                <Iconfont className={styles.operationIcon} code="&#xe87d;" />
                <div className={styles.operationTitle}>{i18n('common.button.exportHtml')}</div>
              </div>
            ),
            key: '3',
            onClick: () => {
              handleExport(ExportTypeEnum.HTML);
            },
          },
          {
            label: (
              <div className={classnames(styles.operationItem, styles.importTableOperationItem)}>
                <Iconfont className={styles.operationIcon} code="&#xe7b8;" />
                <div className={styles.operationTitle}>{i18n('common.button.exportMarkdown')}</div>
              </div>
            ),
            key: '4',
            onClick: () => {
              handleExport(ExportTypeEnum.MARKDOWN);
            },
          },
          {
            label: (
              <div className={classnames(styles.operationItem, styles.importTableOperationItem)}>
                <Iconfont className={styles.operationIcon} code="&#xe67a;" />
                <div className={styles.operationTitle}>{i18n('common.button.exportPdf')}</div>
              </div>
            ),
            key: '5',
            onClick: () => {
              handleExport(ExportTypeEnum.PDF);
            },
          },
        ],
      },
      {
        label: (
          <ImportBlock
            title={i18n('common.button.import')}
            accept={'.sql'}
            onConfirm={async (file) => {
              if (Array.isArray(file)) return Promise.resolve(false);

              const reader = new FileReader();

              reader.onload = function (event) {
                const sqlContent = (event.target?.result ?? '') as string;
                addConsole(sqlContent);
              };

              reader.readAsText(file);
              return Promise.resolve(true);
            }}
          >
            <div className={styles.operationItem}>
              <Iconfont className={styles.operationIcon} code="&#xe66c;" />
              <div className={styles.operationTitle}>{i18n('common.button.import')}</div>
            </div>
          </ImportBlock>
        ),
        key: 'importSQL',
        onClick: () => {
          // addConsole();
        },
      },
    ];
    const dataSourceFormConfig = dataSourceFormConfigs.find((item) => item.type === curWorkspaceParams.databaseType);

    if (dataSourceFormConfig?.baseInfo.excludes?.includes(OperationColumn.EditTable)) {
      list.splice(1, 1);
    }
    return list;
  }, [curWorkspaceParams]);

  useUpdateEffect(() => {
    getList();
  }, [curType]);

  useUpdateEffect(() => {
    getList();
  }, [pagingData.pageSize, pagingData.pageNo]);

  useUpdateEffect(() => {
    if (curType.value !== TreeNodeType.TABLES) {
      setSearchedTableList(approximateTreeNode(curList, searchKey));
    }
  }, [searchKey]);

  useEffect(() => {
    setCurList([]);
    setPagingData(defaultPaddingData);
    if (isReady) {
      setCurType({ ...optionsList[0] });
    }
  }, [curWorkspaceParams]);

  useEffect(() => {
    if (searching) {
      inputRef.current!.focus({
        cursor: 'start',
      });
    }
  }, [searching]);

  // 监听treeBox滚动时，给leftModuleTitle添加下阴影
  useEffect(() => {
    const treeBox = treeBoxRef.current;
    const leftModuleTitleDom = leftModuleTitleRef.current;
    if (!treeBox || !leftModuleTitleDom) {
      return;
    }
    const handleScroll = () => {
      const scrollTop = treeBox.scrollTop;
      if (scrollTop > 0) {
        leftModuleTitleDom.classList.add(styles.leftModuleTitleShadow);
      } else {
        leftModuleTitleDom.classList.remove(styles.leftModuleTitleShadow);
      }
    };
    treeBox.addEventListener('scroll', handleScroll);
    return () => {
      treeBox.removeEventListener('scroll', handleScroll);
    };
  }, [treeBoxRef.current, leftModuleTitleRef.current]);

  const addConsole = (ddl?: string) => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    const params = {
      name: `new console`,
      ddl: ddl || '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: WorkspaceTabType.CONSOLE,
      tabType: WorkspaceTabType.CONSOLE,
    };

    historyService.saveConsole(params).then((res) => {
      dispatch({
        type: 'workspace/setCreateConsoleIntro',
        payload: {
          id: res,
          type: WorkspaceTabType.CONSOLE,
          title: params.name,
          uniqueData: params,
        },
      });
    });
  };

  function getList(params?: { refresh?: boolean }) {
    // 在每一getList之前，都要把上一次的请求abort掉
    controllerRef.current && controllerRef.current.abort('abortRequest');

    // 为每一次请求创建一个新的AbortController
    controllerRef.current = new AbortController();

    // abort会触发上一次请求的setTableLoading(false); 所以这里要延迟触发
    setTimeout(() => {
      setTableLoading(true);
    }, 0);

    setCurList([]);
    const { refresh = false } = params || {};
    const p = {
      refresh,
      ...curWorkspaceParams,
      extraParams: curWorkspaceParams,
      searchKey: inputRef.current?.input.value || '',
    };
    if (curType.value === TreeNodeType.TABLES) {
      p.pageNo = pagingData.pageNo;
      p.pageSize = pagingData.pageSize;
    }

    // 发送请求
    treeConfig[curType.value].getChildren!(p, {
      signal: controllerRef.current.signal,
    })
      .then((res: any) => {
        // 表的处理
        if (curType.value === TreeNodeType.TABLES) {
          setCurList(approximateTreeNode(res.data, inputRef.current?.input.value));
          setPagingData({
            ...pagingData,
            total: res.total,
          });
          dispatch({
            type: 'workspace/setCurTableList',
            payload: res.data,
          });
        } else {
          setCurList(approximateTreeNode(res, inputRef.current?.input.value));
        }
        setTableLoading(false);
      })
      .catch(() => {
        setTableLoading(false);
      });
  }

  function openSearch() {
    setSearching(true);
  }

  function onBlur() {
    if (!inputRef.current.input.value) {
      setSearching(false);
      setSearchedTableList(undefined);
    }
  }

  function refreshTableList() {
    if (isReady && !tableLoading) {
      getList({
        refresh: true,
      });
    }
  }

  function cascaderChange(value: string[], selectedOptions: IOption[]) {
    setCurType(selectedOptions[0]);
  }

  const handleChangePagination = (pageNo: number) => {
    setPagingData({
      ...pagingData,
      pageNo,
    });
  };

  const handleChangePageSize = (value: number) => {
    setPagingData({
      ...pagingData,
      pageNo: 1,
      pageSize: value,
    });
  };

  const handleValue = useCallback(
    debounce(() => {
      if (curType.value === TreeNodeType.TABLES) {
        if (pagingData.pageNo === 1) {
          getList();
        }
        setPagingData({
          ...pagingData,
          pageNo: 1,
        });
      }
    }, 500),
    [curType, pagingData],
  );

  return (
    <div className={styles.tableModule}>
      <div ref={leftModuleTitleRef} className={styles.leftModuleTitle}>
        {searching ? (
          <div className={styles.leftModuleTitleSearch}>
            <Input
              ref={inputRef}
              value={searchKey}
              size="small"
              placeholder={i18n('common.text.search')}
              prefix={<Iconfont code="&#xe600;" />}
              onBlur={onBlur}
              onChange={(e) => {
                setSearchKey(e.target.value);
                handleValue();
              }}
              allowClear
            />
          </div>
        ) : (
          <div className={styles.leftModuleTitleText}>
            <ConfigProvider
              theme={{
                token: {
                  motion: false,
                },
              }}
            >
              <Cascader
                defaultValue={[curType.value]}
                popupClassName={styles.cascaderPopup}
                options={optionsList}
                onChange={cascaderChange as any}
              >
                <div className={styles.modelName}>
                  {curType.label}
                  <Iconfont code="&#xe88e;" />
                </div>
              </Cascader>
            </ConfigProvider>
            <div className={styles.iconBox}>
              <div className={classnames(styles.refreshIcon, styles.itemIcon)} onClick={() => refreshTableList()}>
                <Iconfont code="&#xec08;" />
              </div>
              <div className={classnames(styles.searchIcon, styles.itemIcon)} onClick={() => openSearch()}>
                <Iconfont code="&#xe600;" />
              </div>
              {curType.value === TreeNodeType.TABLES && (
                <Dropdown menu={{ items }}>
                  <div className={classnames(styles.moreIcon, styles.itemIcon)}>
                    <Iconfont code="&#xe601;" />
                  </div>
                </Dropdown>
              )}
            </div>
          </div>
        )}
      </div>

      <div ref={treeBoxRef} className={styles.treeBox}>
        <LoadingContent isLoading={tableLoading}>
          {curType.value === TreeNodeType.TABLES && !curList.length ? (
            <div className={styles.emptyBox}>
              <div>{i18n('common.text.noTableFoundUp')}</div>
              <div>{i18n('common.text.noTableFoundDown')}</div>
            </div>
          ) : (
            <Tree initialData={searchedTableList || curList} />
          )}
        </LoadingContent>
      </div>

      {pagingData?.total > 100 && !searchKey && curType.value === TreeNodeType.TABLES && (
        <div className={styles.paging}>
          <div className={styles.paginationBox}>
            <Pagination
              onChange={handleChangePagination}
              current={pagingData?.pageNo}
              pageSize={pagingData?.pageSize}
              simple
              size="small"
              total={pagingData?.total}
            />
          </div>
          <div className={styles.paginationSelectBox}>
            <Select
              defaultValue={100}
              style={{ width: 60 }}
              onChange={handleChangePageSize}
              bordered={false}
              options={[
                { value: 50, label: '50' },
                { value: 100, label: '100' },
                { value: 200, label: '200' },
              ]}
            />
          </div>
        </div>
      )}
    </div>
  );
});

export default dvaModel(TableList);
