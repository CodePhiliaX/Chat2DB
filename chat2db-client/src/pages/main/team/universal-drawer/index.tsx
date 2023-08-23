import { Button, Drawer, Input, message, Popconfirm, Table } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import { ITeamVO, IUserVO, ManagementType, TeamUserPageQueryVO } from '@/typings/team';
import { deleteUserFromTeam, getUserListFromTeam } from '@/service/team';

import styles from './index.less';
import UniversalAddModal from '../universal-add-modal';

interface IProps {
  type: ManagementType;
  open: boolean;
  onClose: () => void;
  /** 依赖某个id，可能是team、user、dataSource */
  byId?: number;
}

function UniversalDrawer(props: IProps) {
  const { type, open } = props;
  const [dataSource, setDataSource] = useState<Array<IUserVO | ITeamVO>>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [modalInfo, setModalInfo] = useState({
    open: false,
    type,
  });

  const [pagination, setPagination] = useState({
    searchKey: '',
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
  });

  const managementMap: Record<ManagementType, any> = useMemo(
    () => ({
      [ManagementType.USER]: {
        title: '用户',
        queryListApi: getUserListFromTeam,
        columns: [
          {
            title: '用户名',
            dataIndex: ['user', 'userName'],
            key: 'userName',
          },
          {
            title: '昵称',
            dataIndex: ['user', 'nickName'],
            key: 'nickName',
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: TeamUserPageQueryVO) => (
              <Popconfirm
                title="确定要删除这个用户吗？"
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteUserFromTeam({ id: record.id });
                    message.success('删除成功');
                    queryTableList();
                  }
                }}
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
        byId: 'teamId',
      },
      [ManagementType.TEAM]: {},
      [ManagementType.DATASOURCE]: {},
    }),
    [],
  );

  const managementDataByType = useMemo(() => managementMap[type], [type]);

  useEffect(() => {
    setPagination({
      searchKey: '',
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
    });
    setModalInfo({
      open: false,
      type,
    });
  }, [props.byId]);

  useEffect(() => {
    if (props.byId !== null) {
      queryTableList();
    }
  }, [pagination.current, pagination.pageSize, pagination.searchKey, props.byId]);

  const queryTableList = async () => {
    const { searchKey, current: pageNo, pageSize } = pagination;
    const requestApi = managementDataByType?.queryListApi;
    let res = await requestApi({ searchKey, pageNo, pageSize, [managementDataByType.byId]: props.byId });
    if (res) {
      setDataSource(res?.data ?? []);
    }
  };

  const handleSearch = () => {};

  return (
    <Drawer open={open} width={720} title={managementDataByType?.title} onClose={props.onClose}>
      <div className={styles.tableTop}>
        <Input.Search
          style={{ width: '200px' }}
          placeholder="输入关键字进行搜索"
          onSearch={handleSearch}
          enterButton={<SearchOutlined />}
        />
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setModalInfo({
              ...modalInfo,
              open: true,
            });
          }}
        >
          添加
        </Button>
      </div>
      <Table rowKey={'id'} columns={managementDataByType?.columns} dataSource={dataSource} />

      <UniversalAddModal
        {...modalInfo}
        onClose={() => {
          setModalInfo({
            ...modalInfo,
            open: false,
          });
        }}
      />
    </Drawer>
  );
}

export default UniversalDrawer;
