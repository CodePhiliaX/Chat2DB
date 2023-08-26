import { Button, Drawer, Input, message, Popconfirm, Table, Tag } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import { AffiliationType, IDataSourceAccessVO, IDataSourceVO, ITeamVO, ITeamWithDataSourceVO, ITeamWithUserVO, IUserVO, IUserWithDataSourceVO, IUserWithTeamVO, ManagementType, SearchType, } from '@/typings/team';
import { deleteDataSourceFromTeam, deleteDataSourceFromUser, deleteTeamListFromUser, deleteUserFromTeam, deleteUserOrTeamFromDataSource, getDataSourceListFromTeam, getDataSourceListFromUser, getTeamListFromUser, getUserAndTeamListFromDataSource, getUserListFromTeam, updateDataSourceListFromTeam, updateDataSourceListFromUser, updateTeamListFromUser, updateUserAndTeamListFromDataSource, updateUserListFromTeam } from '@/service/team';

import UniversalAddModal from '../universal-add-modal';
import styles from './index.less';
import { ColumnsType } from 'antd/es/table';
import i18n from '@/i18n';
import { isNumber } from 'lodash';

interface IProps {
  type?: AffiliationType;
  open: boolean;
  onClose: () => void;
  byId?: number;
}

interface IAffiliationDetail {
  type: AffiliationType;
  searchType: SearchType;
  title: string;
  byIdKey: string;
  columns: ColumnsType<any>;
  queryListApi: (params: any) => Promise<any>;
  updateListApi: (params: any) => Promise<any>;
  deleteApi: (params: { id: number }) => Promise<any>;
}

