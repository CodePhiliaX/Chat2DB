import { createUser, getUserManagementList } from '@/service/team';
import { IUserPageQueryVO, IUserStatus, IUserVO, RoleStatusType, UserStatusType } from '@/typings/team';
import { Button, Form, Input, Modal, Radio, Table, Tag } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import styles from './index.less';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
  colon: false,
};

const requireRule = { required: true, message: 'Require field empty!' };

function UserManagement() {
  const [form] = Form.useForm();
  const [dataSource, setDataSource] = useState<IUserPageQueryVO[]>([]);
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
        render: (status: IUserStatus) => <Tag color={status === UserStatusType.VALID ? 'green' : 'red'}>{status}</Tag>,
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
    console.log('handleTableChange', p);
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

  const handleCreateUser = async (userInfo: IUserVO) => {
    let res = await createUser(userInfo);
    if (res) {
      queryUserList();
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

      <Modal
        title="添加用户"
        open={isModalVisible}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              console.log('Form values:', JSON.stringify(values));
              handleCreateUser(values);
              setIsModalVisible(false);
              form.resetFields();
            })
            .catch((errorInfo) => {
              console.log('Validation failed:', errorInfo);
              form.scrollToField(errorInfo.errorFields[0].name);
              form.setFields(errorInfo.errorFields);
            })
            .finally(() => {
              form.resetFields();
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
            roleCode: RoleStatusType.USER,
            status: UserStatusType.VALID,
          }}
        >
          <Form.Item label="用户名" name="userName" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label="昵称" name="nickName" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label="邮箱" name="email" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label="密码" name="password" rules={[requireRule]}>
            <Input.Password />
          </Form.Item>
          <Form.Item label="角色" name="roleCode" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={RoleStatusType.ADMIN}>管理员</Radio>
              <Radio value={RoleStatusType.USER}>用户</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={UserStatusType.VALID}>有效</Radio>
              <Radio value={UserStatusType.INVALID}>无效</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default UserManagement;
