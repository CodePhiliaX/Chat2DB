import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { MenuOutlined } from '@ant-design/icons';
import type { DragEndEvent } from '@dnd-kit/core';
import { DndContext } from '@dnd-kit/core';
import { restrictToVerticalAxis } from '@dnd-kit/modifiers';
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Table, InputNumber, Input, Form, Select, Checkbox, Button } from 'antd';
import { v4 as uuidv4 } from 'uuid'

interface IProps {
  className?: string;
}

// 数据库字段类型 枚举
enum DatabaseFieldType {
  // 数字
  Number = 'number',
  // 字符串
  String = 'string',
  // 日期
  Date = 'date',
  // 布尔
  Boolean = 'boolean',
  // 二进制
  Binary = 'binary',
  // 对象
  Object = 'object',
}

const databaseFieldTypeList = [DatabaseFieldType['Number'], DatabaseFieldType['String'], DatabaseFieldType['Date'], DatabaseFieldType['Boolean'], DatabaseFieldType['Binary'], DatabaseFieldType['Object']]

interface Item {
  key: string;
  columnName: string;
  length: number | null;
  fieldType: DatabaseFieldType;
}

const mockData: Item[] = [
  {
    key: uuidv4(),
    columnName: 'John Brown',
    length: 32,
    fieldType: DatabaseFieldType.Binary,
  },
  {
    key: uuidv4(),
    columnName: 'Jim Green',
    length: 42,
    fieldType: DatabaseFieldType.Number,
  },
  {
    key: uuidv4(),
    columnName: 'Joe Black',
    length: 32,
    fieldType: DatabaseFieldType.String,
  },
]

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
}

const Row = ({ children, ...props }: RowProps) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    setActivatorNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({
    id: props['data-row-key'],
  });

  const style: React.CSSProperties = {
    ...props.style,
    transform: CSS.Transform.toString(transform && { ...transform, scaleY: 1 }),
    transition,
    ...(isDragging ? { position: 'relative', zIndex: 9999 } : {}),
  };

  return (
    <tr {...props} ref={setNodeRef} style={style} {...attributes}>
      {React.Children.map(children, (child) => {
        if ((child as React.ReactElement).key === 'sort') {
          return React.cloneElement(child as React.ReactElement, {
            children: (
              <MenuOutlined
                ref={setActivatorNodeRef}
                style={{ touchAction: 'none', cursor: 'move' }}
                {...listeners}
              />
            ),
          });
        }
        return child;
      })}
    </tr>
  );
};

const ColumnList: React.FC = () => {
  const [dataSource, setDataSource] = useState<Item[]>(mockData);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState('');

  const isEditing = (record: Item) => record.key === editingKey;

  const edit = (record: Partial<Item> & { key: React.Key }) => {
    form.setFieldsValue({ ...record });
    setEditingKey(record.key);
  };

  const columns = [
    {
      key: 'sort',
      width: '40px',
      align: 'center'
    },
    {
      title: 'columnName',
      dataIndex: 'columnName',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="columnName"
            style={{ margin: 0 }}
          >
            <Input />
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
      title: 'length',
      dataIndex: 'length',
      editable: true,
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="length"
            style={{ margin: 0 }}
          >
            <InputNumber />
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
      title: 'fieldType',
      dataIndex: 'fieldType',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="fieldType"
            style={{ margin: 0 }}
          >
            <Select
              style={{ width: 120 }}
              options={databaseFieldTypeList.map((i) => ({ label: i, value: i }))}
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
      title: 'nullable',
      dataIndex: 'nullable',
      render: (text: string, record: Item) => {
        return <Form.Item
          name="fieldType"
          style={{ margin: 0 }}
        >
          <Checkbox />
        </Form.Item>
      }
    },
    {
      title: 'annotation',
      dataIndex: 'annotation',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="annotation"
            style={{ margin: 0 }}
          >
            <Input />
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

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (active.id !== over?.id) {
      setDataSource((previous) => {
        const activeIndex = previous.findIndex((i) => i.key === active.id);
        const overIndex = previous.findIndex((i) => i.key === over?.id);
        return arrayMove(previous, activeIndex, overIndex);
      });
    }
  };

  const formChange = (value: any) => {
    const newData = form.getFieldsValue();
    setDataSource(dataSource.map(i => {
      if (i.key === editingKey) {
        return {
          ...i,
          ...newData
        }
      }
      return i
    }))
  }

  const addData = () => {
    const newData = {
      key: uuidv4(),
      columnName: '',
      length: null,
      fieldType: DatabaseFieldType.String,
    }
    setDataSource([...dataSource, newData])
    edit(newData)
  }

  const deleteData = () => {
    setDataSource(dataSource.filter(i => i.key !== editingKey))
  }

  const moveData = (action: 'up' | 'down') => {
    const index = dataSource.findIndex(i => i.key === editingKey)
    if (index === -1) {
      return
    }
    if (action === 'up') {
      if (index === 0) {
        return
      }
      const newData = [...dataSource]
      newData[index] = dataSource[index - 1]
      newData[index - 1] = dataSource[index]
      setDataSource(newData)
    } else {
      if (index === dataSource.length - 1) {
        return
      }
      const newData = [...dataSource]
      newData[index] = dataSource[index + 1]
      newData[index + 1] = dataSource[index]
      setDataSource(newData)
    }
  }

  return (
    <div className='box'>
      <div className={styles.columnListHeader}>
        <Button onClick={addData}>新增</Button>
        <Button onClick={deleteData}>删除</Button>
        <Button onClick={moveData.bind(null, 'up')}>上移</Button>
        <Button onClick={moveData.bind(null, 'down')}>下移</Button>
      </div>
      <Form form={form} onChange={formChange}>
        <DndContext modifiers={[restrictToVerticalAxis]} onDragEnd={onDragEnd}>
          <SortableContext
            items={dataSource.map((i) => i.key)}
            strategy={verticalListSortingStrategy}
          >
            <Table
              components={{
                body: {
                  row: Row,
                },
              }}
              pagination={false}
              rowKey="key"
              columns={columns}
              dataSource={dataSource}
            />
          </SortableContext>
        </DndContext>
      </Form>
    </div>
  );
};

export default ColumnList;
