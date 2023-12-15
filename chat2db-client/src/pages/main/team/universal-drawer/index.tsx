import { Button, Drawer, Input, message, Popconfirm, Table, Tag } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import {
  AffiliationType,
  IDataSourceAccessVO,
  IDataSourceVO,
  ITeamVO,
  ITeamWithDataSourceVO,
  ITeamWithUserVO,
  IUserVO,
  IUserWithDataSourceVO,
  IUserWithTeamVO,
  ManagementType,
  SearchType,
} from '@/typings/team';
import {
  deleteDataSourceFromTeam,
  deleteDataSourceFromUser,
  deleteTeamListFromUser,
  deleteUserFromTeam,
  deleteUserOrTeamFromDataSource,
  getDataSourceListFromTeam,
  getDataSourceListFromUser,
  getTeamListFromUser,
  getUserAndTeamListFromDataSource,
  getUserListFromTeam,
  updateDataSourceListFromTeam,
  updateDataSourceListFromUser,
  updateTeamListFromUser,
  updateUserAndTeamListFromDataSource,
  updateUserListFromTeam,
} from '@/service/team';

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
  const [modalInfo, setModalInfo] = useState<{ open: boolean; type?: SearchType }>({
    open: false,
  });
  const [searchInput, setSearchInput] = useState('');

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
        title: i18n('team.team.name'),
        byIdKey: 'userId',
        queryListApi: getTeamListFromUser,
        updateListApi: updateTeamListFromUser,
        deleteApi: deleteTeamListFromUser,
        columns: [
          {
            title: i18n('team.team.addForm.code'),
            dataIndex: ['team', 'code'],
            key: 'team.code',
          },
          {
            title: i18n('team.team.addForm.name'),
            dataIndex: ['team', 'name'],
            key: 'team.name',
          },
          {
            title: i18n('common.text.action'),
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
                    message.success(i18n('common.text.successfullyDelete'));
                    queryTableList();
                  }
                }}
              >
                <a href="#" onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            ),
          },
        ],
      },
      [AffiliationType.USER_DATASOURCE]: {
        type: AffiliationType.USER_DATASOURCE,
        searchType: SearchType.DATASOURCE,
        title: i18n('team.datasource.rightManagement'),
        byIdKey: 'userId',
        queryListApi: getDataSourceListFromUser,
        updateListApi: updateDataSourceListFromUser,
        deleteApi: deleteDataSourceFromUser,
        columns: [
          {
            title: i18n('team.datasource.alias'),
            dataIndex: ['dataSource', 'alias'],
            key: 'dataSource.alias',
          },
          {
            title: i18n('team.datasource.url'),
            dataIndex: ['dataSource', 'url'],
            key: 'dataSource.url',
          },
          {
            title: i18n('common.text.action'),
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
                    message.success(i18n('common.text.successfullyDelete'));
                    queryTableList();
                  }
                }}
              >
                <a href="#" onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            ),
          },
        ],
      },
      [AffiliationType.TEAM_USER]: {
        type: AffiliationType.TEAM_USER,
        searchType: SearchType.USER,
        title: i18n('team.user.name'),
        byIdKey: 'teamId',
        queryListApi: getUserListFromTeam,
        updateListApi: updateUserListFromTeam,
        deleteApi: deleteUserFromTeam,
        columns: [
          {
            title: i18n('team.user.addForm.userName'),
            dataIndex: ['user', 'userName'],
            key: 'user.userName',
          },
          {
            title: i18n('team.user.addForm.nickName'),
            dataIndex: ['user', 'nickName'],
            key: 'user.nickName',
          },
          {
            title: i18n('common.text.action'),
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
                    message.success(i18n('common.text.successfullyDelete'));
                    queryTableList();
                  }
                }}
              >
                <a href="#" onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            ),
          },
        ],
      },
      [AffiliationType.TEAM_DATASOURCE]: {
        type: AffiliationType.TEAM_DATASOURCE,
        searchType: SearchType.DATASOURCE,
        title: i18n('team.action.affiliation.datasource'),
        byIdKey: 'teamId',
        queryListApi: getDataSourceListFromTeam,
        updateListApi: updateDataSourceListFromTeam,
        deleteApi: deleteDataSourceFromTeam,
        columns: [
          {
            title: i18n('team.datasource.alias'),
            dataIndex: ['dataSource', 'alias'],
            key: 'dataSource.alias',
          },
          {
            title: i18n('team.datasource.url'),
            dataIndex: ['dataSource', 'url'],
            key: 'dataSource.url',
          },
          {
            title: i18n('common.text.action'),
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
                    message.success(i18n('common.text.successfullyDelete'));
                    queryTableList();
                  }
                }}
              >
                <a href="#" onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            ),
          },
        ],
      },
      [AffiliationType['DATASOURCE_USER/TEAM']]: {
        type: AffiliationType['DATASOURCE_USER/TEAM'],
        searchType: SearchType['USER/TEAM'],
        title: i18n('team.datasource.rightManagement'),
        byIdKey: 'dataSourceId',
        queryListApi: getUserAndTeamListFromDataSource,
        updateListApi: updateUserAndTeamListFromDataSource,
        deleteApi: deleteUserOrTeamFromDataSource,
        columns: [
          {
            title: i18n('team.datasource.code'),
            dataIndex: ['accessObject', 'code'],
            key: 'accessObject.code',
          },
          {
            title: i18n('team.datasource.name'),
            dataIndex: ['accessObject', 'name'],
            key: 'accessObject.name',
          },
          {
            title: i18n('team.datasource.status'),
            dataIndex: ['accessObject', 'type'],
            key: 'accessObject.type',
            render: (status: ManagementType) => (
              <Tag color={status === ManagementType.TEAM ? 'blue' : 'lime'}>{status}</Tag>
            ),
          },
          {
            title: i18n('common.text.action'),
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
                    message.success(i18n('common.text.successfullyDelete'));
                    queryTableList();
                  }
                }}
              >
                <a href="#" onClick={(e) => e.preventDefault()}>
                  {i18n('common.button.delete')}
                </a>
              </Popconfirm>
            ),
          },
        ],
      },
    }),
    [props.byId, type],
  );

  const managementDataByType = type ? managementMap[type] : null;

  useEffect(() => {
    if (!open) {
      return;
    }
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
  }, [props.byId, type, open]);

  useEffect(() => {
    queryTableList();
  }, [pagination]);

  const queryTableList = async (searchKey?: string) => {
    const { current: pageNo, pageSize } = pagination;
    const requestApi = managementDataByType?.queryListApi;
    if (!requestApi || !isNumber(props.byId)) {
      return;
    }
    const res = await requestApi({
      searchKey: searchKey || pagination.searchKey,
      pageNo,
      pageSize,
      [managementDataByType?.byIdKey]: props.byId,
    });
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

  if (!managementDataByType) {
    return;
  }

  return (
    <Drawer open={open} width={720} title={managementDataByType?.title} onClose={props.onClose}>
      <div className={styles.tableTop}>
        <Input.Search
          style={{ width: '200px' }}
          placeholder={i18n('team.input.search.placeholder')}
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
              type: managementDataByType.searchType,
            });
          }}
        >
          {i18n('common.button.add')}
        </Button>
      </div>
      <Table rowKey={'id'} columns={managementDataByType?.columns} dataSource={dataSource} />

      <UniversalAddModal
        {...modalInfo}
        onConfirm={(values) => {
          managementDataByType.updateListApi({ [managementDataByType.byIdKey]: props.byId, ...values }).then((res) => {
            message.success(i18n('common.tips.updateSuccess'));
            queryTableList();
          });
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
