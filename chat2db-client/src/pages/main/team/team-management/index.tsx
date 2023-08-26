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
  const [loadding, setLoading] = useState(false)
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
        title: '团队编码',
        dataIndex: 'code',
        key: 'code',
      },
      {
        title: '团队名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        render: (status: StatusType) => <Tag color={status === StatusType.VALID ? 'green' : 'red'}>{status}</Tag>,
      },
      {
        title: '操作',
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
                  type: AffiliationType.TEAM_USER
                });
              }}
            >
              包含用户
            </Button>
            <Button
              type="link"
              onClick={() => {
                setDrawerInfo({
                  ...drawerInfo,
                  open: true,
                  teamId: record.id,
                  type: AffiliationType.TEAM_DATASOURCE
                });
              }}>
              归属链接
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
      }
    } catch (error) {

    } finally {
      setLoading(false)
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
      message.success('删除成功');
      queryTeamList();
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
          添加团队
        </Button>
      </div>
      <Table
        rowKey={'id'}
        loading={loadding}
        dataSource={dataSource}
        columns={columns}
        pagination={pagination}
        onChange={handleTableChange}
      />

      <Modal
        title={form.getFieldValue('id') !== undefined ? '编辑团队' : '添加团队'}
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
            })
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
          <Form.Item label="团队编码" name="code" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label="团队名" name="name">
            <Input />
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={StatusType.VALID}>有效</Radio>
              <Radio value={StatusType.INVALID}>无效</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="描述" name="description">
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
