import React, { memo, useState, useContext, useEffect, forwardRef, ForwardedRef, useImperativeHandle, } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { MenuOutlined } from '@ant-design/icons';
import { CSS } from '@dnd-kit/utilities';
import { Table, InputNumber, Input, Form, Select, Checkbox, Button, Modal, message } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { Context } from '../index';
import { IColumnItem } from '@/typings'

interface IProps {
  includedColumnList: IColumnItem[];
}

interface IIncludeColItem {
  key: string;
  columnName: string;
  prefixLength: number | null;
}

const createInitialData = () => {
  return {
    key: uuidv4(),
    columnName: '',
    prefixLength: null,
  }
}

export interface IIncludeColRef {
  getIncludeColInfo: () => IColumnItem[];
}

const InitialDataSource = [createInitialData()]

const IncludeCol = forwardRef((props: IProps, ref: ForwardedRef<IIncludeColRef>) => {
  const { includedColumnList } = props;
  const { columnListRef } = useContext(Context);
  const [dataSource, setDataSource] = useState<IIncludeColItem[]>(InitialDataSource);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState(dataSource[0]?.key);
  const [columnList, setColumnList] = useState<IColumnItem[]>([]);
  const isEditing = (record: IIncludeColItem) => record.key === editingKey;

  useEffect(() => {
    setDataSource(
      includedColumnList.map(t => {
        return {
          columnName: t.name,
          prefixLength: t.prefixLength || null,
          key: uuidv4(),
        }
      })
    )
  }, [includedColumnList])

  useEffect(() => {
    const columnListInfo = columnListRef.current?.getColumnListInfo();
    if (columnListInfo) {
      setColumnList(columnListInfo)
    }
    console.log(columnListInfo)
  }, [])

  const edit = (record: Partial<IIncludeColItem> & { key: React.Key }) => {
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
      render: (text: string, record: IIncludeColItem) => {
        return dataSource.findIndex(i => i.key === record.key) + 1;
      }

    },
    {
      title: 'columnName',
      dataIndex: 'columnName',
      width: '45%',
      render: (text: string, record: IIncludeColItem) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="columnName"
            style={{ margin: 0 }}
          >
            <Select
              options={columnList.map((i) => ({ label: i.name, value: i.name }))}
            />
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
      render: (text: string, record: IIncludeColItem) => {
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

  const getIncludeColInfo = () => {
    const IncludeColInfo: IColumnItem[] = []
    dataSource.forEach(t => {
      columnList.forEach(columnItem => {
        if (t.columnName === columnItem.name) {
          IncludeColInfo.push({
            ...columnItem,
            prefixLength: t.prefixLength,
          })
        }
      })
    })
    return IncludeColInfo
  }

  useImperativeHandle(ref, () => ({
    getIncludeColInfo,
  }));

  return <div className={classnames(styles.box)}>
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


export default IncludeCol
