import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Table } from 'antd';
import historyService, { IHistoryRecord } from '@/service/history';

interface IProps {
  className?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const [dataSource, setDataSource] = React.useState<IHistoryRecord[]>([]);

  const columns = [
    {
      title: '',
      dataIndex: 'No',
      width: 50,
      key: 'No',
      align: 'center',
      render: (text: any, record: any, index: number) => {
        return <span>{index + 1}</span>;
      },
    },
    {
      title: '开始时间',
      dataIndex: 'time',
      key: 'time',
    },
    {
      title: '数据库/schema',
      dataIndex: 'databaseName',
      key: 'databaseName',
      render: (value: string, record: IHistoryRecord) => {
        return <span>{`${record.dataSourceName}/${record.databaseName}`}</span>;
      },
    },
    {
      title: 'ddl',
      dataIndex: 'ddl',
      key: 'ddl',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (value: boolean) => {
        return <span style={{ color: value ? 'green' : 'red' }}>{value ? '成功' : '失败'}</span>;
      },
    },
    {
      title: '影响行数',
      dataIndex: 'operationRows',
      key: 'operationRows',
    },
    {
      title: '执行耗时',
      dataIndex: 'useTime',
      key: 'useTime',
    },
  ];

  useEffect(() => {
    getHistoryList();
  }, []);

  const getHistoryList = () => {
    historyService
      .getHistoryList({
        pageNo: 1,
        pageSize: 100,
      })
      .then((res) => {
        setDataSource(res.data);
      });
  };

  return (
    <div className={classnames(styles.output, className)}>
      <Table
        style={{
          maxHeight: '100%',
          overflow: 'auto',
        }}
        sticky
        dataSource={dataSource}
        columns={columns as any}
        pagination={false}
      />
    </div>
  );
});
