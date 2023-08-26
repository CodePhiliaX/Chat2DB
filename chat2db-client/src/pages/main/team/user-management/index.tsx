import { createUser, deleteUser, getUserManagementList, updateUser } from '@/service/team';
import { Button, Form, Input, Modal, Popconfirm, Radio, Table, Tag, message } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import styles from './index.less';
import { AffiliationType, IUserVO, RoleType, StatusType } from '@/typings/team';
import i18n from '@/i18n';
import UniversalDrawer from '../universal-drawer';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
  colon: false,
};

const requireRule = { required: true, message: i18n('common.form.error.required') };

function UserManagement() {
  const [form] = Form.useForm();
  const [dataSource, setDataSource] = useState<IUserVO[]>([]);
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
  const [drawerInfo, setDrawerInfo] = useState<{ open: boolean; type?: AffiliationType; id?: number }>({
    open: false,
  })

  const columns = useMemo(
    () => [
      {
        title: '用户名',
        dataIndex: 'userName',
        key: 'userName',
      },
      {
        title: '昵称',
        dataIndex: 'nickName',
        key: 'nickName',
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
        render: (_: any, record: IUserVO) => (
          <>
            <Button type='link' onClick={() => {
              handleEdit(record)
            }}>
              {i18n('common.button.edit')}
            </Button>
            <Button type='link' onClick={() => {
              setDrawerInfo({
                ...drawerInfo,
                open: true,
                type: AffiliationType.USER_TEAM,
                id: record.id,
              })
            }}>
              所属团队
            </Button>
            <Button type='link' onClick={() => {
              setDrawerInfo({
                ...drawerInfo,
                open: true,
                type: AffiliationType.USER_DATASOURCE,
                id: record.id,
              })
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
    queryUserList();
  }, [pagination.current, pagination.pageSize, pagination.searchKey]);

  const queryUserList = async () => {
    const { searchKey, current: pageNo, pageSize } = pagination;
    let res = await getUserManagementList({ searchKey, pageNo, pageSize });
    if (res) {
      setDataSource(res?.data ?? []);
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

  const handleCreateOrUpdateUser = async (userInfo: IUserVO) => {
    const requestApi = userInfo?.id ? updateUser : createUser;
    let res = await requestApi(userInfo);
    if (res) {
      queryUserList();
    }
  };

  const handleEdit = (record: IUserVO) => {
    form.setFieldsValue(record)
    setIsModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    await deleteUser({ id })
    message.success('删除成功')
    queryUserList()
  }


  const isEditing = useMemo(() => {
    return form.getFieldValue('id') !== undefined;
  }, [form.getFieldValue('id')])


  console.log('form', form.getFieldsValue(true))
  return (
    <div>
      <div className={styles.tableTop}>
        <Input.Search
          maxLength={50}
          style={{ width: '200px' }}
          placeholder="输入关键字进行搜索"
          onSearch={handleSearch}
          enterButton={<SearchOutlined />}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => {
          form.resetFields();
          setIsModalVisible(true)
        }}>
          添加用户
        </Button>
      </div>
      <Table
        rowKey={'id'}
        dataSource={dataSource}
        columns={columns}
        pagination={pagination}
        onChange={handleTableChange}
      />

      <Modal
        title={isEditing ? '编辑用户' : '添加用户'}
        open={isModalVisible}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              const formValues = form.getFieldsValue(true);
              handleCreateOrUpdateUser(formValues);
              setIsModalVisible(false);
              form.resetFields();
            })
            .catch((errorInfo) => {
              form.scrollToField(errorInfo.errorFields[0].name);
              form.setFields(errorInfo.errorFields);
            })
            .finally(() => {
              form.resetFields();
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
            roleCode: RoleType.USER,
            status: StatusType.VALID,
          }}
        >
          <Form.Item label="用户名" name="userName" rules={[requireRule]}>
            <Input maxLength={50} showCount autoComplete='off' />
          </Form.Item>
          <Form.Item label="昵称" name="nickName" rules={[requireRule]}>
            <Input maxLength={100} showCount />
          </Form.Item>
          <Form.Item label="邮箱" name="email" rules={[requireRule, {
            type: 'email',
            message: i18n('common.form.error.email')
          }]}>
            <Input autoComplete='off' />
          </Form.Item>
          <Form.Item label="密码" name="password" rules={[requireRule]}>
            <Input.Password maxLength={30} placeholder={isEditing ? '******' : ''} autoComplete='fake-password' />
          </Form.Item>
          <Form.Item label="角色" name="roleCode" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={RoleType.ADMIN}>管理员</Radio>
              <Radio value={RoleType.USER}>用户</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={StatusType.VALID}>有效</Radio>
              <Radio value={StatusType.INVALID}>无效</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>

      <UniversalDrawer
        {...drawerInfo}
        byId={drawerInfo.id}
        onClose={() => {
          setDrawerInfo({
            ...drawerInfo,
            open: false
          })
        }}
      />
    </div>
  );
}

export default UserManagement;
