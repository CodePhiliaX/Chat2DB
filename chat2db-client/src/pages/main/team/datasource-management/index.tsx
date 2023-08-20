import React, { useEffect, useMemo, useState } from 'react';
import { Button, Input, Table, Popconfirm, message } from 'antd';
import { IDataSourcePageQueryVO } from '@/typings/team';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import styles from './index.less';
import { getDataSourceList } from '@/service/team';

function DataSourceManagement() {
  const [dataSource, setDataSource] = useState<IDataSourcePageQueryVO[]>([]);
  const [pagination, setPagination] = useState({
    searchKey: '',
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    pageSizeOptions: ['10', '20', '30', '40'],
  });
  const [isModalVisible, setIsModalVisible] = useState(false);

  const columns = useMemo(
    () => [
      {
        title: '链接名称',
        dataIndex: 'alias',
        key: 'alias',
      },
      {
        title: '链接地址',
        dataIndex: 'url',
        key: 'url',
      },
      {
        title: '操作',
        key: 'action',
        render: (_, record: any) => (
          <Popconfirm
            title="确定要删除这条记录吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <a href="#" onClick={(e) => e.preventDefault()}>
              删除
            </a>
          </Popconfirm>
        ),
      },
    ],
    [],
  );

  useEffect(() => {
    queryDataSourceList();
  }, [pagination.current, pagination.pageSize, pagination.searchKey]);

  const queryDataSourceList = async () => {
    const { searchKey, current: pageNo, pageSize } = pagination;
    let res = await getDataSourceList({ searchKey, pageNo, pageSize });
    if (res) {
      setDataSource(res?.data ?? []);
    }
  };

  const handleSearch = (searchKey: string) => {
    setPagination({
      ...pagination,
      searchKey,
    });
  };

  const handleTableChange = (p: any) => {
    console.log('handleTableChange', p);
    setPagination({
      ...pagination,
      ...p,
    });
  };

  const handleDelete = async (recordId: number) => {
    const success = true; // Replace with actual API response

    if (success) {
      message.success('删除成功');
      queryDataSourceList();
    } else {
      message.error('删除失败');
    }
  };

  return (
    <div>
      <div className={styles.tableTop}>
        <Input.Search
          style={{ width: '200px' }}
          placeholder="输入关键字进行搜索"
          onSearch={handleSearch}
          enterButton={<SearchOutlined />}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalVisible(true)}>
          添加
        </Button>
      </div>
      <Table
        rowKey={'id'}
        dataSource={dataSource}
        columns={columns}
        pagination={pagination}
        onChange={handleTableChange}
      />
    </div>
  );
}

export default DataSourceManagement;
