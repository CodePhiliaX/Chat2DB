import React, { useEffect, useMemo, useState } from 'react';
import { TableDataType } from '@/constants/table';
import { IManageResultData, ITableHeaderItem } from '@/typings/database';
import { formatDate } from '@/utils/date';
import { message, Modal, Table } from 'antd';
import { applyTransforms, BaseTable, BaseTableProps, collectNodes, makeColumnResizeTransform } from 'ali-react-table';
import Iconfont from '../Iconfont';
import classnames from 'classnames';
import StateIndicator from '../StateIndicator';
import MonacoEditor from '../Console/MonacoEditor';
import styles from './TableBox.less';

interface ITableProps {
  className?: string;
  data: IManageResultData;
  key: number;
}

interface IViewTableCellData {
  name: string;
  value: any;
}

export default function TableBox(props: ITableProps) {
  const { className, data, key } = props;
  const { headerList, dataList, duration, description } = data || {};
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);

  function viewTableCell(data: IViewTableCellData) {
    setViewTableCellData(data);
  }

  function copyTableCell(data: IViewTableCellData) {
    navigator.clipboard.writeText(data?.value || viewTableCellData?.value);
    message.success('复制成功');
  }

  function handleCancel() {
    setViewTableCellData(null);
  }

  const columns = useMemo(
    () =>
      (headerList || []).map((item: any) => ({
        title: item.name,
        dataIndex: item.name,
        code: item.name,
        key: item.name,
        type: item.dataType,
        sorter: (a: any, b: any) => a[item.name] - b[item.name],
        render: (value: any) => (
          <div className={styles.tableItem}>
            <div className={styles.tableHoverBox}>
              <Iconfont code="&#xe606;" onClick={viewTableCell.bind(null, { name: item.name, value })} />
              <Iconfont code="&#xeb4e;" onClick={copyTableCell.bind(null, { name: item.name, value })} />
            </div>
            {value}
          </div>
        ),
      })),
    [headerList],
  );

  const tableData = useMemo(() => {
    if (!columns?.length) {
      return [];
    } else {
      return (dataList || []).map((item, rowIndex) => {
        const rowData: any = {};
        item.map((i: string | null, index: number) => {
          const { dataType: type } = headerList[index] || {};
          // console.log('headerList[rowIndex]', headerList[rowIndex]);
          if (type === TableDataType.DATETIME && i) {
            rowData[columns[index].title] = formatDate(i, 'yyyy-MM-dd hh:mm:ss');
          } else if (i === null) {
            rowData[columns[index].title] = '[null]';
          } else {
            rowData[columns[index].title] = i;
          }
        });
        rowData.key = rowIndex;
        return rowData;
      });
    }
  }, [dataList, columns]);

  console.log('dataList', dataList);
  return (
    <div className={classnames(className, styles.tableBox)}>
      {columns.length ? (
        <Table pagination={false} columns={columns} dataSource={tableData} scroll={{ y: '100vh' }} size="small" />
      ) : (
        <StateIndicator state="success" text="执行成功" />
      )}
      <div className={styles.statusBar}>{`结果:${description}. 耗时:${duration}ms`}</div>
      <Modal
        title={viewTableCellData?.name}
        open={!!viewTableCellData?.name}
        onCancel={handleCancel}
        width="60vw"
        maskClosable={false}
        footer={
          <>
            {/* {
              <Button onClick={copyTableCell.bind(null, viewTableCellData!)} className={styles.cancel}>
                复制
              </Button>
            } */}
          </>
        }
      >
        <div className={styles.monacoEditor}>
          <MonacoEditor
            id="view_table-Cell_data"
            defaultValue={viewTableCellData?.value}
            options={{
              readOnly: true,
            }}
          ></MonacoEditor>
        </div>
      </Modal>
    </div>
  );
}
function pipeline(arg0: {
  sizes: number;
  onChangeSizes: React.Dispatch<React.SetStateAction<number>>;
  appendExpander: boolean;
  expanderVisibility: string;
  disableUserSelectWhenResizing: boolean;
  minSize: number;
  maxSize: number;
}): import('ali-react-table').Transform<{ columns: any; dataSource: any }> {
  throw new Error('Function not implemented.');
}
