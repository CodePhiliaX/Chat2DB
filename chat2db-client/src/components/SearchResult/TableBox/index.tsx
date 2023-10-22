import React, { useEffect, useMemo, useState } from 'react';
import { Dropdown, Input, MenuProps, message, Modal, Space, Popover, Spin } from 'antd';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import styled from 'styled-components';
import classnames from 'classnames';
import i18n from '@/i18n';
import { CRUD } from '@/constants';
import { TableDataType } from '@/constants/table';
import ExecuteSQL from '@/components/ExecuteSQL';
import { IManageResultData, IResultConfig } from '@/typings/database';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import { compareStrings } from '@/utils/sort';
import { DownOutlined } from '@ant-design/icons';
import { copy } from '@/utils';
import Iconfont from '../../Iconfont';
import StateIndicator from '../../StateIndicator';
import MonacoEditor from '../../Console/MonacoEditor';
import MyPagination from '../Pagination';
import StatusBar from '../StatusBar';
import styles from './index.less';
import sqlService, { IExportParams, IExecuteSqlParams } from '@/service/sql';
import { downloadFile } from '@/utils/common';
import lodash from 'lodash';

interface ITableProps {
  className?: string;
  outerQueryResultData: IManageResultData;
  executeSqlParams: any;
}

interface IViewTableCellData {
  name: string;
  value: any;
}

interface IUpdateData {
  oldDataList?: string[];
  dataList?: string[];
  type: CRUD;
  rowNo: string;
}

const SupportBaseTable: any = styled(BaseTable)`
  &.supportBaseTable {
    --bgcolor: var(--color-bg-base);
    --header-bgcolor: var(--color-bg-subtle);
    --hover-bgcolor: var(--color-hover-bg);
    --header-hover-bgcolor: var(--color-hover-bg);
    --highlight-bgcolor: var(--color-hover-bg);
    --header-highlight-bgcolor: var(--color-hover-bg);
    --color: var(--color-text);
    --header-color: var(--color-text);
    --lock-shadow: rgb(37 37 37 / 0.5) 0 0 6px 2px;
    --border-color: var(--color-border-secondary);
    --cell-padding: 0px;
    --row-height: 32px;
    --lock-shadow: 0px 1px 2px 0px var(--color-border);
  }
`;

const preCode = '$$chat2db_';

const defaultPaginationConfig: IResultConfig = {
  pageNo: 1,
  pageSize: 200,
  total: 0,
  hasNextPage: true,
};

