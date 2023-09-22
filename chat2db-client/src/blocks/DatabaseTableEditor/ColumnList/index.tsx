import React, { useContext, useEffect, useState, forwardRef, ForwardedRef, useImperativeHandle } from 'react';
import styles from './index.less';
import { MenuOutlined } from '@ant-design/icons';
import { DndContext, type DragEndEvent } from '@dnd-kit/core';
import { restrictToVerticalAxis } from '@dnd-kit/modifiers';
import { Table, InputNumber, Input, Form, Select, Checkbox, Button } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { arrayMove, SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import sqlService from '@/service/sql';
import { Context } from '../index';
import { IColumnItemNew, IColumnTypes } from '@/typings';
import i18n from '@/i18n';
import { EditColumnOperationType } from '@/constants';
import CustomSelect from '@/components/CustomSelect';

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
}

interface IProps {}

interface IOption {
  label: string;
  value: string | number | null;
}

// 编辑配置
interface IEditingConfig extends IColumnTypes {
  editKey: string;
}

// 列字段类型，select组件的options需要的数据结构
interface IColumnTypesOption extends IColumnTypes {
  label: string;
  value: string | number | null;
}

// 本组件暴露给父组件的方法
export interface IColumnListRef {
  getColumnListInfo: () => IColumnItemNew[];
}

const Row = ({ children, ...props }: RowProps) => {
  const { attributes, listeners, setNodeRef, setActivatorNodeRef, transform, transition, isDragging } = useSortable({
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
              <MenuOutlined ref={setActivatorNodeRef} style={{ touchAction: 'none', cursor: 'move' }} {...listeners} />
            ),
          });
        }
        return child;
      })}
    </tr>
  );
};

// 创建一个空的数据结构
const createInitialData = () => {
  return {
    key: uuidv4(),
    oldName: null,
    name: null,
    tableName: null,
    columnType: null,
    dataType: null,
    defaultValue: null,
    autoIncrement: null,
    comment: null,
    primaryKey: null,
    schemaName: null,
    databaseName: null,
    typeName: null,
    columnSize: null,
    bufferLength: null,
    decimalDigits: null,
    numPrecRadix: null,
    nullableInt: null,
    sqlDataType: null,
    sqlDatetimeSub: null,
    charOctetLength: null,
    ordinalPosition: null,
    nullable: null,
    generatedColumn: null,
    editStatus: EditColumnOperationType.Add,
  };
};

