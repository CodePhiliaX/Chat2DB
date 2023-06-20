import {
  createUser,
  deleteUser,
  getUserList,
  updateUser,
} from '@/service/user';
import { IRole, IUser } from '@/typings/user';
import {
  Button,
  Table,
  Drawer,
  Form,
  Input,
  Select,
  Space,
  Row,
  Col,
  Tag,
  message,
} from 'antd';
import { ColumnsType } from 'antd/lib/table';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import styles from './index.less';

const RoleType: Record<IRole, string> = {
  admin: '管理者',
  normal: '普通用户',
};

const initUser = {
  userName: '',
  nickName: '',
  password: '123',
  password2: '123',
  email: '',
};

export default function Manage() {
  const [visible, setVisible] = useState(false);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);
  const [formData, setFormData] = useState<IUser>();
  const [listData, setListData] = useState<IUser[]>([]);

  useEffect(() => {
    queryUserList();
  }, [pageNo]);

  const queryUserList = useCallback(async () => {
    let res = await getUserList({
      pageNo,
      pageSize: 10,
    });
    setListData(res?.data);
    setTotal(res?.total);
  }, [pageNo]);

  const onOpen = useCallback(() => {
    setVisible(true);
  }, []);
  const onClose = useCallback(() => {
    setFormData(undefined);
    setVisible(false);
  }, []);

  /** 新增、编辑用户信息 */
  const addOrUpdateUser = async () => {
    if (!formData) return;
    let res;
    if (formData?.id) {
      res = await updateUser(formData);
    } else {
      res = await createUser(formData);
    }
    queryUserList();
    onClose();
  };

  const columns: ColumnsType<IUser> = useMemo(
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
        title: '邮箱',
        dataIndex: 'email',
        key: 'email',
      },
      {
        title: '角色',
        dataIndex: 'role',
        key: 'role',
        render: (role?: IRole) =>
          role && (
            <Tag color={role === 'admin' ? 'lime' : 'blue'}>
              {RoleType[role]}
            </Tag>
          ),
      },
      {
        title: '操作',
        key: 'operation',
        render: (item: IUser, record: IUser, index: number) => (
          <div>
            <Button
              type="link"
              onClick={() => {
                console.log('edit', item, record);
                setFormData({ ...item, password: undefined });
                onOpen();
              }}
            >
              编辑
            </Button>
            <Button
              type="link"
              onClick={async () => {
                if (!item?.id) return;

                await deleteUser({ id: item.id });
                message.success('删除成功');
                queryUserList();
              }}
            >
              删除
            </Button>
          </div>
        ),
      },
    ],
    [],
  );

  return (
    <div className={styles.manage}>
      <div className={styles['add-user']}>
        <Button
          onClick={() => {
            setFormData(initUser);
            onOpen();
          }}
          type="primary"
        >
          新增用户
        </Button>
      </div>
      <Table
        dataSource={listData}
        columns={columns}
        pagination={{
          total,
          current: pageNo,
          onChange: (page: number) => setPageNo(page),
        }}
      />

      {visible && (
        <Drawer
          title={formData?.id ? '编辑用户' : '新增用户'}
          placement="right"
          closable={false}
          onClose={onClose}
          open={visible}
          width={640}
          className={styles['add-user-drawer']}
          extra={
            <Space>
              <Button onClick={onClose}>取消</Button>
              <Button onClick={addOrUpdateUser} type="primary">
                确认
              </Button>
            </Space>
          }
        >
          <Form
            size="large"
            layout="horizontal"
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 20 }}
            initialValues={formData}
            onValuesChange={(_: any, newData: IUser) =>
              setFormData({ ...formData, ...newData })
            }
            scrollToFirstError
          >
            <Form.Item
              label="用户名"
              name="userName"
              rules={[
                {
                  required: true,
                  message: '请输入用户名!',
                  whitespace: true,
                },
              ]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label="昵称"
              name="nickName"
              rules={[
                {
                  required: true,
                  message: '请输入昵称!',
                  whitespace: true,
                },
              ]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label="邮箱"
              name="email"
              rules={[
                {
                  type: 'email',
                  message: '请输入正确邮箱地址!',
                },
                {
                  required: true,
                  message: '请输入邮箱地址!',
                },
              ]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label="密码"
              name={'password'}
              rules={[
                {
                  required: true,
                  message: '请输入密码!',
                },
              ]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              label="确认密码"
              name={'password2'}
              rules={[
                {
                  required: true,
                  message: '请确认密码!',
                },
                ({ getFieldValue }) => ({
                  validator(_, value: any) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('两次密码不一致'));
                  },
                }),
              ]}
            >
              <Input.Password />
            </Form.Item>

            <Form.Item
              label="角色"
              name="role"
              rules={[
                {
                  required: true,
                  message: '请选择用户角色!',
                },
              ]}
            >
              <Select
                size="large"
                placeholder="请选择"
                defaultValue={'user'}
                options={[
                  { label: '管理员', value: 'admin' },
                  { label: '用户', value: 'user' },
                ]}
              />
            </Form.Item>
          </Form>
        </Drawer>
      )}
    </div>
  );
}
