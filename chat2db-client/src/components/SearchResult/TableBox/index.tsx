import React, { useEffect, useMemo, useState } from 'react';
import { Dropdown, Input, MenuProps, message, Modal, Space } from 'antd';
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
import styles from './index.less';
import sqlService, { IExportParams, IExecuteSqlParams } from '@/service/sql';
import { downloadFile } from '@/utils/common';
import lodash from 'lodash';

interface ITableProps {
  className?: string;
  data: IManageResultData;
  config: IResultConfig;
  onConfigChange: (config: IResultConfig) => void;
  onSearchTotal: () => Promise<number | undefined>;
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

// const defaultResultConfig: IResultConfig = {
//   pageNo: 1,
//   pageSize: 200,
//   total: 0,
//   hasNextPage: true,
// };

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
    --cell-padding: 0px 4px;
    --row-height: 32px;
  }
`;

const preCode = '$$chat2db_';

export default function TableBox(props: ITableProps) {
  const { className, data, config, onConfigChange } = props;
  const { headerList, dataList, duration, description } = data || {};
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);
  const [messageApi, contextHolder] = message.useMessage();
  const [tableData, setTableData] = useState<any[]>([]);
  const [oldTableData, setOldTableData] = useState<any[]>([]);
  const [editingCell, setEditingCell] = useState<[string, string] | null>(null);
  const [editingData, setEditingData] = useState<string>('');
  const [curOperationRowNo, setCurOperationRowNo] = useState<string | null>(null);
  const [updateData, setUpdateData] = useState<IUpdateData[] | []>([]);
  const [updateDataSql, setUpdateDataSql] = useState<string>('');
  const [initError, setInitError] = useState<string>('');
  const [viewUpdateDataSql, setViewUpdateDataSql] = useState<boolean>(false);
  const tableBoxRef = React.useRef<HTMLDivElement>(null);
  const [oldDataList, setOldDataList] = useState<string[][]>([]);

  const handleExportSQLResult = async (exportType: ExportTypeEnum, exportSize: ExportSizeEnum) => {
    const params: IExportParams = {
      ...(props.executeSqlParams || {}),
      sql: data.sql,
      originalSql: data.originalSql,
      exportType,
      exportSize,
    };
    downloadFile(window._BaseURL + '/api/rdb/dml/export', params);
  };

  useEffect(() => {
    if (data.dataList?.length) {
      setOldDataList(data.dataList);
    }
  }, [data.dataList]);

  const items: MenuProps['items'] = useMemo(
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
    [data],
  );

  const defaultSorts: SortItem[] = useMemo(
    () =>
      (headerList || []).map((item) => ({
        code: item.name,
        order: 'none',
      })),
    [headerList],
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
    if (!data.canEdit) {
      return;
    }
    setEditingData(value);
    setEditingCell([colIndex, rowNo]);
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

  const renderTableCellValue = (value) => {
    if (value === null) {
      return '<null>';
    } else {
      return value;
    }
  };

  // 每个单元格的样式
  const tableCellStyle = (value, colIndex, rowNo) => {
    // 单元格的基础样式
    const styleList = [styles.tableItem];
    // 如果当前单元格所在的行被选中了，则需要把单元格的背景色设置为透明
    if (curOperationRowNo === rowNo) {
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

  const columns: ArtColumn[] = useMemo(() => {
    return (headerList || []).map((item, colIndex) => {
      const { dataType, name } = item;
      const isNumber = dataType === TableDataType.NUMERIC;
      const isNumericalOrder = dataType === TableDataType.CHAT2DB_ROW_NUMBER;
      if (isNumericalOrder) {
        return {
          code: `${preCode}${colIndex}No.`,
          name: 'No.',
          key: name,
          lock: true,
          width: 48,
          features: { sortable: compareStrings },
          render: (value: any) => {
            return (
              <div className={styles.tableItem}>
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
        width: 120,
        render: (value: any, rowData) => {
          const rowNo = rowData[`${preCode}0No.`];
          return (
            <div
              className={tableCellStyle(value, colIndex, rowNo)}
              onDoubleClick={handleDoubleClickTableItem.bind(null, colIndex, rowNo, value)}
            >
              {editingCell?.join(',') === `${colIndex},${rowNo}` ? (
                <Input
                  value={editingData}
                  onChange={(e) => {
                    setEditingData(e.target.value);
                  }}
                  onBlur={editDataOnBlur}
                />
              ) : (
                <>
                  {renderTableCellValue(value)}
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
  }, [headerList, editingCell, editingData, curOperationRowNo, oldDataList]);

  useEffect(() => {
    if (!columns?.length) {
      setTableData([]);
    } else {
      const newTableData = dataListTransformTableData(dataList);
      setTableData(newTableData);
      setOldTableData(newTableData);
    }
  }, [dataList]);

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
        minSize: 60,
        maxSize: 1080,
      }),
    );

  const onPageNoChange = (pageNo: number) => {
    onConfigChange && onConfigChange({ ...config, pageNo });
  };

  const onPageSizeChange = (pageSize: number) => {
    onConfigChange && onConfigChange({ ...config, pageSize, pageNo: 1 });
  };

  const onClickTotalBtn = async () => {
    if (props.onSearchTotal) {
      return await props.onSearchTotal();
    }
  };

  // 处理撤销
  const handleRevoke = () => {
    setUpdateData(updateData.filter((item) => item.rowNo !== curOperationRowNo));
    setTableData(
      tableData.map((item) =>
        item[`${preCode}0No.`] === curOperationRowNo
          ? oldTableData.find((i) => i[`${preCode}0No.`] === curOperationRowNo)
          : item,
      ),
    );
  };

  // 处理创建数据
  const handelCreateData = () => {
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
  const handelDeleteData = () => {
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
    setUpdateData([
      ...updateData,
      {
        type: CRUD.DELETE,
        oldDataList: oldDataList[curOperationRowNo],
        rowNo: curOperationRowNo,
      },
    ]);
    setCurOperationRowNo(null);
  };

  // 查看更新数据的sql
  const handelViewSql = () => {
    if (!updateData.length) {
      return;
    }
    getExecuteUpdateSql().then((res) => {
      setUpdateDataSql(res);
      setViewUpdateDataSql(true);
    });
  };

  // 更新数据的sql
  const handelUpdateSubmit = () => {
    if (!updateData.length) {
      return;
    }
    getExecuteUpdateSql().then((res) => {
      executeSql(res);
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
        tableName: data.tableName,
        headerList,
        operations: updateData,
      };
      sqlService.getExecuteUpdateSql(params).then((res) => {
        resolve(res || '');
      });
    });
  };

  // 执行sql
  const executeSql = (sql: string) => {
    const executeSQLParams: IExecuteSqlParams = {
      sql,
      dataSourceId: props.executeSqlParams?.dataSourceId,
      databaseName: props.executeSqlParams?.databaseName,
      schemaName: props.executeSqlParams?.schemaName,
      tableName: data.tableName,
    };
    sqlService.executeDDL(executeSQLParams).then((res) => {
      if (res.success) {
        getTableData().then((sqlRes) => {
          setOldDataList(sqlRes?.[0]?.dataList);
          const newTableData = dataListTransformTableData(sqlRes?.[0]?.dataList);
          setTableData(newTableData);
          setOldTableData(newTableData);
          message.success(i18n('common.text.successfulExecution'));
          setUpdateData([]);
        });
      } else {
        setUpdateDataSql(res.originalSql);
        setViewUpdateDataSql(true);
        setInitError(res.message);
      }
    });
  };

  // 获取表格数据
  const getTableData = async () => {
    const executeSQLParams: IExecuteSqlParams = {
      sql: data.sql,
      dataSourceId: props.executeSqlParams?.dataSourceId,
      databaseName: props.executeSqlParams?.databaseName,
      schemaName: props.executeSqlParams?.schemaName,
    };

    return sqlService.executeSql(executeSQLParams);
  };

  // 不同状态下的表格行样式
  const tableRowStyle = (rowNo: string) => {
    // 如果是当前操作的行
    if (rowNo === curOperationRowNo) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'linear-gradient(140deg, #ff000038, #009cff3d)',
      };
    }
    // 如果是删除过的行
    const index = updateData.findIndex((item) => item.rowNo === rowNo && item.type === CRUD.DELETE);
    if (index !== -1) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'var(--color-error-bg)',
      };
    }
    // 如果是新增的行
    const index2 = updateData.findIndex((item) => {
      return item.rowNo === rowNo && item.type === CRUD.CREATE;
    });
    if (index2 !== -1) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'var(--color-success-bg)',
      };
    }
    return {};
  };

  // sql执行成功后的回调
  const executeSuccessCallBack = () => {
    setViewUpdateDataSql(false);
    message.success(i18n('common.text.successfulExecution'));
    setUpdateData([]);
  };

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
        <span>{`【${i18n('common.text.result')}】${description}.`}</span>
        <span>{`【${i18n('common.text.timeConsuming')}】${duration}ms.`}</span>
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
                data={config}
                onPageNoChange={onPageNoChange}
                onPageSizeChange={onPageSizeChange}
                onClickTotalBtn={onClickTotalBtn}
              />
            </div>
            {data.canEdit && (
              <div className={classnames(styles.toolBarItem, styles.editTableDataBar)}>
                <div
                  onClick={handelCreateData}
                  className={classnames(styles.createDataBar, styles.editTableDataBarItem)}
                >
                  <Iconfont code="&#xe61b;" />
                </div>
                <div
                  onClick={handelDeleteData}
                  className={classnames(styles.deleteDataBar, styles.editTableDataBarItem, {
                    [styles.disableBar]: curOperationRowNo === null,
                  })}
                >
                  <Iconfont code="&#xe644;" />
                </div>
                <div
                  onClick={handleRevoke}
                  className={classnames(styles.revokeBar, styles.editTableDataBarItem, {
                    [styles.disableBar]: revokeDisableBarState,
                  })}
                >
                  <Iconfont code="&#xe6e2;" />
                </div>
                <div
                  onClick={handelViewSql}
                  className={classnames(styles.viewSqlBar, styles.editTableDataBarItem, {
                    [styles.disableBar]: !updateData.length,
                  })}
                >
                  <Iconfont code="&#xe651;" />
                </div>
                <div
                  onClick={handelUpdateSubmit}
                  className={classnames(styles.updateSubmitBar, styles.editTableDataBarItem, {
                    [styles.disableBar]: !updateData.length,
                  })}
                >
                  <Iconfont code="&#xe650;" />
                </div>
              </div>
            )}
            <div className={styles.toolBarRight}>
              <Dropdown menu={{ items }}>
                <Space className={styles.exportBar}>
                  {i18n('common.text.export')}
                  <DownOutlined />
                </Space>
              </Dropdown>
            </div>
          </div>
          <SupportBaseTable
            className={classnames('supportBaseTable', props.className, styles.table)}
            components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}
            isStickyHead
            stickyTop={31}
            getRowProps={(record) => {
              const rowNo = record[`${preCode}0No.`];
              return {
                style: tableRowStyle(rowNo),
                onClick() {
                  setCurOperationRowNo(rowNo);
                },
              };
            }}
            {...pipeline.getProps()}
          />
          {bottomStatus}
        </>
      );
    }
  };

  return (
    <div ref={tableBoxRef} className={classnames(className, styles.tableBox)}>
      {renderContent()}
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        footer={false}
        // footer={
        //   <>
        //     {
        //       <Button onClick={copyTableCell.bind(null, viewTableCellData!)} className={styles.cancel}>
        //         {i18n('common.button.copy')}
        //       </Button>
        //     }
        //   </>
        // }
      >
        <div className={styles.monacoEditor}>
          <MonacoEditor
            id="view_table-Cell_data"
            appendValue={{
              text: viewTableCellData?.value,
              range: 'reset',
            }}
            options={{
              readOnly: true,
            }}
          />
        </div>
      </Modal>
      <Modal
        width="60vw"
        maskClosable={false}
        title={initError ? i18n('common.button.executionError') : i18n('editTable.title.sqlPreview')}
        open={viewUpdateDataSql}
        footer={false}
        destroyOnClose={true}
        onCancel={() => {
          setViewUpdateDataSql(false);
          setUpdateDataSql('');
          setInitError('');
        }}
      >
        <ExecuteSQL
          initError={initError}
          initSql={updateDataSql}
          databaseName={props.executeSqlParams?.databaseName}
          dataSourceId={props.executeSqlParams?.dataSourceId}
          tableName={data.tableName}
          schemaName={props.executeSqlParams?.schemaName}
          databaseType={props.executeSqlParams?.databaseType}
          executeSuccessCallBack={executeSuccessCallBack}
        />
      </Modal>
      {contextHolder}
    </div>
  );
}
