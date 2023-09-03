import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { MenuOutlined } from '@ant-design/icons';
import { CSS } from '@dnd-kit/utilities';
import { Table, InputNumber, Input, Form, Select, Checkbox, Button, Modal, message } from 'antd';
import { v4 as uuidv4 } from 'uuid'

interface IProps {
  className?: string;
}

//索引类型
enum IndexesType {
  // 普通索引
  Normal = 'normal',
  // 唯一索引
  Unique = 'unique',
  // 全文索引
  Fulltext = 'fulltext',
  // 空间索引
  Spatial = 'spatial',
}

const indexesTypeList = [IndexesType['Normal'], IndexesType['Unique'], IndexesType['Fulltext'], IndexesType['Spatial']]

interface Item {
  key: string;
  columnInformation: string;
  prefixLength: number | null;
}

const createInitialData = () => {
  return {
    key: uuidv4(),
    columnInformation: '',
    prefixLength: null,
  }
}

const InitialDataSource = [createInitialData()]

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
}

export default memo<IProps>(function IndexList(props) {
  const { className } = props;
  const [dataSource, setDataSource] = useState<Item[]>(InitialDataSource);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState(dataSource[0]?.key);

  const isEditing = (record: Item) => record.key === editingKey;

  const edit = (record: Partial<Item> & { key: React.Key }) => {
    form.setFieldsValue({ ...record });
    setEditingKey(record.key);
  };

  const addData = () => {
    const newData = createInitialData()
    setDataSource([...dataSource, newData])
    edit(newData)
  }

  const deleteData = () => {
    // if (dataSource.length === 1) {
    //   message.warning('至少保留一条数据')
    //   return
    // }

    setDataSource(dataSource.filter(i => i.key !== editingKey))
  }

  const columns = [
    {
      title: 'index',
      dataIndex: 'index',
      width: '10%',
      render: (text: string, record: Item) => {
        return dataSource.findIndex(i => i.key === record.key) + 1;
      }

    },
    {
      title: 'columnInformation',
      dataIndex: 'columnInformation',
      width: '45%',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="columnInformation"
            style={{ margin: 0 }}
          >
            <Select options={indexesTypeList.map((i) => ({ label: i, value: i }))} />
          </Form.Item>
        ) : (
          <div
            className={styles.editableCell}
            onClick={() => edit(record)}
          >
            {text}
          </div>
        );
      }
    },
    {
      title: 'prefixLength',
      dataIndex: 'prefixLength',
      width: '45%',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="prefixLength"
            style={{ margin: 0 }}
          >
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        ) : (
          <div
            className={styles.editableCell}
            onClick={() => edit(record)}
          >
            {text}
          </div>
        );
      }
    },
  ];

  const onValuesChange = (changedValues: any, allValues: any) => {
    const newDataSource = dataSource?.map(i => {
      if (i.key === editingKey) {
        return {
          ...i,
          ...allValues,
        }
      }
      return i
    })
    setDataSource(newDataSource)
  }

  return <div className={classnames(styles.box, className)}>
    <div className={styles.indexListHeader}>
      <Button onClick={addData}>新增</Button>
      <Button onClick={deleteData}>删除</Button>
    </div>
    <Form form={form} onValuesChange={onValuesChange}>
      <Table
        pagination={false}
        rowKey="key"
        columns={columns}
        dataSource={dataSource}
      />
    </Form>
  </div>
})
