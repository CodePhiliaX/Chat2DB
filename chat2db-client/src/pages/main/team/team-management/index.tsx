import React, { useEffect, useMemo, useState } from 'react';
import { createTeam, deleteTeam, getTeamManagementList, updateTeam } from '@/service/team';
import { Button, Form, Input, Modal, Popconfirm, Radio, Table, Tag, message } from 'antd';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import UniversalDrawer from '../universal-drawer';
import { AffiliationType, ITeamVO, StatusType } from '@/typings/team';
import i18n from '@/i18n';
import styles from './index.less';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
  colon: false,
};

const requireRule = { required: true, message: i18n('common.form.error.required') };

function TeamManagement() {
  const [form] = Form.useForm();
  const [loadding, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<ITeamVO[]>([]);
  const [pagination, setPagination] = useState({
    searchKey: '',
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    // pageSizeOptions: ['10', '20', '30', '40'],
  });
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [drawerInfo, setDrawerInfo] = useState<{ open: boolean; type?: AffiliationType; teamId?: number }>({
    open: false,
  });
  const columns = useMemo(
    () => [
      {
        title: i18n('team.team.addForm.code'),
        dataIndex: 'code',
        key: 'code',
      },
      {
        title: i18n('team.team.addForm.name'),
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: i18n('team.team.addForm.status'),
        dataIndex: 'status',
        key: 'status',
        render: (status: StatusType) => <Tag color={status === StatusType.VALID ? 'green' : 'red'}>{status}</Tag>,
      },
      {
        title: i18n('common.text.action'),
        key: 'action',
        width: 260,
        render: (_: any, record: ITeamVO) => (
          <>
            <Button type="link" onClick={() => handleEdit(record)}>
              {i18n('common.button.edit')}
            </Button>
            <Button
              type="link"
              onClick={() => {
                setDrawerInfo({
                  ...drawerInfo,
                  open: true,
                  teamId: record.id,
                  type: AffiliationType.TEAM_USER,
                });
              }}
            >
              {i18n('team.action.affiliation.user')}
            </Button>
            <Button
              type="link"
              onClick={() => {
                setDrawerInfo({
                  ...drawerInfo,
                  open: true,
                  teamId: record.id,
                  type: AffiliationType.TEAM_DATASOURCE,
                });
              }}
            >
              {i18n('team.action.affiliation.datasource')}
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
    queryTeamList();
  }, [pagination.current, pagination.pageSize, pagination.searchKey]);

  const queryTeamList = async () => {
    setLoading(true);
    try {
      const { searchKey, current: pageNo, pageSize } = pagination;
      let res = await getTeamManagementList({ searchKey, pageNo, pageSize });
      if (res) {
        setDataSource(res?.data ?? []);
        setPagination({
          ...pagination,
          total: res?.total ?? 0,
        } as any);
      }
    } catch (error) {
    } finally {
      setLoading(false);
    }
  };

  const handleTableChange = (p: any) => {
    setPagination({
      ...pagination,
      ...p,
    });
  };

  const handleSearch = (searchKey: string) => {
    setPagination({
      ...pagination,
      searchKey,
    });
  };

  const handleCreateOrUpdateTeam = async (teamInfo: ITeamVO) => {
    const requestApi = teamInfo.id ? updateTeam : createTeam;
    let res = await requestApi(teamInfo);
    if (res) {
      queryTeamList();
    }
  };

  const handleEdit = (record: ITeamVO) => {
    form.setFieldsValue(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (id?: number) => {
    if (id !== undefined) {
      await deleteTeam({ id });
      message.success(i18n('common.text.successfullyDelete'));
      queryTeamList();
    }
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
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalVisible(true)}>
          {i18n('team.action.addTeam')}
        </Button>
      </div>
      <Table
        style={{
          maxHeight: '82vh',
          overflow: 'auto',
        }}
        sticky
        rowKey={'id'}
        loading={loadding}
        dataSource={dataSource}
        columns={columns}
        pagination={pagination}
        onChange={handleTableChange}
      />

      <Modal
        title={form.getFieldValue('id') !== undefined ? i18n('team.action.editTeam') : i18n('team.action.addTeam')}
        open={isModalVisible}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              const formValues = form.getFieldsValue(true);
              handleCreateOrUpdateTeam(formValues);
              setIsModalVisible(false);
              form.resetFields();
            })
            .catch((errorInfo) => {
              form.scrollToField(errorInfo.errorFields[0].name);
              form.setFields(errorInfo.errorFields);
            });
        }}
        onCancel={() => {
          form.resetFields();
          setIsModalVisible(false);
        }}
      >
        <Form
          {...formItemLayout}
          form={form}
          autoComplete={'off'}
          initialValues={{
            status: StatusType.VALID,
          }}
        >
          <Form.Item label={i18n('team.team.addForm.code')} name="code" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label={i18n('team.team.addForm.name')} name="name">
            <Input />
          </Form.Item>
          <Form.Item label={i18n('team.team.addForm.status')} name="status" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={StatusType.VALID}>{i18n('team.team.addForm.status.valid')}</Radio>
              <Radio value={StatusType.INVALID}>{i18n('team.team.addForm.status.invalid')}</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label={i18n('team.team.addForm.description')} name="description">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>

      <UniversalDrawer
        {...drawerInfo}
        byId={drawerInfo.teamId}
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

export default TeamManagement;
