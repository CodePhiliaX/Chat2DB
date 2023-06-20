import React, { memo, useEffect, useState, useRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Tabs from '@/components/Tabs';
import type { ColumnsType } from 'antd/es/table';
import Iconfont from '@/components/Iconfont';
import StateIndicator from '@/components/StateIndicator';
import LoadingContent from '@/components/Loading/LoadingContent';
import MonacoEditor from '@/components/MonacoEditor';
import { Button, DatePicker, Input, Table, Modal, message } from 'antd';
import { StatusType, TableDataType } from '@/utils/constants';
import { formatDate } from '@/utils';
import { IManageResultData, ITableHeaderItem, ITableCellItem } from '@/types';
import Item from 'antd/lib/list/Item';

interface IProps {
  className?: string;
  manageResultDataList: IManageResultData[];
}

interface DataType {
  [key: string]: any;
}

export default memo<IProps>(function SearchResult({ className, manageResultDataList = [] }) {
  const [isUnfold, setIsUnfold] = useState(true);
  const [currentTab, setCurrentTab] = useState('0');

  useEffect(() => {
    setCurrentTab('0')
  }, [manageResultDataList])

  const renderStatus = (text: string) => {
    return <div className={styles.tableStatus}>
      <i className={classnames(styles.dot, { [styles.successDot]: text == StatusType.SUCCESS })}></i>
      {text == StatusType.SUCCESS ? '成功' : '失败'}
    </div>
  }

  function onChange(index: string) {
    setCurrentTab(index)
  }

  const makerResultHeaderList = () => {
    const list: any = []
    manageResultDataList?.map((item, index) => {
      list.push({
        label: <div key={index}>
          <Iconfont className={classnames(
            styles[item.success ? 'successIcon' : 'failIcon'],
            styles.statusIcon
          )}
            code={item.success ? '\ue605' : '\ue87c'} />
          执行结果{index + 1}
        </div>,
        key: index
      })
    })
    return list
  }

  return <div className={classnames(className, styles.box)}>
    <div className={styles.resultHeader}>
      <Tabs
        onChange={onChange}
        tabs={makerResultHeaderList()}
      />
    </div>
    <div className={styles.resultContent}>
      <LoadingContent data={manageResultDataList} handleEmpty>
        {
          manageResultDataList.map((item, index) => {
            if (item.success) {
              return <TableBox key={index} className={classnames({ [styles.cursorTableBox]: (index + '') == currentTab })} data={item} headerList={item.headerList} dataList={item.dataList}></TableBox>
            } else {
              return <StateIndicator key={index} state='error' text={item.message}></StateIndicator>
            }
          })
        }
      </LoadingContent>
    </div>
  </div>
})

interface ITableProps {
  headerList: ITableHeaderItem[];
  dataList: ITableCellItem[][];
  className?: string;
  data: IManageResultData;
}

interface IViewTableCellData {
  name: string;
  value: any;
}

export function TableBox(props: ITableProps) {
  const { headerList, dataList, className, data, ...rest } = props
  const [columns, setColumns] = useState<any>();
  const [tableData, setTableData] = useState<any>();
  const [viewTableCellData, setViewTableCellData] = useState<IViewTableCellData | null>(null);

  function viewTableCell(data: IViewTableCellData) {
    setViewTableCellData(data)
  }

  function copyTableCell(data: IViewTableCellData) {
    navigator.clipboard.writeText(data?.value || viewTableCellData?.value);
    message.success('复制成功');
  }

  function handleCancel() {
    setViewTableCellData(null)
  }

  useEffect(() => {
    if (!headerList?.length) {
      return
    }
    const columns: any = headerList?.map((item: any, index) => {
      const data = {
        title: item.name,
        dataIndex: item.name,
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
      }
      return data
    })
    setColumns(columns)
  }, [headerList])

  useEffect(() => {
    if (!columns?.length) return
    const tableData = dataList?.map((item: any[], index) => {
      const rowData: any = {}
      item.map((i: string | null, index: number) => {
        if (columns[index].dataType === TableDataType.DATETIME && i) {
          rowData[columns[index].title] = formatDate(i, 'yyyy-MM-dd hh:mm:ss');
        } else if (i === null) {
          rowData[columns[index].title] = '[null]';
        } else {
          rowData[columns[index].title] = i;
        }
      })
      rowData.key = index
      return rowData
    })
    setTableData(tableData);
  }, [columns])

  return <div {...rest} className={classnames(className, styles.tableBox)}>
    {
      (dataList !== null)
        ?
        <Table bordered pagination={false} columns={columns} dataSource={tableData} size="small" />
        :
        <StateIndicator state='success' text='执行成功'></StateIndicator>
    }
    <Modal
      title={viewTableCellData?.name}
      open={!!viewTableCellData?.name}
      onCancel={handleCancel}
      width='60vw'
      maskClosable={false}
      footer={
        <>
          {
            <Button onClick={copyTableCell.bind(null, viewTableCellData!)} className={styles.cancel}>
              复制
            </Button>
          }
        </>
      }
    >
      <div className={styles.monacoEditor}>
        <MonacoEditor value={viewTableCellData?.value} readOnly={true} id='view_table-Cell_data'></MonacoEditor>
      </div>
    </Modal>
  </div>
}
