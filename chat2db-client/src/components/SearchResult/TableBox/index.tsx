import React, { useEffect, useMemo, useState, useContext } from 'react';
import { Dropdown, Input, MenuProps, message, Modal, Space, Popover, Spin, Button } from 'antd';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import styled from 'styled-components';
import classnames from 'classnames';
import lodash from 'lodash';
import { v4 as uuid } from 'uuid';
import i18n from '@/i18n';

// 样式
import styles from './index.less';

// 工具函数
import { compareStrings } from '@/utils/sort';
import { downloadFile } from '@/utils/common';
import { transformInputValue, useCheckCanPaste } from './utils';

// 类型定义
import { CRUD } from '@/constants';
import { TableDataType } from '@/constants/table';
import { IManageResultData, IResultConfig } from '@/typings/database';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';

// api
import sqlService, { IExportParams, IExecuteSqlParams } from '@/service/sql';

// store
import { useCommonStore } from '@/store/common';

// 依赖组件
import ExecuteSQL from '@/components/ExecuteSQL';
import { DownOutlined } from '@ant-design/icons';
import { copy, tableCopy, clipboardToArray } from '@/utils';
import Iconfont from '../../Iconfont';
import StateIndicator from '../../StateIndicator';
import MonacoEditor from '../../Console/MonacoEditor';
import MyPagination from '../Pagination';
import StatusBar from '../StatusBar';
import RightClickMenu, { AllSupportedMenusType } from '../RightClickMenu';
import { Context } from '../index';

interface ITableProps {
  className?: string;
  outerQueryResultData: IManageResultData;
  executeSqlParams: any;
  tableBoxId: string;
}

interface IViewTableCellData {
  name: string;
  value: any;
  colIndex: number;
  rowNo: string;
}

interface IUpdateData {
  oldDataList?: Array<string | null>;
  dataList?: Array<string | null>;
  type: CRUD;
  rowNo: string;
}

export enum USER_FILLED_VALUE {
  DEFAULT = 'CHAT2DB_UPDATE_TABLE_DATA_USER_FILLED_DEFAULT',
}

