import React, { useState, useEffect, useRef, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Input, Cascader, Dropdown, MenuProps, Pagination } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import Tree from '../Tree';
import { treeConfig } from '../Tree/treeConfig';
import { TreeNodeType, WorkspaceTabType, ConsoleStatus, ConsoleOpenedStatus } from '@/constants';
import { approximateTreeNode } from '@/utils';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { v4 as uuidV4 } from 'uuid';
import { IPagingData, ITreeNode } from '@/typings';
import styles from './index.less';
import { ExportTypeEnum } from '@/typings/resultTable';
import historyService from '@/service/history';

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
  pageSize: 200,
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

  // 导出表结构
  const handleExport = (exportType: ExportTypeEnum) => {
    props.onExport && props.onExport(exportType);
  };

  const items: MenuProps['items'] = useMemo(
    () => [
      {
        label: (
          <div className={styles.operationItem}>
            <Iconfont className={styles.operationIcon} code="&#xe63a;" />
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
    ],
    [curWorkspaceParams],
  );

  useUpdateEffect(() => {
    setCurList([]);
    getList();
  }, [curType]);

  useEffect(() => {
    setCurList([]);
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

  const addConsole = () => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    const params = {
      name: `new console`,
      ddl: '',
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

  function getList(params?: { pageNo?: number; refresh?: boolean; searchKey?: string }) {
    const { refresh = false, searchKey, pageNo = 1 } = params || {};
    setTableLoading(true);
    return new Promise((resolve) => {
      treeConfig[curType.value].getChildren!({
        refresh,
        ...curWorkspaceParams,
        extraParams: curWorkspaceParams,
        searchKey,
        pageNo,
      })
        .then((res: any) => {
          setCurList(res.data);
          resolve(res);
          setPagingData({
            total: res.total,
            pageSize: res.pageSize,
            pageNo: res.pageNo,
          });
          if (curType.value === TreeNodeType.TABLES) {
            dispatch({
              type: 'workspace/setCurTableList',
              payload: res.data,
            });
          }
          setTableLoading(false);
        })
        .catch(() => {
          setTableLoading(false);
        });
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

  function onChange(value: string) {
    if (curType.value === TreeNodeType.TABLES) {
      getList({
        searchKey: value,
        refresh: false,
      }).then((res: any) => {
        setSearchedTableList(approximateTreeNode(res.data, value));
      });
    } else {
      setSearchedTableList(approximateTreeNode(curList, value));
    }
  }

  function refreshTableList() {
    if (isReady) {
      setCurList([]);
      getList({
        refresh: true,
      });
    }
  }

  function cascaderChange(value: string[], selectedOptions: IOption[]) {
    setCurType(selectedOptions[0]);
  }

  const handelChangePagination = (pageNo: number) => {
    getList({
      pageNo,
      refresh: false,
      searchKey: inputRef.current?.input.value,
    }).then((res: any) => {
      setSearchedTableList(approximateTreeNode(res.data, inputRef.current?.input.value));
    });
  };

  return (
    <div className={styles.tableModule}>
      <div className={styles.leftModuleTitle}>
        {searching ? (
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
        ) : (
          <div className={styles.leftModuleTitleText}>
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
      <LoadingContent className={styles.treeBox} isLoading={tableLoading}>
        <Tree className={styles.tree} initialData={searchedTableList || curList} />
      </LoadingContent>
      {pagingData?.total > 200 && (
        <div className={styles.paging}>
          <Pagination
            onChange={handelChangePagination}
            current={pagingData?.pageNo}
            defaultPageSize={pagingData?.pageSize}
            simple
            size="small"
            total={pagingData?.total}
          />
        </div>
      )}
    </div>
  );
});

export default dvaModel(TableList);
