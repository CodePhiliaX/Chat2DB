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
import { Table, InputNumber, Input, Form, Select, Checkbox, Button, Modal } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import IncludeCol from '../IncludeCol';

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
  columnInformation: string[];
  indexName: string;
  indexesType: IndexesType | null;
}

const initialData: Item[] = [
  {
    key: uuidv4(),
    columnInformation: [],
    indexName: '',
    indexesType: null,
  },
]

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
}

export default memo<IProps>(function IndexList(props) {
  const { className } = props;
  const [dataSource, setDataSource] = useState<Item[]>(initialData);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState(dataSource[0]?.key);
  const [includeColModalOpen, setIncludeColModalOpen] = useState(false);

  const isEditing = (record: Item) => record.key === editingKey;

  const edit = (record: Partial<Item> & { key: React.Key }) => {
    form.setFieldsValue({ ...record });
    setEditingKey(record.key);
  };

  const addData = () => {
    const newData = {
      key: uuidv4(),
      columnInformation: [],
      indexName: '',
      indexesType: null,
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

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (active.id !== over?.id) {
      setDataSource((previous) => {
        const activeIndex = previous.findIndex((i) => i.key === active.id);
        const overIndex = previous.findIndex((i) => i.key === over?.id);
        return arrayMove(previous, activeIndex, overIndex);
      });
    }
  };

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

  const columns = [
    {
      key: 'sort',
      width: '60px',
    },
    {
      title: 'index',
      width: '70px',
      render: (text: string, record: Item) => {
        return dataSource.findIndex(i => i.key === record.key) + 1
      }
    },
    {
      title: '索引名称',
      dataIndex: 'indexName',
      width: '30%',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="indexName"
            style={{ margin: 0 }}
          >
            <Input />
          </Form.Item>
        ) : <div
          className={styles.editableCell}
          onClick={() => edit(record)}
        >
          {text}
        </div>
      }
    },
    {
      title: '索引类型',
      dataIndex: 'indexesType',
      width: '30%',
      render: (text: string, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item
            name="indexesType"
            style={{ margin: 0 }}
          >
            <Select style={{ width: '100%' }}>
              {indexesTypeList.map(i => <Select.Option key={i} value={i}>{i}</Select.Option>)}
            </Select>
          </Form.Item>
        ) : <div
          className={styles.editableCell}
          onClick={() => edit(record)}
        >
          {text}
        </div>
      }
    },
    {
      title: '包含列',
      dataIndex: 'columnInformation',
      render: (text: string[], record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <div className={styles.columnInformation}>
            <span onClick={() => { setIncludeColModalOpen(true) }}>编辑</span>
            {text.join(',')}
          </div>
        ) : (
          <div
            className={styles.editableCell}
            onClick={() => edit(record)}
          >
            {text.join(',')}
          </div>
        );
      }
    },

  ];

  return <div className={classnames(styles.box, className)}>
    <div className={styles.indexListHeader}>
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
    <Modal
      open={includeColModalOpen}
      width={800}
      title="包含列"
      onCancel={() => { setIncludeColModalOpen(false) }}
      maskClosable={false}
    >
      <IncludeCol></IncludeCol>
    </Modal>
  </div >
})
