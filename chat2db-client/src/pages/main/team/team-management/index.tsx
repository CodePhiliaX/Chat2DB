import React, { useEffect, useMemo, useState } from 'react';
import { createTeam, getTeamManagementList } from '@/service/team';
import { ITeamPageQueryVO, ITeamVO, RoleStatusType, TeamStatusType } from '@/typings/team';
import { Button, Form, Input, Modal, Radio, Table, Tag } from 'antd';
import { SearchOutlined, PlusOutlined } from '@ant-design/icons';
import styles from './index.less';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
  colon: false,
};

const requireRule = { required: true, message: 'Require field empty!' };

function TeamManagement() {
  const [form] = Form.useForm();
  const [dataSource, setDataSource] = useState<ITeamPageQueryVO[]>([]);
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
        render: (status: TeamStatusType) => (
          <Tag color={status === TeamStatusType.VALID ? 'green' : 'red'}>{status}</Tag>
        ),
      },
    ],
    [],
  );

  useEffect(() => {
    queryTeamList();
  }, [pagination.current, pagination.pageSize, pagination.searchKey]);

  const queryTeamList = async () => {
    const { searchKey, current: pageNo, pageSize } = pagination;
    let res = await getTeamManagementList({ searchKey, pageNo, pageSize });
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

  const handleCreateTeam = async (teamInfo: ITeamVO) => {
    let res = await createTeam(teamInfo);
    if (res) {
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
        title="添加团队"
        open={isModalVisible}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              console.log('Form values:', JSON.stringify(values));
              handleCreateTeam(values);
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
            status: TeamStatusType.VALID,
          }}
        >
          <Form.Item label="团队编码" name="code" rules={[requireRule]}>
            <Input />
          </Form.Item>
          <Form.Item label="团队名" name="name">
            <Input />
          </Form.Item>
          <Form.Item label="角色" name="roleCode" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={RoleStatusType.ADMIN}>管理员</Radio>
              <Radio value={RoleStatusType.USER}>用户</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[requireRule]}>
            <Radio.Group>
              <Radio value={TeamStatusType.VALID}>有效</Radio>
              <Radio value={TeamStatusType.INVALID}>无效</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="描述" name="description">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default TeamManagement;
