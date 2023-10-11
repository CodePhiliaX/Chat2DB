import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Table } from 'antd';

interface IProps {
  className?: string;
  dataSourcesId?: string;
}

// 获取Output的参数
export interface IGetOutputParams {
  dataSourcesId?: string;
  databaseName?: string;
}

export default memo<IProps>((props) => {
  const { className } = props;
  const dataSource = [
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '1',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
    {
      key: '2',
      time: '2022-10-11 12:00:00',
      sql: 'select * from test',
      executionTime: '100ms',
      databaseName: 'sys',
      state: '成功',
    },
  ];

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
    },
    {
      title: '状态',
      dataIndex: 'state',
      key: 'state',
    },
    {
      title: 'sql',
      dataIndex: 'sql',
      key: 'sql',
    },
    {
      title: '执行耗时',
      dataIndex: 'executionTime',
      key: 'executionTime',
    },
  ];

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
