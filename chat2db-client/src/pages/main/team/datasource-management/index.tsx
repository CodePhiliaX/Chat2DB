import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Button, Input, Table, Popconfirm, message, Drawer } from 'antd';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import ConnectionServer from '@/service/connection';
import { createDataSource, deleteDataSource, getDataSourceList, updateDataSource } from '@/service/team';
import { IConnectionDetails } from '@/typings';
import { AffiliationType, IDataSourceVO } from '@/typings/team';
import i18n from '@/i18n';
import { isValid } from '@/utils/check';
import CreateConnection from '@/blocks/CreateConnection';
import UniversalDrawer from '../universal-drawer';
import { isNumber } from 'lodash';
import styles from './index.less';

function DataSourceManagement() {
  const [dataSource, setDataSource] = useState<IDataSourceVO[]>([]);
  const [pagination, setPagination] = useState({
    searchKey: '',
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    // pageSizeOptions: ['10', '20', '30', '40'],
  });
  const [showCreateConnection, setShowCreateConnection] = useState(false);
  const connectionInfo = useRef<IConnectionDetails>();

  const [drawerInfo, setDrawerInfo] = useState<{ open: boolean; type: AffiliationType; id?: number }>({
    open: false,
    type: AffiliationType['DATASOURCE_USER/TEAM'],
  });

  const columns = useMemo(
    () => [
      {
        title: i18n('team.datasource.alias'),
        dataIndex: 'alias',
        key: 'alias',
      },
      {
        title: i18n('team.datasource.url'),
        dataIndex: 'url',
        key: 'url',
      },
      {
        title: i18n('common.text.action'),
        key: 'action',
        width: 300,
        render: (_: any, record: IDataSourceVO) => (
          <>
            <Button
              type="link"
              onClick={() => {
                handleEdit(record);
              }}
            >
              {i18n('common.button.edit')}
            </Button>
            <Button
              type="link"
              onClick={() => {
                setDrawerInfo({
                  ...drawerInfo,
                  open: true,
                  id: record.id,
                });
              }}
            >
              {i18n('team.action.rightManagement')}
            </Button>
            <Popconfirm
              title={i18n('common.tips.delete.confirm')}
              onConfirm={() => handleDelete(record.id)}
              okText={i18n('common.button.affirm')}
              cancelText={i18n('common.button.cancel')}
            >
              <a href="#" onClick={(e) => e.preventDefault()}>
                {i18n('common.button.delete')}
              </a>
            </Popconfirm>
          </>
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
      setPagination({
        ...pagination,
        total: res?.total ?? 0,
      } as any);
    }
  };

  const handleSearch = (searchKey: string) => {
    setPagination({
      ...pagination,
      searchKey,
    });
  };

  const handleTableChange = (p: any) => {
    setPagination({
      ...pagination,
      ...p,
    });
  };

  const handleAddDataSource = () => {
    connectionInfo.current = undefined;
    setShowCreateConnection(true);
  };

  const handleEdit = async (record: IDataSourceVO) => {
    const { id } = record;
    if (!id) {
      return;
    }

    let detail = await ConnectionServer.getDetails({ id });
    connectionInfo.current = detail;
    setShowCreateConnection(true);
  };

  const handleDelete = async (id?: number) => {
    if (isNumber(id)) {
      await deleteDataSource({ id });
      message.success(i18n('common.text.successfullyDelete'));
      queryDataSourceList();
    }
  };

  const handleConfirmConnection = async (data: IConnectionDetails) => {
    if (JSON.stringify(connectionInfo.current) === '{}') {
      return;
    }
    connectionInfo.current = data;

    const isUpdate = isValid(connectionInfo?.current?.id);
    const requestApi = isUpdate ? updateDataSource : createDataSource;
    try {
      await requestApi({ ...connectionInfo.current });
      message.success(isUpdate ? i18n('common.tips.updateSuccess') : i18n('common.tips.createSuccess'));
      setShowCreateConnection(false);
      queryDataSourceList();
    } catch {}
  };

  return (
    <div>
      <div className={styles.tableTop}>
        <Input.Search
          style={{ width: '320px' }}
          placeholder={i18n('team.input.search.placeholder')}
          onSearch={handleSearch}
          enterButton={<SearchOutlined />}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddDataSource}>
          {i18n('team.action.addDatasource')}
        </Button>
      </div>
      <Table
        style={{
          maxHeight: '82vh',
          overflow: 'auto',
        }}
        sticky
        rowKey={'id'}
        dataSource={dataSource}
        columns={columns}
        pagination={pagination}
        onChange={handleTableChange}
      />

      <Drawer
        title={connectionInfo?.current?.id ? i18n('team.action.editDatasource') : i18n('team.action.addDatasource')}
        width={1000}
        open={showCreateConnection}
        onClose={() => setShowCreateConnection(false)}
      >
        <CreateConnection connectionDetail={connectionInfo.current} onSubmit={handleConfirmConnection} />
      </Drawer>

      <UniversalDrawer
        {...drawerInfo}
        byId={drawerInfo.id}
        onClose={() => {
          setDrawerInfo({
            ...drawerInfo,
            open: false,
          });
        }}
      />
    </div>
  );
}

export default DataSourceManagement;