export default function TableBox(props: ITableProps) {
  const { className, outerQueryResultData } = props;
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);
  const [messageApi, contextHolder] = message.useMessage();
  const [paginationConfig, setPaginationConfig] = useState<IResultConfig>(defaultPaginationConfig);
  // sql查询结果
  const [queryResultData, setQueryResultData] = useState<IManageResultData>(outerQueryResultData);
  // tableData：带列标识的表数据 可以传给Table组件 进行渲染
  // 保存原始的表数据，用于撤销
  const [oldTableData, setOldTableData] = useState<{ [key: string]: string }[]>([]);
  // 实时更新的表数据
  const [tableData, setTableData] = useState<{ [key: string]: string }[]>([]);
  // DataList不带列标识的表数据
  // 保存原始的表数据，用于对比新老数据看是否有变化
  const [oldDataList, setOldDataList] = useState<string[][]>([]);
  // 正在标记的单元格的坐标
  const [editingCell, setEditingCell] = useState<[string, string] | null>(null);
  // input受控的正在编辑的数据
  const [editingData, setEditingData] = useState<string>('');
  // 当前选中的行号
  const [curOperationRowNo, setCurOperationRowNo] = useState<string | null>(null);
  // 操作过的数据列表
  const [updateData, setUpdateData] = useState<IUpdateData[] | []>([]);
  // 更新数据的sql
  const [updateDataSql, setUpdateDataSql] = useState<string | null>(null);
  // ExecuteSQL弹窗 初始化的错误信息
  const [initError, setInitError] = useState<string | null>(null);
  // 是否显示更新数据的sql
  const [viewUpdateDataSqlModal, setViewUpdateDataSqlModal] = useState<boolean>(false);
  // 用于滚动到底部
  const tableBoxRef = React.useRef<HTMLDivElement>(null);
  // 所有数据准备好了
  const [allDataReady, setAllDataReady] = useState<boolean>(false);
  // 编辑数据的inputRef
  const editDataInputRef = React.useRef<any>(null);

  const handleExportSQLResult = async (exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => {
    const params: IExportParams = {
      ...(props.executeSqlParams || {}),
      sql: queryResultData.sql,
      originalSql: queryResultData.originalSql,
      exportType,
      exportSize,
    };
    downloadFile(window._BaseURL + '/api/rdb/dml/export', params);
  };

  useEffect(() => {
    let total: any = queryResultData.fuzzyTotal;

    // 如果total是数字，且不为0，则还是使用原先的total
    if (lodash.isNumber(paginationConfig.total) && paginationConfig.total !== 0) {
      total = paginationConfig.total;
    }

    if (!lodash.isNumber(paginationConfig.total)) {
      const oldTotal = Number(paginationConfig.total.split('+')[0]);
      const newTotal = Number(queryResultData.fuzzyTotal.split('+')[0]);
      if (oldTotal > newTotal) {
        total = paginationConfig.total;
      }
    }

    setPaginationConfig({
      ...paginationConfig,
      total,
      hasNextPage: queryResultData.hasNextPage,
    });
  }, [queryResultData]);

  useEffect(() => {
    // 每次dataList变化，都需要重新计算tableData
    if (!columns?.length) {
      setTableData([]);
    } else {
      const newTableData = dataListTransformTableData(queryResultData.dataList);
      setTableData(newTableData);
      setOldTableData(newTableData);
      setAllDataReady(true);
    }
    // 每次data变化，都需要重新计算oldDataList
    if (queryResultData.dataList?.length) {
      setOldDataList(queryResultData.dataList);
    }
  }, [queryResultData.dataList]);

  // 导出sql的菜单项
  const exportDropdownItems: MenuProps['items'] = useMemo(
    () => [
      {
        label: i18n('workspace.table.export.all.csv'),
        key: '1',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExportSQLResult(ExportTypeEnum.CSV, ExportSizeEnum.ALL);
        },
      },
      {
        label: i18n('workspace.table.export.all.insert'),
        key: '2',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExportSQLResult(ExportTypeEnum.INSERT, ExportSizeEnum.ALL);
        },
      },
      {
        label: i18n('workspace.table.export.cur.csv'),
        key: '3',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExportSQLResult(ExportTypeEnum.CSV, ExportSizeEnum.CURRENT_PAGE);
        },
      },
      {
        label: i18n('workspace.table.export.cur.insert'),
        key: '4',
        // icon: <UserOutlined />,
        onClick: () => {
          handleExportSQLResult(ExportTypeEnum.INSERT, ExportSizeEnum.CURRENT_PAGE);
        },
      },
    ],
    [queryResultData],
  );

  const defaultSorts: SortItem[] = useMemo(
    () =>
      (queryResultData.headerList || []).map((item) => ({
        code: item.name,
        order: 'none',
      })),
    [queryResultData.headerList],
  );

  function viewTableCell(cellData: IViewTableCellData) {
    setViewTableCellData(cellData);
  }

  function copyTableCell(cellData: IViewTableCellData) {
    copy(cellData?.value || viewTableCellData?.value);
    messageApi.success(i18n('common.button.copySuccessfully'));
  }

  function handleCancel() {
    setViewTableCellData(null);
  }

  const handleDoubleClickTableItem = (colIndex, rowNo, value) => {
    if (!queryResultData.canEdit) {
      return;
    }
    setEditingData(value);
    setEditingCell([colIndex, rowNo]);
    setTimeout(() => {
      editDataInputRef?.current?.focus();
    }, 0);
  };

  // 编辑数据失焦
  const editDataOnBlur = () => {
    setEditingCell(null);
    setEditingData('');
    const [colIndex, rowNo] = editingCell!;
    const newTableData = lodash.cloneDeep(tableData);
    let oldRowDataList: string[] = [];
    let newRowDataList: string[] = [];
    newTableData.forEach((item) => {
      if (item[`${preCode}0No.`] === rowNo) {
        item[`${preCode}${colIndex}${columns[colIndex].name}`] = editingData;
        newRowDataList = Object.keys(item).map((i) => item[i]);
      }
    });

    setTableData(newTableData);

    oldDataList.forEach((item) => {
      if (item[0] === rowNo) {
        oldRowDataList = item;
      }
    });

    const index = updateData.findIndex((item) => item.rowNo === rowNo);
    // 如果newRowDataList和oldRowDataList的数据一样，代表用户虽然编辑过，但是又改回去了，则不需要更新
    if (oldRowDataList?.join(',') === newRowDataList?.join(',')) {
      if (index !== -1) {
        setUpdateData(updateData.filter((item) => item.rowNo !== rowNo && item.type !== CRUD.UPDATE));
      }
      return;
    }

    if (index === -1) {
      setUpdateData([
        ...updateData,
        {
          type: CRUD.UPDATE,
          oldDataList: oldRowDataList,
          dataList: newRowDataList,
          rowNo,
        },
      ]);
      return;
    }

    const newRowUpdateData = {
      ...updateData[index],
      dataList: newRowDataList,
    };

    // 如果是删除过的，则需要把type改为update
    if (newRowUpdateData.type === CRUD.DELETE) {
      newRowUpdateData.type = CRUD.UPDATE;
    }

    updateData[index] = newRowUpdateData;
    setUpdateData([...updateData]);
  };

  // 渲染单元格的值
  const renderTableCellValue = (value) => {
    if (value === null) {
      return <span className={styles.cellValueNull}>{'<null>'}</span>;
    } else if (!value) {
      // 如果为空需要展位
      return <span />;
    } else {
      return value;
    }
  };

  // 每个单元格的样式
  const tableCellStyle = (value, colIndex, rowNo) => {
    // 单元格的基础样式
    const styleList = [styles.tableItem];
    // 当前单元格所在的行被选中了
    if (rowNo === curOperationRowNo) {
      styleList.push(styles.tableItemEditing);
      return classnames(...styleList);
    }
    // 新添加的行
    const index2 = updateData.findIndex((item) => {
      return item.rowNo === rowNo && item.type === CRUD.CREATE;
    });
    if (index2 !== -1) {
      styleList.push(styles.tableItemSuccess);
      return classnames(...styleList);
    }
    // 如果是删除过的行
    const index = updateData.findIndex((item) => item.rowNo === rowNo && item.type === CRUD.DELETE);
    if (index !== -1) {
      styleList.push(styles.tableItemError);
      return classnames(...styleList);
    }
    // 编辑过的单元格的样式
    let oldValue = '';
    oldDataList.forEach((item) => {
      if (item[0] === rowNo) {
        oldValue = item[colIndex];
      }
    });
    if (value !== oldValue) {
      styleList.push(styles.tableItemEdit);
    }
    return classnames(...styleList);
  };

  // 纯数据的dataList 转换为 tableData
  const dataListTransformTableData = (myDataList: string[][]) => {
    const newTableData = (myDataList || []).map((item) => {
      const rowData: any = {};
      item.map((i: string | null, colIndex: number) => {
        const name = `${preCode}${colIndex}${columns[colIndex].name}`;
        rowData[name] = i;
      });
      return rowData;
    });
    return newTableData;
  };

  // 表格的列配置
  const columns: ArtColumn[] = useMemo(() => {
    return (queryResultData.headerList || []).map((item, colIndex) => {
      const { dataType, name } = item;
      const isNumber = dataType === TableDataType.NUMERIC;
      const isNumericalOrder = dataType === TableDataType.CHAT2DB_ROW_NUMBER;
      if (isNumericalOrder) {
        return {
          code: `${preCode}${colIndex}No.`,
          name: 'No.',
          key: name,
          lock: true,
          width: 60,
          features: { sortable: compareStrings },
          render: (value: any, rowData) => {
            const rowNo = rowData[`${preCode}0No.`];
            return (
              <div className={tableCellStyle(value, colIndex, rowNo)}>
                <div className={styles.tableItemNo}>{value}</div>
              </div>
            );
          },
        };
      }
      return {
        code: `${preCode}${colIndex}${name}`,
        name: name,
        key: name,
        render: (value: any, rowData) => {
          const rowNo = rowData[`${preCode}0No.`];
          return (
            <div
              className={tableCellStyle(value, colIndex, rowNo)}
              onDoubleClick={handleDoubleClickTableItem.bind(null, colIndex, rowNo, value)}
            >
              {editingCell?.join(',') === `${colIndex},${rowNo}` ? (
                <Input
                  ref={editDataInputRef}
                  value={editingData}
                  onChange={(e) => {
                    setEditingData(e.target.value);
                  }}
                  onBlur={editDataOnBlur}
                />
              ) : (
                <>
                  <div className={styles.tableItemContent}>{renderTableCellValue(value)}</div>
                  <div className={styles.tableHoverBox}>
                    <Iconfont code="&#xe606;" onClick={viewTableCell.bind(null, { name: item.name, value })} />
                    <Iconfont code="&#xeb4e;" onClick={copyTableCell.bind(null, { name: item.name, value })} />
                  </div>
                </>
              )}
            </div>
          );
        },
        // 如果是数字类型，因为后端返回的都是字符串，所以需要调用字符串对比函数来判断
        features: { sortable: isNumber ? compareStrings : true },
      };
    });
  }, [queryResultData.headerList, editingCell, editingData, curOperationRowNo, oldDataList]);

  // 表格渲染的配置
  const pipeline = useTablePipeline()
    .input({ dataSource: tableData, columns })
    .use(
      features.sort({
        mode: 'single',
        defaultSorts,
        highlightColumnWhenActive: true,
        // sorts,
        // onChangeSorts,
      }),
    )
    .use(
      features.columnResize({
        fallbackSize: 120,
        handleActiveBackground: `var(--color-primary-bg-hover)`,
      }),
    );
  // .use(
  //   features.columnResize({
  //     fallbackSize: 120,
  //     minSize: 60,
  //     maxSize: 1080,
  //   }),
  // );

  const onPageNoChange = (pageNo: number) => {
    const config = { ...paginationConfig, pageNo };
    setPaginationConfig(config);
    getTableData({ pageNo });
  };

  const onPageSizeChange = (pageSize: number) => {
    const config = { ...paginationConfig, pageSize, pageNo: 1 };
    setPaginationConfig(config);
    getTableData({ pageSize, pageNo: 1 });
  };

  const onClickTotalBtn = async () => {
    const res = await sqlService.getDMLCount({
      sql: queryResultData.sql,
      ...(props.executeSqlParams || {}),
    });
    setPaginationConfig({ ...paginationConfig, total: res });
    return res;
  };

  // 处理撤销
  const handleRevoke = () => {
    setUpdateData(updateData.filter((item) => item.rowNo !== curOperationRowNo));
    setTableData(
      tableData.map((item) =>
        item[`${preCode}0No.`] === curOperationRowNo
          ? oldTableData.find((i) => i[`${preCode}0No.`] === curOperationRowNo)!
          : item,
      ),
    );
  };

  // 处理创建数据
  const handleCreateData = () => {
    // 如果加的这行数据是删除过的，则恢复
    const index = updateData.findIndex((item) => item.rowNo === curOperationRowNo && item.type === CRUD.DELETE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      return;
    }
    // 正常的新增
    const newTableData = lodash.cloneDeep(tableData);
    const newData = {};
    columns.forEach((t, i) => {
      if (t.name === 'No.') {
        newData[`${preCode}${i}${t.name}`] = (newTableData.length + 1).toString();
      } else {
        newData[`${preCode}${i}${t.name}`] = null;
      }
    });
    newTableData.push(newData);
    setTableData(newTableData);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.CREATE,
        dataList: Object.keys(newData).map((item) => newData[item]),
        rowNo: newTableData.length.toString(),
      },
    ]);

    // 新增一条数据，tableBox需要滚动到最下方
    setTimeout(() => {
      tableBoxRef.current?.scrollTo(0, tableBoxRef.current?.scrollHeight + 31);
    }, 0);
  };

  // 处理删除数据
  const handleDeleteData = () => {
    if (curOperationRowNo === null) {
      return;
    }

    // 如果是新增的行，则直接删除
    const index = updateData.findIndex((item) => item.rowNo === curOperationRowNo && item.type === CRUD.CREATE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      setTableData(tableData.filter((item) => item[`${preCode}0No.`] !== curOperationRowNo));
      return;
    }

    // 正常的删除数据
    const deleteIndex = updateData.findIndex((t) => t.rowNo === curOperationRowNo);
    if (deleteIndex !== -1) {
      updateData.splice(deleteIndex, 1);
    }

    // 如果删除的这个数据时编辑过的，要把这个数据恢复
    setTableData(
      tableData.map((item) =>
        item[`${preCode}0No.`] === curOperationRowNo
          ? oldTableData.find((i) => i[`${preCode}0No.`] === curOperationRowNo)!
          : item,
      ),
    );
    const newDataOldList = oldDataList.find((item) => item[0] === curOperationRowNo);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.DELETE,
        oldDataList: newDataOldList,
        rowNo: curOperationRowNo,
      },
    ]);
    setCurOperationRowNo(null);
  };

  // 查看更新数据的sql
  const handleViewSql = () => {
    if (!updateData.length) {
      return;
    }
    getExecuteUpdateSql().then((res) => {
      setUpdateDataSql(res);
      setViewUpdateDataSqlModal(true);
    });
  };

  // 更新数据的sql
  const handleUpdateSubmit = () => {
    if (!updateData.length) {
      return;
    }
    getExecuteUpdateSql().then((res) => {
      executeUpdateDataSql(res);
    });
  };

  // 获取更新数据的sql
  const getExecuteUpdateSql = () => {
    return new Promise<string>((resolve) => {
      const params = {
        databaseName: props.executeSqlParams?.databaseName,
        dataSourceId: props.executeSqlParams?.dataSourceId,
        schemaName: props.executeSqlParams?.schemaName,
        type: props.executeSqlParams?.databaseType,
        tableName: queryResultData.tableName,
        headerList: queryResultData.headerList,
        operations: updateData,
      };
      sqlService.getExecuteUpdateSql(params).then((res) => {
        resolve(res || '');
      });
    });
  };

  // 执行sql
  const executeUpdateDataSql = (sql: string) => {
    const executeSQLParams: IExecuteSqlParams = {
      sql,
      dataSourceId: props.executeSqlParams?.dataSourceId,
      databaseName: props.executeSqlParams?.databaseName,
      schemaName: props.executeSqlParams?.schemaName,
      tableName: queryResultData.tableName,
    };
    sqlService.executeUpdateDataSql(executeSQLParams).then((res) => {
      if (res?.success) {
        // 更新成功后，需要重新获取表格数据
        getTableData().then(() => {
          message.success(i18n('common.text.successfulExecution'));
          setUpdateData([]);
        });
      } else {
        setUpdateDataSql(res.sql);
        setViewUpdateDataSqlModal(true);
        setInitError(res.message);
      }
    });
  };

  // 获取表格数据 接受一个参数params 包含IExecuteSqlParams中的一个或多个
  const getTableData = (params?: Partial<IExecuteSqlParams>) => {
    const executeSQLParams: IExecuteSqlParams = {
      sql: queryResultData.sql,
      dataSourceId: props.executeSqlParams?.dataSourceId,
      databaseName: props.executeSqlParams?.databaseName,
      schemaName: props.executeSqlParams?.schemaName,
      pageNo: paginationConfig.pageNo,
      pageSize: paginationConfig.pageSize,
      ...(params || {}),
    };

    return sqlService.executeSql(executeSQLParams).then((res) => {
      setQueryResultData(res?.[0]);
      setUpdateData([]);
    });
  };
  // 不同状态下的表格行样式
  // const tableRowStyle = (rowNo: string) => {
  //   // 如果是当前操作的行
  //   if (rowNo === curOperationRowNo) {
  //     return {
  //       '--hover-bgcolor': 'transparent',
  //       '--bgcolor': 'transparent',
  //       background: 'var(--color-primary-bg-hover)',
  //     };
  //   }
  //   // 如果是删除过的行
  //   const index = updateData.findIndex((item) => item.rowNo === rowNo && item.type === CRUD.DELETE);
  //   if (index !== -1) {
  //     return {
  //       '--hover-bgcolor': 'transparent',
  //       '--bgcolor': 'transparent',
  //       background: 'var(--color-error-bg)',
  //     };
  //   }
  //   // 如果是新增的行
  //   const index2 = updateData.findIndex((item) => {
  //     return item.rowNo === rowNo && item.type === CRUD.CREATE;
  //   });
  //   if (index2 !== -1) {
  //     return {
  //       '--hover-bgcolor': 'transparent',
  //       '--bgcolor': 'transparent',
  //       background: 'var(--color-success-bg)',
  //     };
  //   }
  //   return {};
  // };

  // sql执行成功后的回调
  const executeSuccessCallBack = () => {
    getTableData().then(() => {
      setViewUpdateDataSqlModal(false);
      message.success(i18n('common.text.successfulExecution'));
    });
  };

  // 撤销按钮是否可用
  const revokeDisableBarState = useMemo(() => {
    if (!curOperationRowNo) {
      return true;
    }
    return (
      updateData.findIndex(
        (item) =>
          (item.rowNo === curOperationRowNo && item.type === CRUD.UPDATE) ||
          (item.rowNo === curOperationRowNo && item.type === CRUD.DELETE),
      ) === -1
    );
  }, [curOperationRowNo, updateData]);

  const renderContent = () => {
    const bottomStatus = (
      <div className={styles.statusBar}>
        <span>{`【${i18n('common.text.result')}】${queryResultData.description}.`}</span>
        <span>{`【${i18n('common.text.timeConsuming')}】${queryResultData.duration}ms.`}</span>
        <span>{`【${i18n('common.text.searchRow')}】${tableData.length} ${i18n('common.text.row')}.`}</span>
      </div>
    );

    if (!columns.length) {
      return (
        <>
          <StateIndicator state="success" text={i18n('common.text.successfulExecution')} />
          <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0 }}>{bottomStatus}</div>
        </>
      );
    } else {
      return (
        <>
          <div className={styles.toolBar}>
            <div className={styles.toolBarItem}>
              <MyPagination
                paginationConfig={paginationConfig}
                onPageNoChange={onPageNoChange}
                onPageSizeChange={onPageSizeChange}
                onClickTotalBtn={onClickTotalBtn}
              />
            </div>
            {queryResultData.canEdit && (
              <div className={classnames(styles.toolBarItem, styles.editTableDataBar)}>
                <Popover content={i18n('editTableData.tips.addRow')} trigger="hover">
                  <div
                    onClick={handleCreateData}
                    className={classnames(styles.createDataBar, styles.editTableDataBarItem)}
                  >
                    <Iconfont code="&#xe61b;" />
                  </div>
                </Popover>
                <Popover content={i18n('editTableData.tips.deleteRow')} trigger="hover">
                  <div
                    onClick={handleDeleteData}
                    className={classnames(styles.deleteDataBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: curOperationRowNo === null,
                    })}
                  >
                    <Iconfont code="&#xe644;" />
                  </div>
                </Popover>
                <Popover content={i18n('editTableData.tips.revert')} trigger="hover">
                  <div
                    onClick={handleRevoke}
                    className={classnames(styles.revokeBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: revokeDisableBarState,
                    })}
                  >
                    <Iconfont code="&#xe6e2;" />
                  </div>
                </Popover>
                <Popover content={i18n('editTableData.tips.previewPendingChanges')} trigger="hover">
                  <div
                    onClick={handleViewSql}
                    className={classnames(styles.viewSqlBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: !updateData.length,
                    })}
                  >
                    <Iconfont code="&#xe654;" />
                  </div>
                </Popover>
                <Popover content={i18n('editTableData.tips.submit')} trigger="hover">
                  <div
                    onClick={handleUpdateSubmit}
                    className={classnames(styles.updateSubmitBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: !updateData.length,
                    })}
                  >
                    <Iconfont code="&#xe650;" />
                  </div>
                </Popover>
              </div>
            )}
            <div className={styles.toolBarRight}>
              <Dropdown menu={{ items: exportDropdownItems }}>
                <Space className={styles.exportBar}>
                  {i18n('common.text.export')}
                  <DownOutlined />
                </Space>
              </Dropdown>
            </div>
          </div>
          {allDataReady && (
            <div ref={tableBoxRef} className={styles.supportBaseTableBox}>
              <SupportBaseTable
                className={classnames('supportBaseTable', props.className, styles.table)}
                components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}
                isStickyHead
                stickyTop={31}
                getRowProps={(record) => {
                  const rowNo = record[`${preCode}0No.`];
                  return {
                    // style: tableRowStyle(rowNo),
                    onClick() {
                      setCurOperationRowNo(rowNo);
                    },
                  };
                }}
                {...pipeline.getProps()}
              />
            </div>
          )}
          <StatusBar
            description={queryResultData.description}
            duration={queryResultData.duration}
            dataLength={tableData.length}
          />
        </>
      );
    }
  };

  return (
    <div className={classnames(className, styles.tableBox, { [styles.noDataTableBox]: !tableData.length })}>
      {renderContent()}
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        footer={false}
      >
        <div className={styles.monacoEditor}>
          <MonacoEditor
            id="view_table-Cell_data"
            appendValue={{
              text: viewTableCellData?.value,
              range: 'reset',
            }}
            options={{
              lineNumbers: 'off',
              readOnly: true,
            }}
          />
        </div>
      </Modal>
      <Modal
        width="60vw"
        maskClosable={false}
        title={initError ? i18n('common.button.executionError') : i18n('editTable.title.sqlPreview')}
        open={viewUpdateDataSqlModal}
        footer={false}
        destroyOnClose={true}
        onCancel={() => {
          setViewUpdateDataSqlModal(false);
          setUpdateDataSql('');
          setInitError(null);
        }}
      >
        <ExecuteSQL
          initError={initError}
          initSql={updateDataSql}
          databaseName={props.executeSqlParams?.databaseName}
          dataSourceId={props.executeSqlParams?.dataSourceId}
          tableName={queryResultData.tableName}
          schemaName={props.executeSqlParams?.schemaName}
          databaseType={props.executeSqlParams?.databaseType}
          executeSuccessCallBack={executeSuccessCallBack}
          executeSqlApi="executeUpdateDataSql"
        />
      </Modal>
      {contextHolder}
    </div>
  );
}