const SupportBaseTable: any = styled(BaseTable)`
  &.supportBaseTable {
    --bgcolor: var(--color-bg-base);
    --header-bgcolor: var(--color-bg-subtle);
    --hover-bgcolor: transparent;
    --header-hover-bgcolor: var(--color-bg-subtle);
    --highlight-bgcolor: transparent;
    --header-highlight-bgcolor: var(--color-bg-subtle);
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
  const { className, outerQueryResultData, tableBoxId } = props;
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);
  const [, contextHolder] = message.useMessage();
  const { activeTabIdRef } = useContext(Context);
  const [paginationConfig, setPaginationConfig] = useState<IResultConfig>(defaultPaginationConfig);
  // sql查询结果
  const [queryResultData, setQueryResultData] = useState<IManageResultData>(outerQueryResultData);
  // tableData：带列标识的表数据 可以传给Table组件 进行渲染
  // 保存原始的表数据，用于撤销
  const [oldTableData, setOldTableData] = useState<{ [key: string]: string }[]>([]);
  // 实时更新的表数据
  const [tableData, setTableData] = useState<{ [key: string]: string | null }[]>([]);
  // DataList不带列标识的表数据
  // 保存原始的表数据，用于对比新老数据看是否有变化
  const [oldDataList, setOldDataList] = useState<string[][]>([]);
  // 当前聚焦的单元格的坐标，以及是否正在编辑，为false时，代表正在聚焦，但是没有编辑
  const [editingCell, setEditingCell] = useState<[number, string, boolean] | null>(null);
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
  // monacoEditorRef
  const monacoEditorRef = React.useRef<any>(null);
  // 表格loading
  const [tableLoading, setTableLoading] = useState<boolean>(false);
  // 列宽数组
  const [columnResize, setColumnResize] = useState<number[]>([0]);
  // 表格的宽度
  // const [tableBoxWidth, setTableBoxWidth] = useState<number>(0);
  // 判断是否可以执行cmd+v
  const [canPaste, setCanPaste] = useState<boolean>(false);
  // 判断是否聚焦在了可粘贴的区域中 hooks
  useCheckCanPaste(setCanPaste);
  const { setFocusedContent } = useCommonStore((state) => {
    return {
      setFocusedContent: state.setFocusedContent,
    };
  });

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

  function monacoEditorEditData() {
    const editorData = monacoEditorRef?.current?.getAllContent();
    // 获取原始的该单元格的数据
    // let _oldData = '';
    const { rowNo, colIndex } = viewTableCellData as any;
    oldDataList.forEach((item) => {
      if (item[0] === rowNo) {
        if (item[colIndex] !== editorData) {
          const newTableData = lodash.cloneDeep(tableData);
          let newRowDataList: any = [];
          newTableData.forEach((i) => {
            if (i[`${preCode}0No.`] === rowNo) {
              i[`${preCode}${colIndex}${columns[colIndex].name}`] = editorData;
              newRowDataList = Object.keys(i).map((_i) => i[_i]);
            }
          });
          setTableData(newTableData);

          // 添加更新记录
          setUpdateData([
            ...updateData,
            {
              type: CRUD.UPDATE,
              oldDataList: item,
              dataList: newRowDataList,
              rowNo,
            },
          ]);
        }
        return;
      }
    });
    setViewTableCellData(null);
  }

  function handleCancel() {
    setViewTableCellData(null);
  }

  const handleClickTableItem = (colIndex, rowNo, value, isEditing) => {
    // 1. 如果当前单元格正在编辑，则不需要再次编辑
    // 2. 如果当前单元格正在编辑，则不需要聚焦
    if (editingCell?.[0] === colIndex && editingCell?.[1] === rowNo && editingCell?.[2]) {
      return;
    }
    setFocusedContent(value);
    // 聚焦当前单元格，取消对于行的聚焦
    setCurOperationRowNo(null);
    // 当前聚焦或者编辑的单元格的数据
    setEditingData(value);
    // 当前聚焦或者编辑的单元格的坐标
    setEditingCell([colIndex, rowNo, isEditing]);
    // 如果是编辑状态，则需要聚焦到input
    if (isEditing) {
      setTimeout(() => {
        editDataInputRef?.current?.focus();
      }, 0);
    }
  };

  // 编辑数据
  const updateTableData = (type: 'setCell' | 'setRow', _data: string | null | Array<string | null>) => {
    const newTableData = lodash.cloneDeep(tableData);
    let oldRowDataList: Array<string | null> = [];
    let newRowDataList: Array<string | null> = [];
    let curRowNo: string | null = '0';
    if (type === 'setCell' && (typeof _data === 'string' || _data === null)) {
      const [colIndex, rowNo] = editingCell!;
      curRowNo = rowNo;
      newTableData.forEach((item) => {
        if (item[`${preCode}0No.`] === rowNo) {
          item[`${preCode}${colIndex}${columns[colIndex].name}`] = _data;
          newRowDataList = Object.keys(item).map((i) => item[i]);
        }
      });
    }

    if (type === 'setRow' && Array.isArray(_data)) {
      curRowNo = curOperationRowNo;
      _data.unshift(curOperationRowNo);
      newTableData.forEach((t) => {
        if (t[`${preCode}0No.`] === curOperationRowNo) {
          const dataLength = Object.keys(t).length;
          Object.keys(t).forEach((item, index) => {
            if (index > dataLength) return;
            t[item] = _data[index] || null;
          });
          return;
        }
      });
      newRowDataList = _data;
    }

    setTableData(newTableData);

    oldDataList.forEach((item) => {
      if (item[0] === curRowNo) {
        oldRowDataList = item;
      }
    });

    const index = updateData.findIndex((item) => item.rowNo === curRowNo);
    // 如果newRowDataList和oldRowDataList的数据一样，代表用户虽然编辑过，但是又改回去了，则不需要更新
    if (oldRowDataList?.join(',') === newRowDataList?.join(',')) {
      if (index !== -1) {
        setUpdateData(updateData.filter((item) => item.rowNo !== curRowNo && item.type !== CRUD.UPDATE));
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
          rowNo: curRowNo!,
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
    } else if (value === USER_FILLED_VALUE.DEFAULT) {
      return <span className={styles.cellValueNull}>{'<default>'}</span>;
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
    // 如果当前行中的单元格正在聚焦或编辑
    if (editingCell?.[1] === rowNo) {
      // 设置正在编辑或聚焦的单元格所在行的样式为高亮
      styleList.push(styles.tableItemHighlight);
      // 精确找到列，设置正在编辑或聚焦的单元格的样式为Focus
      if (editingCell?.[0] === colIndex && !editingCell?.[2]) {
        styleList.push(styles.tableItemFocus);
      }
      return classnames(...styleList);
    }
    // 当前单元格所在的行被选中了(行聚焦)
    if (rowNo === curOperationRowNo) {
      // No列的高亮只需要用tableItemHighlight不需要用tableItemFocus
      if (colIndex === 0) {
        styleList.push(styles.tableItemHighlight);
      } else {
        styleList.push(styles.tableItemFocus);
      }
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
      sql: queryResultData.originalSql,
      ...(props.executeSqlParams || {}),
    });
    setPaginationConfig({ ...paginationConfig, total: res });
    return res;
  };

  // 处理撤销
  const handleRevoke = () => {
    // 聚焦行撤销
    if (curOperationRowNo) {
      setUpdateData(updateData.filter((item) => item.rowNo !== curOperationRowNo));
      const oldData = oldTableData.find((i) => i[`${preCode}0No.`] === curOperationRowNo)!;
      const _tableData = tableData.map((item) => (item[`${preCode}0No.`] === curOperationRowNo ? oldData : item));
      setTableData(_tableData);
      setCurOperationRowNo(null);
      return;
    }
    // 聚焦单元格撤销
    if (editingCell && editingCell[2] === false) {
      const oldRowDataList = oldDataList.find((item) => item[0] === editingCell[1]);
      const oldData = oldRowDataList?.[editingCell[0]];
      const _tableData = tableData.map((item) => {
        if (item[`${preCode}0No.`] === editingCell[1]) {
          item[`${preCode}${editingCell[0]}${columns[editingCell[0]].name}`] = oldData || '';
        }
        return item;
      });

      // 如果撤销后这一行的数据和原始数据一样，则删除这条更新记录
      const newRowTableData = _tableData.find((item) => item[`${preCode}0No.`] === editingCell[1])!;
      const newRowDataList = Object.keys(newRowTableData).map((item) => newRowTableData[item]);
      if (lodash.isEqual(newRowDataList, oldRowDataList)) {
        setUpdateData(updateData.filter((item) => item.rowNo !== editingCell[1]));
      }

      setTableData(_tableData);
    }
  };

  // 处理创建数据
  const handleCreateData = (_newData?: any) => {
    // 正常的新增
    const newTableData = lodash.cloneDeep(tableData);
    let newData = {};
    if (_newData) {
      newData = _newData;
    } else {
      columns.forEach((t, i) => {
        if (t.name === 'No.') {
          newData[`${preCode}${i}${t.name}`] = (newTableData.length + 1).toString();
        } else {
          // 判断是否有默认值
          const hasDefaultValue =
            queryResultData.headerList.find((item) => item.name === t.name)?.defaultValue !== null;
          if (hasDefaultValue) {
            newData[`${preCode}${i}${t.name}`] = USER_FILLED_VALUE.DEFAULT;
            return;
          }
          newData[`${preCode}${i}${t.name}`] = null;
        }
      });
    }
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
    setCurOperationRowNo(newTableData.length.toString());
    setEditingCell(null);

    // 新增一条数据，tableBox需要滚动到最下方
    setTimeout(() => {
      tableBoxRef.current?.scrollTo(0, tableBoxRef.current?.scrollHeight + 31);
    }, 0);
  };

  // 处理删除数据
  const handleDeleteData = () => {
    const rowNo = curOperationRowNo || editingCell?.[1];
    if (rowNo === null) {
      return;
    }
    // 如果是新增的行，则直接删除
    const index = updateData.findIndex((item) => item.rowNo === rowNo && item.type === CRUD.CREATE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      setTableData(tableData.filter((item) => item[`${preCode}0No.`] !== rowNo));
      setCurOperationRowNo(null);
      return;
    }

    // 正常的删除数据
    const deleteIndex = updateData.findIndex((t) => t.rowNo === rowNo);
    if (deleteIndex !== -1) {
      updateData.splice(deleteIndex, 1);
    }

    // 如果删除的这个数据时编辑过的，要把这个数据恢复
    setTableData(
      tableData.map((item) =>
        item[`${preCode}0No.`] === rowNo ? oldTableData.find((i) => i[`${preCode}0No.`] === rowNo)! : item,
      ),
    );
    const newDataOldList = oldDataList.find((item) => item[0] === rowNo);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.DELETE,
        oldDataList: newDataOldList,
        rowNo: rowNo!,
      },
    ]);
    setEditingCell(null);
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
    if (!updateData.length || tableLoading) {
      return;
    }
    setTableLoading(true);
    getExecuteUpdateSql()
      .then((res) => {
        executeUpdateDataSql(res);
      })
      .catch(() => {
        setTableLoading(false);
      });
  };

  // 获取更新数据的sql
  const getExecuteUpdateSql = (_updateData?: any) => {
    return new Promise<string>((resolve) => {
      const params = {
        databaseName: props.executeSqlParams?.databaseName,
        dataSourceId: props.executeSqlParams?.dataSourceId,
        schemaName: props.executeSqlParams?.schemaName,
        type: props.executeSqlParams?.databaseType,
        tableName: queryResultData.tableName,
        headerList: queryResultData.headerList,
        operations: _updateData || updateData,
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
    sqlService
      .executeUpdateDataSql(executeSQLParams)
      .then((res) => {
        if (res?.success) {
          // 更新成功后，需要重新获取表格数据
          getTableData().then(() => {
            message.success(i18n('common.text.successfulExecution'));
            setUpdateData([]);
          });
        } else {
          setUpdateDataSql(res?.sql);
          setViewUpdateDataSqlModal(true);
          setInitError(res.message);
        }
      })
      .finally(() => {
        setTableLoading(false);
      });
  };

  // 获取表格数据 接受一个参数params 包含IExecuteSqlParams中的一个或多个
  const getTableData = (params?: Partial<IExecuteSqlParams>) => {
    setTableLoading(true);
    const executeSQLParams: IExecuteSqlParams = {
      sql: queryResultData.originalSql,
      dataSourceId: props.executeSqlParams?.dataSourceId,
      databaseName: props.executeSqlParams?.databaseName,
      schemaName: props.executeSqlParams?.schemaName,
      pageNo: paginationConfig.pageNo,
      pageSize: paginationConfig.pageSize,
      ...(params || {}),
    };

    return sqlService.executeSql(executeSQLParams).then((res) => {
      setTableLoading(false);
      setQueryResultData(res?.[0]);
      setUpdateData([]);
    });
  };

  // sql执行成功后的回调
  const executeSuccessCallBack = () => {
    getTableData().then(() => {
      setViewUpdateDataSqlModal(false);
      message.success(i18n('common.text.successfulExecution'));
    });
  };

  // 撤销按钮是否可用
  const revokeDisableBarState = useMemo(() => {
    // 如果有聚焦的行，但是没有操作过的数据，则不可用
    if (curOperationRowNo) {
      return (
        updateData.findIndex(
          (item) =>
            (item.rowNo === curOperationRowNo && item.type === CRUD.UPDATE) ||
            (item.rowNo === curOperationRowNo && item.type === CRUD.DELETE),
        ) === -1
      );
    }
    // 如果有聚焦的单元格
    if (editingCell && editingCell[2] === false) {
      const oldRowDataList = oldDataList.find((item) => item[0] === editingCell[1]);
      const oldData = oldRowDataList?.[editingCell[0]];
      // 如果当前单元格的数据和老数据一样，则可用
      if (oldData !== editingData) {
        return false;
      }
    }
    // 如果都没，那撤销按钮不可用
    return true;
  }, [curOperationRowNo, updateData, editingCell]);

  const handelRowNoClick = (rowNo: string) => {
    setEditingCell(null);
    setCurOperationRowNo(rowNo);
    const newRowData = tableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
    const newRowDataList = Object.keys(newRowData).map((item) => newRowData[item]);
    newRowDataList.splice(0, 1);
    setFocusedContent([newRowDataList]);
  };

  useEffect(() => {
    const handleCopy = () => {
      if (curOperationRowNo) {
        navigator.clipboard
          .readText()
          .then((text) => {
            const array2D = clipboardToArray(text);
            updateTableData('setRow', array2D[0]);
          })
          .catch((err) => {
            console.error('Failed to read clipboard contents: ', err);
          });
      }
      if (editingCell && editingCell[2] === false) {
        navigator.clipboard
          .readText()
          .then((text) => {
            updateTableData('setCell', text);
          })
          .catch((err) => {
            console.error('Failed to read clipboard contents: ', err);
          });
      }
    };
    if (canPaste) {
      document.addEventListener('paste', handleCopy);
    } else {
      document.removeEventListener('paste', handleCopy);
    }
    return () => {
      document.removeEventListener('paste', handleCopy);
    };
  }, [curOperationRowNo, editingCell, canPaste]);

  // 表格 列配置
  const columns: ArtColumn[] = useMemo(() => {
    return (queryResultData.headerList || []).map((item, colIndex) => {
      const { dataType, name } = item;
      const isNumber = dataType === TableDataType.NUMERIC;
      const isNumericalOrder = dataType === TableDataType.CHAT2DB_ROW_NUMBER;
      if (isNumericalOrder) {
        return {
          code: `${preCode}0No.`,
          name: 'No.',
          title: <div />,
          key: name,
          lock: true,
          // features: { sortable: compareStrings },
          render: (value: any, rowData) => {
            const rowNo = rowData[`${preCode}0No.`];
            return (
              <div
                data-chat2db-general-can-copy-element
                data-chat2db-edit-table-data-can-paste
                data-chat2db-edit-table-data-can-right-click
                onClick={() => {
                  handelRowNoClick(rowNo);
                }}
                onContextMenu={() => {
                  handelRowNoClick(rowNo);
                }}
                className={tableCellStyle(value, colIndex, rowNo)}
              >
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
        // title: <div>{name}</div>,
        render: (value: any, rowData) => {
          const rowNo = rowData[`${preCode}0No.`];
          return (
            <div
              data-chat2db-general-can-copy-element
              data-chat2db-edit-table-data-can-paste
              data-chat2db-edit-table-data-can-right-click
              className={tableCellStyle(value, colIndex, rowNo)}
              onClick={handleClickTableItem.bind(null, colIndex, rowNo, value, false)}
              onDoubleClick={handleClickTableItem.bind(null, colIndex, rowNo, value, true)}
              onContextMenu={handleClickTableItem.bind(null, colIndex, rowNo, value, false)}
            >
              {editingCell?.[0] === colIndex && editingCell?.[1] === rowNo && editingCell?.[2] ? (
                <Input
                  ref={editDataInputRef}
                  value={transformInputValue(editingData) as any}
                  onChange={(e) => {
                    setEditingData(e.target.value);
                  }}
                  onBlur={() => {
                    setEditingCell([editingCell![0], editingCell![1], false]);
                    updateTableData('setCell', editingData);
                  }}
                />
              ) : (
                <>
                  <div className={styles.tableItemContent}>{renderTableCellValue(value)}</div>
                  {/* <div className={styles.tableHoverBox}>
                    <Iconfont
                      code="&#xe606;"
                      onClick={viewTableCell.bind(null, { name: item.name, value, colIndex, rowNo })}
                    />
                    <Iconfont
                      code="&#xeb4e;"
                      onClick={copyTableCell.bind(null, { name: item.name, value, colIndex, rowNo })}
                    />
                  </div> */}
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
        fallbackSize: 150,
        // handleBackground: '#ddd',
        handleHoverBackground: `var(--color-primary-bg-hover)`,
        handleActiveBackground: `var(--color-primary-bg-hover)`,
        minSize: 60,
        maxSize: 1080,
        sizes: columnResize,
        onChangeSizes: (sizes) => {
          sizes[0] = 0;
          setColumnResize(sizes);
        },
      }),
    );

  // 右键菜单配置项
  const copyRow = {
    key: AllSupportedMenusType.CopyRow,
    children: [
      {
        callback: () => {
          const rowNo = curOperationRowNo || editingCell![1];
          const newRowData = tableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
          const newRowDataList = Object.keys(newRowData).map((item) => newRowData[item]);
          const _updateData = {
            type: CRUD.CREATE,
            dataList: newRowDataList,
            rowNo: (tableData.length + 1).toString(),
          };
          getExecuteUpdateSql([_updateData]).then((res) => {
            copy(res);
          });
        },
        hide: !queryResultData.canEdit,
      },
      {
        callback: () => {
          const rowNo = curOperationRowNo || editingCell![1];
          const newRowData = tableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
          const newRowDataList = Object.keys(newRowData).map((item) => newRowData[item]);
          const _updateData = {
            type: CRUD.UPDATE_COPY,
            dataList: newRowDataList,
            rowNo: (tableData.length + 1).toString(),
          };
          getExecuteUpdateSql([_updateData]).then((res) => {
            copy(res);
          });
        },
        hide: !queryResultData.canEdit,
      },
      // 复制当前行的数据
      {
        callback: () => {
          const rowNo = curOperationRowNo || editingCell![1];
          const newRowData = tableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
          const newRowDataList = Object.keys(newRowData).map((item) => newRowData[item]);
          // 去掉No列
          newRowDataList.splice(0, 1);
          tableCopy([newRowDataList]);
        },
      },
      // 复制表头
      {
        callback: () => {
          const headerList = queryResultData.headerList.map((item) => item.name);
          // 去掉No列
          headerList.splice(0, 1);
          tableCopy([headerList]);
        },
      },
      // 复制表头和当前行的数据
      {
        callback: () => {
          const rowNo = curOperationRowNo || editingCell![1];
          const headerList = queryResultData.headerList.map((item) => item.name);
          const newRowData = tableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
          const newRowDataList = Object.keys(newRowData).map((item) => newRowData[item]);
          // 去掉No列
          headerList.splice(0, 1);
          const array2D = [headerList, newRowDataList];
          tableCopy(array2D);
        },
      },
    ],
  };

  const cloneRow = {
    key: AllSupportedMenusType.CloneRow,
    callback: () => {
      const newTableData = lodash.cloneDeep(tableData);
      const rowNo = curOperationRowNo || editingCell![1];
      const newRowData = newTableData.find((item) => item[`${preCode}0No.`] === rowNo)!;
      newRowData[`${preCode}0No.`] = (newTableData.length + 1).toString();
      handleCreateData(newRowData);
    },
  };

  const deleteRow = {
    key: AllSupportedMenusType.DeleteRow,
    callback: handleDeleteData,
  };

  const copyCell = {
    key: AllSupportedMenusType.CopyCell,
    callback: () => {
      copy(editingData);
    },
  };

  const setDefault = {
    key: AllSupportedMenusType.SetDefault,
    callback: () => {
      updateTableData('setCell', USER_FILLED_VALUE.DEFAULT);
    },
  };

  const setNull = {
    key: AllSupportedMenusType.SetNull,
    callback: () => {
      updateTableData('setCell', null);
    },
  };

  const viewData = {
    key: AllSupportedMenusType.ViewData,
    callback: () => {
      setViewTableCellData({
        name: columns[editingCell![0]].name,
        value: editingData,
        colIndex: editingCell![0],
        rowNo: editingCell![1],
      });
    },
  };
  const rowRightClickMenu = useMemo(() => {
    // const allSupportedMenus = {
    //   [AllSupportedMenusType.CopyCell]: copyCell,
    //   [AllSupportedMenusType.CopyRow]: copyRow,
    //   [AllSupportedMenusType.CloneRow]: cloneRow,
    //   [AllSupportedMenusType.DeleteRow]: deleteRow,
    //   [AllSupportedMenusType.SetDefault]: setDefault,
    //   [AllSupportedMenusType.SetNull]: setNull,
    //   [AllSupportedMenusType.ViewData]: viewData,
    // }

    let rightClickMenu: any = [];
    if (curOperationRowNo) {
      rightClickMenu = [copyRow, cloneRow, deleteRow];
      // 如果当前数据不可编辑，则不显示cloneRow和deleteRow
      if (!queryResultData.canEdit) {
        rightClickMenu = rightClickMenu.filter(
          (i) => i.key !== AllSupportedMenusType.CloneRow && i.key !== AllSupportedMenusType.DeleteRow,
        );
      }
    }

    if (editingCell) {
      rightClickMenu = [viewData, copyCell, copyRow, cloneRow, setNull, setDefault, deleteRow];
      // 判断是否有默认值,如果没有默认值，则不显示设置默认值的菜单
      const hasDefaultValue =
        queryResultData.headerList.find((item) => item.name === columns[editingCell![0]].name)?.defaultValue !== null;
      if (!hasDefaultValue) {
        rightClickMenu = rightClickMenu.filter((i) => i.key !== AllSupportedMenusType.SetDefault);
      }
      // 如果当前数据不可编辑，则不显示cloneRow和deleteRow
      if (!queryResultData.canEdit) {
        rightClickMenu = rightClickMenu.filter(
          (i) =>
            i.key !== AllSupportedMenusType.CloneRow &&
            i.key !== AllSupportedMenusType.DeleteRow &&
            i.key !== AllSupportedMenusType.SetNull,
        );
      }
    }

    if (!curOperationRowNo && !editingCell) {
      return null;
    }
    return rightClickMenu;
  }, [curOperationRowNo, editingCell]);

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
            <div className={classnames(styles.toolBarItem, styles.refreshBar)}>
              {/* 刷新 */}
              <Popover mouseEnterDelay={0.8} content={i18n('common.button.refresh')} trigger="hover">
                <div
                  onClick={() => {
                    getTableData();
                  }}
                  className={classnames(styles.refreshIconBox)}
                >
                  <Iconfont code="&#xe62d;" />
                </div>
              </Popover>
            </div>
            {queryResultData.canEdit && (
              <div className={classnames(styles.toolBarItem, styles.editTableDataBar)}>
                {/* 新增行 */}
                <Popover mouseEnterDelay={0.8} content={i18n('editTableData.tips.addRow')} trigger="hover">
                  <div
                    onClick={() => {
                      handleCreateData();
                    }}
                    className={classnames(styles.createDataBar, styles.editTableDataBarItem)}
                  >
                    <Iconfont code="&#xe61b;" />
                  </div>
                </Popover>
                {/* 删除行 */}
                <Popover mouseEnterDelay={0.8} content={i18n('editTableData.tips.deleteRow')} trigger="hover">
                  <div
                    onClick={handleDeleteData}
                    className={classnames(styles.deleteDataBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: curOperationRowNo === null,
                    })}
                  >
                    <Iconfont code="&#xe644;" />
                  </div>
                </Popover>
                {/* 撤销 */}
                <Popover mouseEnterDelay={0.8} content={i18n('editTableData.tips.revert')} trigger="hover">
                  <div
                    onClick={handleRevoke}
                    className={classnames(styles.revokeBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: revokeDisableBarState,
                    })}
                  >
                    <Iconfont code="&#xe6e2;" />
                  </div>
                </Popover>
                {/* 查看更改sql */}
                <Popover
                  mouseEnterDelay={0.8}
                  content={i18n('editTableData.tips.previewPendingChanges')}
                  trigger="hover"
                >
                  <div
                    onClick={handleViewSql}
                    className={classnames(styles.viewSqlBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: !updateData.length,
                    })}
                  >
                    <Iconfont code="&#xe654;" />
                  </div>
                </Popover>
                {/* 提交 */}
                <Popover mouseEnterDelay={0.8} content={i18n('editTableData.tips.submit')} trigger="hover">
                  <div
                    onClick={handleUpdateSubmit}
                    className={classnames(styles.updateSubmitBar, styles.editTableDataBarItem, {
                      [styles.disableBar]: !updateData.length,
                    })}
                  >
                    <Iconfont code="&#xe687;" />
                  </div>
                </Popover>
              </div>
            )}
            <div className={styles.toolBarRight}>
              <Dropdown menu={{ items: exportDropdownItems }} trigger={['click']}>
                <Space className={styles.exportBar}>
                  {i18n('common.text.export')}
                  <DownOutlined />
                </Space>
              </Dropdown>
            </div>
          </div>
          <RightClickMenu menuList={rowRightClickMenu}>
            <div
              ref={tableBoxRef}
              className={classnames(styles.supportBaseTableBox, { [styles.supportBaseTableBoxHidden]: tableLoading })}
            >
              {allDataReady && (
                <>
                  {tableLoading && <Spin className={styles.supportBaseTableSpin} />}
                  <SupportBaseTable
                    className={classnames('supportBaseTable', props.className, styles.table)}
                    components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}
                    isStickyHead
                    stickyTop={31}
                    {...pipeline.getProps()}
                  />
                </>
              )}
            </div>
          </RightClickMenu>
          <StatusBar
            description={queryResultData.description}
            duration={queryResultData.duration}
            dataLength={tableData.length}
          />
        </>
      );
    }
  };

  const renderMonacoEditor = useMemo(() => {
    return (
      <div className={styles.monacoEditor}>
        <MonacoEditor
          ref={monacoEditorRef}
          id={`view_table-Cell_data-${uuid()}`}
          appendValue={{
            text: transformInputValue(viewTableCellData?.value),
            range: 'reset',
          }}
          options={{
            lineNumbers: 'off',
            readOnly: !queryResultData.canEdit,
          }}
        />
      </div>
    );
  }, [queryResultData, viewTableCellData]);

  return (
    <div className={classnames(className, styles.tableBox, { [styles.noDataTableBox]: !tableData.length })}>
      {activeTabIdRef?.current === tableBoxId && renderContent()}
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        destroyOnClose={true}
        footer={
          queryResultData.canEdit && [
            <Button key="1" type="primary" onClick={monacoEditorEditData}>
              {i18n('common.button.modify')}
            </Button>,
          ]
        }
      >
        {renderMonacoEditor}
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
