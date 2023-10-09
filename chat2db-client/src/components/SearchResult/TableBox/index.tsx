import React, { useEffect, useMemo, useState } from 'react';
import { Button, Dropdown, Input, MenuProps, message, Modal, Space } from 'antd';
import { BaseTable, ArtColumn, useTablePipeline, features, SortItem } from 'ali-react-table';
import styled from 'styled-components';
import classnames from 'classnames';
import i18n from '@/i18n';
import { CRUD } from '@/constants';
import { TableDataType } from '@/constants/table';
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
import sqlService, { IExportParams } from '@/service/sql';
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
  index: number;
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
  const [editingCell, setEditingCell] = useState<[number, number] | null>(null);
  const [editingData, setEditingData] = useState<string>('');
  const [curOperationRowIndex, setCurOperationRowIndex] = useState<number>(-1);
  const [updateData, setUpdateData] = useState<IUpdateData[] | []>([]);

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

  const handleDoubleClickTableItem = (colIndex, rowIndex, value) => {
    if (!data.canEdit) {
      return;
    }
    setEditingData(value);
    setEditingCell([colIndex, rowIndex]);
  };

  // 编辑数据失焦
  const editDataOnBlur = () => {
    setEditingCell(null);
    setEditingData('');
    const [colIndex, rowIndex] = editingCell!;
    const newTableData = lodash.cloneDeep(tableData);
    newTableData[rowIndex][`${preCode}${colIndex}${columns[colIndex].name}`] = editingData;
    setTableData(newTableData);
    // 如果已经存在该行的更新数据，则更新，否则新增
    const index = updateData.findIndex((item) => item.index === rowIndex);
    if (index === -1) {
      setUpdateData([
        ...updateData,
        {
          type: CRUD.UPDATE,
          oldDataList: dataList[rowIndex],
          dataList: Object.keys(newTableData[rowIndex]).map((item) => newTableData[rowIndex][item]),
          index: rowIndex,
        },
      ]);
    } else {
      updateData[index] = {
        ...updateData[index],
        dataList: Object.keys(newTableData[rowIndex]).map((item) => newTableData[rowIndex][item]),
      };
      setUpdateData([...updateData]);
    }
  };

  const renderTableCellValue = (value) => {
    if (value === null) {
      return '<null>';
    } else {
      return value;
    }
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
        render: (value: any, a, rowIndex) => {
          return (
            <div
              className={styles.tableItem}
              onDoubleClick={handleDoubleClickTableItem.bind(null, colIndex, rowIndex, value)}
            >
              {editingCell?.join(',') === `${colIndex},${rowIndex}` ? (
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
  }, [headerList, editingCell, editingData]);

  useEffect(() => {
    if (!columns?.length) {
      setTableData([]);
    } else {
      const newData = (dataList || []).map((item) => {
        const rowData: any = {};
        item.map((i: string | null, colIndex: number) => {
          const name = `${preCode}${colIndex}${columns[colIndex].name}`;
          rowData[name] = i;
        });
        return rowData;
      });
      setTableData(newData);
    }
  }, [dataList]);

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

  const handelCreateData = () => {
    // 如果加的这行数据是删除过的，则恢复
    const index = updateData.findIndex((item) => item.index === curOperationRowIndex && item.type === CRUD.DELETE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      return;
    }
    // 正常的新增
    const newTableData = lodash.cloneDeep(tableData);
    const newData = {};
    columns.forEach((item, index) => {
      if (item.name === 'No.') {
        newData[`${preCode}${index}${item.name}`] = newTableData.length + 1;
      } else {
        newData[`${preCode}${index}${item.name}`] = null;
      }
    });
    newTableData.push(newData);
    setTableData(newTableData);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.CREATE,
        dataList: Object.keys(newData).map((item) => newData[item]),
        index: newTableData.length - 1,
      },
    ]);
  };

  useEffect(() => {
    console.log('updateData', updateData);
  }, [updateData]);

  const handelDeleteData = () => {
    if (curOperationRowIndex === -1) {
      return;
    }
    // 如果是新增的行，则直接删除
    const index = updateData.findIndex((item) => item.index === curOperationRowIndex && item.type === CRUD.CREATE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      setTableData(tableData.filter((item, index) => index !== curOperationRowIndex));
      return;
    }
    const index2 = updateData.findIndex((item) => item.index === curOperationRowIndex);
    if (index2 === -1) {
      setUpdateData([
        ...updateData,
        {
          type: CRUD.DELETE,
          oldDataList: dataList[curOperationRowIndex],
          index: curOperationRowIndex,
        },
      ]);
    }
    setCurOperationRowIndex(-1);
  };

  // 查看更新数据的sql
  const handelViewSql = () => {
    if (!updateData.length) {
      return;
    }
    console.log('handelViewSql');
  };

  // 更新数据的sql
  const handelUpdateSubmit = () => {
    if (!updateData.length) {
      return;
    }
    const params = {
      ...props.executeSqlParams,
      tableName: data.tableName || 't_user1', // TODO:
      operations: updateData,
    };
    sqlService.getExecuteUpdateSql(params).then((res) => {
      console.log('res', res);
    });
  };

  const tableRowStyle = (rowIndex: number) => {
    // 如果是当前操作的行
    if (rowIndex === curOperationRowIndex) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'linear-gradient(140deg, #ff000038, #009cff3d)',
      };
    }
    // 如果是删除过的行
    const index = updateData.findIndex((item) => item.index === rowIndex && item.type === CRUD.DELETE);
    if (index !== -1) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'var(--color-error-bg)',
      };
    }
    // 如果是新增的行
    const index2 = updateData.findIndex((item) => item.index === rowIndex && item.type === CRUD.CREATE);
    if (index2 !== -1) {
      return {
        '--hover-bgcolor': 'transparent',
        '--bgcolor': 'transparent',
        background: 'var(--color-success-bg)',
      };
    }
    return {};
  };

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
            {/* {data.canEdit && (
            )} */}
            <div className={classnames(styles.toolBarItem, styles.editTableDataBar)}>
              <div onClick={handelCreateData} className={classnames(styles.createDataBar, styles.editTableDataBarItem)}>
                <Iconfont code="&#xe61b;" />
              </div>
              <div
                onClick={handelDeleteData}
                className={classnames(styles.deleteDataBar, styles.editTableDataBarItem, {
                  [styles.disableBar]: curOperationRowIndex === -1,
                })}
              >
                <Iconfont code="&#xe644;" />
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
            getRowProps={(record, rowIndex) => {
              return {
                style: tableRowStyle(rowIndex),
                onClick() {
                  setCurOperationRowIndex(rowIndex);
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
    <div className={classnames(className, styles.tableBox)}>
      {renderContent()}
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        footer={
          <>
            {
              <Button onClick={copyTableCell.bind(null, viewTableCellData!)} className={styles.cancel}>
                {i18n('common.button.copy')}
              </Button>
            }
          </>
        }
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
      {contextHolder}
    </div>
  );
}
