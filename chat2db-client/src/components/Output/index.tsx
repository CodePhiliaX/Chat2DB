import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Table } from 'antd';
import historyService, { IGetHistoryListParams } from '@/service/history';
import { set } from 'lodash';

export interface IGetOutputParams extends IGetHistoryListParams {}

interface IProps {
  className?: string;
  params: IGetOutputParams;
}

export default memo<IProps>((props) => {
  const { className, params } = props;
  const [dataSource, setDataSource] = React.useState<any[]>([]);

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
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '执行耗时',
      dataIndex: 'executionTime',
      key: 'executionTime',
    },
  ];

  useEffect(() => {
    getHistoryList();
  }, []);

  const getHistoryList = () => {
    historyService.getHistoryList(params).then((res) => {
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
