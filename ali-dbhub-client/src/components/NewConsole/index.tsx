import React, { memo, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Button, Form, Select, DatePicker, Input, Table, Modal, Tabs, Dropdown, message, Tooltip } from 'antd';
import connectionService from '@/service/connection';
import { TreeNodeType } from '@/utils/constants'
import { ITreeNode } from '@/types';

interface IProps {
  className?: string;
}

export default memo<IProps>(function NewConsole(props) {
  const { className } = props;
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  const [windowName, setWindowName] = useState<string>('console_1');
  const [form] = Form.useForm();
  const [dataSourceList, setDataSourceList] = useState<ITreeNode[]>();
  const [currentDataSource, setCurrentDataSource] = useState<ITreeNode>();
  const [currentDB, setCurrentDB] = useState();
  const [DBList, setDBList] = useState<any>();

  const { Option } = Select;

  useEffect(() => {
    getDataSource();
  }, [])

  useEffect(() => {
    getDBList();
  }, [currentDataSource])

  function getDataSource() {
    console.log('getDataSource')
    let p = {
      pageNo: 1,
      pageSize: 100
    }

    connectionService.getList(p).then(res => {
      const treeData = res.data.map(t => {
        return {
          name: t.alias,
          key: t.id!.toString(),
          nodeType: TreeNodeType.DATASOURCE,
          dataSourceId: t.id,
        }
      })
      setDataSourceList(treeData);
    })
  }

  function getDBList() {
    connectionService.getDBList({
      id: currentDataSource!.key
    }).then(res => {
      setDBList(res.map(item => {
        return {
          ...item,
          // databaseType: currentDataSource.type
        }
      }))
    })
  }

  function handleOk() {
    // addWindowTab(windowList);
  }


  function handleCancel() {
    setIsModalVisible(false);
  }

  return <div className={classnames(styles.box, className)}>
    <Modal
      title="新窗口名称"
      open={isModalVisible}
      onOk={handleOk}
      onCancel={handleCancel}
      maskClosable={false}
      footer={
        <>
          <Button onClick={handleCancel} className={styles.cancel}>
            取消
          </Button>
          <Button type="primary" onClick={handleOk} className={styles.cancel}>
            添加
          </Button>
        </>
      }
    >
      <Form
        form={form}
        initialValues={{ remember: true }}
        autoComplete="off"
        className={styles.form}
      >
        <Form.Item
          label="连接类型"
          name="type"
        >
          <Select value={currentDataSource} onChange={setCurrentDataSource}>
            {
              dataSourceList?.map(item => {
                return <Option key={item.key} value={item.key}>{item.name}</Option>
              })
            }
          </Select>
        </Form.Item>
      </Form>
      <Input value={windowName} onChange={(e) => { setWindowName(e.target.value) }} />
    </Modal>
  </div >
})