const ColumnList = forwardRef((props: IProps, ref: ForwardedRef<IColumnListRef>) => {
  const { dataSourceId, databaseName, schemaName, tableDetails } = useContext(Context);
  const [dataSource, setDataSource] = useState<IColumnItemNew[]>([createInitialData()]);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const [editingConfig, setEditingConfig] = useState<IEditingConfig | null>(null);
  const [databaseSupportField, setDatabaseSupportField] = useState<{
    columnTypes: IColumnTypesOption[];
    charsets: IOption[];
    collations: IOption[];
  }>({
    columnTypes: [],
    charsets: [],
    collations: [],
  });

  const isEditing = (record: IColumnItemNew) => record.key === editingKey;

  const edit = (record: IColumnItemNew) => {
    if (record.key) {
      form.setFieldsValue({ ...record });
      setEditingKey(record.key);

      // 根据当前字段类型，设置编辑配置
      databaseSupportField.columnTypes.forEach((i) => {
        if (i.typeName === record.columnType) {
          setEditingConfig({
            ...i,
            editKey: record.key!,
          });
        }
      });
    }
  };

  // 整理服务端返回的数据，构造为前端需要的数据结构
  useEffect(() => {
    if (tableDetails) {
      const list =
        tableDetails?.columnList?.map((t) => {
          return {
            ...t,
            oldName: t.name,
            key: uuidv4(),
          };
        }) || [];
      setDataSource(list);
    }
  }, [tableDetails]);

  useEffect(() => {
    // 获取数据库字段类型列表
    sqlService
      .getDatabaseFieldTypeList({
        dataSourceId,
        databaseName,
      })
      .then((res) => {
        const columnTypes =
          res?.columnTypes?.map((i) => {
            return {
              ...i,
              value: i.typeName,
              label: i.typeName,
            };
          }) || [];

        const charsets =
          res?.charsets?.map((i) => {
            return {
              value: i.charsetName,
              label: i.charsetName,
            };
          }) || [];

        const collations =
          res?.collations?.map((i) => {
            return {
              value: i.collationName,
              label: i.collationName,
            };
          }) || [];

        setDatabaseSupportField({
          columnTypes,
          charsets,
          collations,
        });
      });
  }, []);

  const columns = [
    {
      key: 'sort',
      width: '40px',
      align: 'center',
    },
    {
      title: 'O T',
      dataIndex: 'editStatus',
      width: '60px',
      align: 'center',
      render: (text: EditColumnOperationType) => {
        return text === EditColumnOperationType.Add ? (
          <span style={{ color: '#52c41a' }}>新</span>
        ) : (
          <span style={{ color: '#f5222d' }}>原</span>
        );
      },
    },
    {
      title: i18n('editTable.label.columnName'),
      dataIndex: 'name',
      width: '160px',
      render: (text: string, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="name" style={{ margin: 0 }}>
            <Input />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.columnType'),
      dataIndex: 'columnType',
      width: '200px',
      render: (text: string, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return (
          <div>
            {editable ? (
              <Form.Item name="columnType" style={{ margin: 0, maxWidth: '184px' }}>
                <Select showSearch options={databaseSupportField.columnTypes} />
              </Form.Item>
            ) : (
              <div style={{ maxWidth: '184px' }} className={styles.editableCell} onClick={() => edit(record)}>
                {text}
              </div>
            )}
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.columnSize'),
      dataIndex: 'columnSize',
      width: '120px',
      render: (text: string, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="columnSize" style={{ margin: 0 }}>
            <InputNumber />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.nullable'),
      dataIndex: 'nullable',
      width: '100px',
      render: (nullable: number, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="nullable" style={{ margin: 0 }} valuePropName="checked">
            <Checkbox checked={nullable === 1} />
          </Form.Item>
        ) : (
          <div onClick={() => edit(record)}>
            <Checkbox checked={nullable === 1} />
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.comment'),
      dataIndex: 'comment',
      render: (text: string, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="comment" style={{ margin: 0 }}>
            <Input />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
          </div>
        );
      },
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

  const handelFieldsChange = (field: any) => {
    let { value } = field[0];
    const { name: nameList } = field[0];
    const name = nameList[0];
    if (name === 'nullable') {
      value = value ? 1 : 0;
    }

    const newData = dataSource.map((item) => {
      if (item.key === editingKey) {
        // 判断当前数据是新增的数据还是编辑后的数据
        let editStatus = item.editStatus;
        if (editStatus !== EditColumnOperationType.Add) {
          editStatus = EditColumnOperationType.Modify;
        }
        return {
          ...item,
          [name]: value,
          editStatus,
        };
      }
      return item;
    });
    setDataSource(newData);
    console.log(field);
  };

  const addData = () => {
    const newData = {
      ...createInitialData(),
    };
    setDataSource([...dataSource, newData]);
    edit(newData);
  };

  const deleteData = () => {
    setDataSource(
      dataSource.map((i) => {
        if (i.key === editingKey) {
          setEditingKey(null);
          setEditingConfig(null);
          return {
            ...i,
            editStatus: EditColumnOperationType.Delete,
          };
        }
        return i;
      }),
    );
  };

  const moveData = (action: 'up' | 'down') => {
    const index = dataSource.findIndex((i) => i.key === editingKey);
    if (index === -1) {
      return;
    }
    if (action === 'up') {
      if (index === 0) {
        return;
      }
      const newData = [...dataSource];
      newData[index] = dataSource[index - 1];
      newData[index - 1] = dataSource[index];
      setDataSource(newData);
    } else {
      if (index === dataSource.length - 1) {
        return;
      }
      const newData = [...dataSource];
      newData[index] = dataSource[index + 1];
      newData[index + 1] = dataSource[index];
      setDataSource(newData);
    }
  };

  function getColumnListInfo(): IColumnItemNew[] {
    return dataSource.map((i) => {
      return {
        ...i,
        tableName: tableDetails?.name,
        databaseName,
        schemaName: schemaName || null,
      };
    });
  }

  useImperativeHandle(ref, () => ({
    getColumnListInfo,
  }));

  const renderOtherInfoForm = () => {
    const labelCol = {
      style: { width: 90 },
    };

    return (
      <>
        {editingConfig?.supportDefaultValue && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.defaultValue')} name="defaultValue">
            <CustomSelect
              options={[
                {
                  label: 'EMPTY STRING',
                  value: 'EMPTY STRING',
                },
                {
                  label: 'NULL',
                  value: 'NULL',
                },
              ]}
            />
          </Form.Item>
        )}
        {editingConfig?.supportCharset && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.characterSet')} name="characterSet">
            <CustomSelect options={databaseSupportField.charsets} />
          </Form.Item>
        )}
        {editingConfig?.supportCollation && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.collation')} name="collation">
            <CustomSelect options={databaseSupportField.collations} />
          </Form.Item>
        )}
        {editingConfig?.supportScale && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.decimalPoint')} name="decimalPoint">
            <Input />
          </Form.Item>
        )}
      </>
    );
  };

  return (
    <div className={styles.columnList}>
      <div className={styles.columnListHeader}>
        <Button onClick={addData}>{i18n('editTable.button.add')}</Button>
        <Button onClick={deleteData}>{i18n('editTable.button.delete')}</Button>
        <Button onClick={moveData.bind(null, 'up')}>{i18n('editTable.button.up')}</Button>
        <Button onClick={moveData.bind(null, 'down')}>{i18n('editTable.button.down')}</Button>
      </div>
      <Form className={styles.formBox} form={form} onFieldsChange={handelFieldsChange}>
        <div className={styles.tableBox}>
          <DndContext modifiers={[restrictToVerticalAxis]} onDragEnd={onDragEnd}>
            <SortableContext items={dataSource.map((i) => i.key!)} strategy={verticalListSortingStrategy}>
              <Table
                components={{
                  body: {
                    row: Row,
                  },
                }}
                pagination={false}
                rowKey="key"
                columns={columns as any}
                dataSource={dataSource.filter((i) => i.editStatus !== EditColumnOperationType.Delete)}
              />
            </SortableContext>
          </DndContext>
        </div>
        <div className={styles.otherInfo}>
          <div className={styles.otherInfoFormBox}>{renderOtherInfoForm()}</div>
        </div>
      </Form>
    </div>
  );
});

export default ColumnList;
