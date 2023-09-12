import React, { memo, useState, useEffect, useRef, useContext, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Input, Cascader, Button, Space, Dropdown, MenuProps } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import Tree from '../Tree';
import { treeConfig } from '../Tree/treeConfig';
import { IManageResultData, IResultConfig, ITreeNode } from '@/typings';
import { TreeNodeType } from '@/constants';
import styles from './index.less';
import { approximateTreeNode } from '@/utils';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
import { DownOutlined } from '@ant-design/icons';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';

interface IOption {
  value: TreeNodeType;
  label: string;
}

const optionsList: IOption[] = [
  {
    value: TreeNodeType.TABLES,
    label: i18n('common.text.table')
  },
  { value: TreeNodeType.VIEWS, label: i18n('workspace.tree.view') },
  { value: TreeNodeType.FUNCTIONS, label: i18n('workspace.tree.function') },
  { value: TreeNodeType.PROCEDURES, label: i18n('workspace.tree.procedure') },
  { value: TreeNodeType.TRIGGERS, label: i18n('workspace.tree.trigger') },
]

const dvaModel = connect(
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    tableLoading: loading.effects['workspace/fetchGetCurTableList'],
    databaseLoading: loading.effects['workspace/fetchDatabaseAndSchema'],
  }),
);

interface IDocProps {
  onExport: (exportType: ExportTypeEnum) => void;
}

const TableList = dvaModel(function (props: IDocProps) {
  const { workspaceModel, dispatch, onExport } = props;
  const { curWorkspaceParams, curTableList, curViewList } = workspaceModel;
  const [searching, setSearching] = useState<boolean>(false);
  const inputRef = useRef<any>();
  const [searchedTableList, setSearchedTableList] = useState<ITreeNode[] | undefined>();
  const isReady = curWorkspaceParams?.dataSourceId && ((curWorkspaceParams?.databaseName || curWorkspaceParams?.schemaName) || (curWorkspaceParams?.databaseName === null && curWorkspaceParams?.schemaName == null))
  const [curType, setCurType] = useState<IOption>(optionsList[0]);
  const [curList, setCurList] = useState<ITreeNode[]>([]);
  const [tableLoading, setTableLoading] = useState<boolean>(false);

  // 导出表结构
  const handleExport = (exportType: ExportTypeEnum) => {
    props.onExport && props.onExport(exportType);
  };

  const items: MenuProps['items'] = useMemo(
    () => [
      {
        label: i18n('common.button.exportWord'),
        key: '1',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.WORD);
        },
      },
      {
        label: i18n('common.button.exportExcel'),
        key: '2',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.EXCEL);
        },
      },
      {
        label: i18n('common.button.exportHtml'),
        key: '3',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.HTML);
        },
      },
      {
        label: i18n('common.button.exportMarkdown'),
        key: '4',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.MARKDOWN);
        },
      },
      {
        label: i18n('common.button.exportPdf'),
        key: '5',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExport(ExportTypeEnum.PDF);
        },
      },
    ],
    [curWorkspaceParams]
  );

  useUpdateEffect(() => {
    setCurList([]);
    getList();
  }, [curType]);

  useEffect(() => {
    setCurList([]);
    if (isReady) {
      setCurType({...optionsList[0]});
    }
  }, [curWorkspaceParams]);



  useEffect(() => {
    if (searching) {
      inputRef.current!.focus({
        cursor: 'start',
      });
    }
  }, [searching])

  function getList(refresh: boolean = false) {
    setTableLoading(true);
    treeConfig[curType.value].getChildren!({
      refresh,
      ...curWorkspaceParams,
      extraParams: curWorkspaceParams,
    }).then(res => {
      setCurList(res);
      setTableLoading(false);
      if (curType.value === TreeNodeType.TABLES) {
        dispatch({
          type: 'workspace/setCurTableList',
          payload: res,
        })
      }
    })
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
    setSearchedTableList(approximateTreeNode(curList, value))
  }

  function refreshTableList() {
    if (isReady) {
      setCurList([]);
      getList(true);
    }
  }

  function cascaderChange(value: string[], selectedOptions: IOption[]) {
    setCurType(selectedOptions[0]);
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
              <Cascader
                defaultValue={[curType.value]}
                popupClassName={styles.cascaderPopup}
                options={optionsList}
                onChange={cascaderChange as any}
              >
                <div className={styles.modelName}>
                  {curType.label}
                  <Iconfont code='&#xe88e;' />
                </div>
              </Cascader>
              <div className={styles.iconBox} >
                <Dropdown menu={{ items }}>
                  <Button>
                    <Space>
                      {i18n('common.text.export')}
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
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
        <Tree className={styles.tree} initialData={searchedTableList || curList}></Tree>
      </LoadingContent>
    </div>
  );
});

export default dvaModel(TableList);