function UniversalDrawer(props: IProps) {
  const { type, open } = props;
  const [dataSource, setDataSource] = useState<Array<IUserVO | ITeamVO | IDataSourceVO>>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [modalInfo, setModalInfo] = useState<{ open: boolean; type?: SearchType }>({
    open: false,
  });
  const [searchInput, setSearchInput] = useState('')

  const [pagination, setPagination] = useState({
    searchKey: '',
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
  });

  const managementMap: Record<AffiliationType, IAffiliationDetail> = useMemo(
    () => ({
      [AffiliationType.USER_TEAM]: {
        type: AffiliationType.USER_TEAM,
        searchType: SearchType.TEAM,
        title: '团队',
        byIdKey: 'userId',
        queryListApi: getTeamListFromUser,
        updateListApi: updateTeamListFromUser,
        deleteApi: deleteTeamListFromUser,
        columns: [
          {
            title: '团队编码',
            dataIndex: ['team', 'code'],
            key: 'team.code'
          },
          {
            title: '团队名称',
            dataIndex: ['team', 'name'],
            key: 'team.name'
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: IUserWithTeamVO) => (
              <Popconfirm
                title={i18n('common.tips.delete.confirm')}
                okText={i18n('common.button.affirm')}
                cancelText={i18n('common.button.cancel')}
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteTeamListFromUser({ id: record.id });
                    message.success(i18n('common.text.successfullyDelete'))
                    queryTableList();
                  }
                }}>
                <a href='#' onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            )
          }
        ]
      },
      [AffiliationType.USER_DATASOURCE]: {
        type: AffiliationType.USER_DATASOURCE,
        searchType: SearchType.DATASOURCE,
        title: '归属链接',
        byIdKey: 'userId',
        queryListApi: getDataSourceListFromUser,
        updateListApi: updateDataSourceListFromUser,
        deleteApi: deleteDataSourceFromUser,
        columns: [
          {
            title: '链接名称',
            dataIndex: ['dataSource', 'alias'],
            key: 'dataSource.alias'
          },
          {
            title: '链接地址',
            dataIndex: ['dataSource', 'url'],
            key: 'dataSource.url'
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: IUserWithDataSourceVO) => (
              <Popconfirm
                title={i18n('common.tips.delete.confirm')}
                okText={i18n('common.button.affirm')}
                cancelText={i18n('common.button.cancel')}
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteDataSourceFromUser({ id: record.id });
                    message.success(i18n('common.text.successfullyDelete'))
                    queryTableList();
                  }
                }}>
                <a href='#' onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            )
          }
        ]
      },
      [AffiliationType.TEAM_USER]: {
        type: AffiliationType.TEAM_USER,
        searchType: SearchType.USER,
        title: '用户',
        byIdKey: 'teamId',
        queryListApi: getUserListFromTeam,
        updateListApi: updateUserListFromTeam,
        deleteApi: deleteUserFromTeam,
        columns: [
          {
            title: '用户名',
            dataIndex: ['user', 'userName'],
            key: 'user.userName'
          },
          {
            title: '昵称',
            dataIndex: ['user', 'nickName'],
            key: 'user.nickName'
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: ITeamWithUserVO) => (
              <Popconfirm
                title={i18n('common.tips.delete.confirm')}
                okText={i18n('common.button.affirm')}
                cancelText={i18n('common.button.cancel')}
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteUserFromTeam({ id: record.id });
                    message.success(i18n('common.text.successfullyDelete'))
                    queryTableList();
                  }
                }}>
                <a href='#' onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            )
          }
        ]
      },
      [AffiliationType.TEAM_DATASOURCE]: {
        type: AffiliationType.TEAM_DATASOURCE,
        searchType: SearchType.DATASOURCE,
        title: '归属链接',
        byIdKey: 'teamId',
        queryListApi: getDataSourceListFromTeam,
        updateListApi: updateDataSourceListFromTeam,
        deleteApi: deleteDataSourceFromTeam,
        columns: [
          {
            title: '链接名称',
            dataIndex: ['dataSource', 'alias'],
            key: 'dataSource.alias'
          },
          {
            title: '链接地址',
            dataIndex: ['dataSource', 'url'],
            key: 'dataSource.url'
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: ITeamWithDataSourceVO) => (
              <Popconfirm
                title={i18n('common.tips.delete.confirm')}
                okText={i18n('common.button.affirm')}
                cancelText={i18n('common.button.cancel')}
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteDataSourceFromUser({ id: record.id });
                    message.success(i18n('common.text.successfullyDelete'))
                    queryTableList();
                  }
                }}>
                <a href='#' onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            )
          }
        ]
      },
      [AffiliationType['DATASOURCE_USER/TEAM']]: {
        type: AffiliationType['DATASOURCE_USER/TEAM'],
        searchType: SearchType['USER/TEAM'],
        title: '链接权限管理',
        byIdKey: 'dataSourceId',
        queryListApi: getUserAndTeamListFromDataSource,
        updateListApi: updateUserAndTeamListFromDataSource,
        deleteApi: deleteUserOrTeamFromDataSource,
        columns: [
          {
            title: '编码',
            dataIndex: ['accessObject', 'code'],
            key: 'accessObject.code'
          },
          {
            title: '名称',
            dataIndex: ['accessObject', 'name'],
            key: 'accessObject.name'
          },
          {
            title: '类型',
            dataIndex: ['accessObject', 'type'],
            key: 'accessObject.type',
            render: (status: ManagementType) => <Tag color={status === ManagementType.TEAM ? 'blue' : 'lime'}>{status}</Tag>
          },
          {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_: any, record: IDataSourceAccessVO) => (
              <Popconfirm
                title={i18n('common.tips.delete.confirm')}
                okText={i18n('common.button.affirm')}
                cancelText={i18n('common.button.cancel')}
                onConfirm={async () => {
                  if (record.id !== undefined) {
                    await deleteUserOrTeamFromDataSource({ id: record.id });
                    message.success(i18n('common.text.successfullyDelete'))
                    queryTableList();
                  }
                }}>
                <a href='#' onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            )
          }
        ]
      }
    }),
    [props.byId, type],
  );

  const managementDataByType = type ? managementMap[type] : null

  useEffect(() => {
    setSearchInput('');
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
      type: managementDataByType?.searchType,
    });
  }, [props.byId]);

  useEffect(() => {
    queryTableList();
  }, [pagination.current, pagination.pageSize, pagination.searchKey, props.byId, type]);

  const queryTableList = async () => {
    const { searchKey, current: pageNo, pageSize } = pagination;
    const requestApi = managementDataByType?.queryListApi;
    if (!requestApi || !isNumber(props.byId)) {
      return;
    }
    let res = await requestApi({ searchKey, pageNo, pageSize, [managementDataByType?.byIdKey]: props.byId });
    if (res) {
      setDataSource(res?.data ?? []);
    }
  };

  const handleSearch = (searchKey: string) => {
    setPagination({
      ...pagination,
      searchKey
    })
  };

  if (!managementDataByType) {
    return;
  }

  return (
    <Drawer open={open} width={720} title={managementDataByType?.title} onClose={props.onClose}>
      <div className={styles.tableTop}>
        <Input.Search
          style={{ width: '200px' }}
          placeholder="输入关键字进行搜索"
          value={searchInput}
          onChange={(v) => setSearchInput(v.target.value)}
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
              type: managementDataByType.searchType
            });
          }}
        >
          添加
        </Button>
      </div>
      <Table rowKey={'id'} columns={managementDataByType?.columns} dataSource={dataSource} />

      <UniversalAddModal
        {...modalInfo}
        onConfirm={(values) => {
          managementDataByType.updateListApi({ [managementDataByType.byIdKey]: props.byId, ...values }).then(res => {
            message.success('更新成功')
            queryTableList()
          })
        }}
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
