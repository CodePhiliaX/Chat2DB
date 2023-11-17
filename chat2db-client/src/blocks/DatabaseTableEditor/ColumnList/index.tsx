import React, { useContext, useEffect, useState, useRef, forwardRef, ForwardedRef, useImperativeHandle } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { MenuOutlined } from '@ant-design/icons';
import { DndContext, type DragEndEvent } from '@dnd-kit/core';
import { restrictToVerticalAxis } from '@dnd-kit/modifiers';
import { Table, InputNumber, Input, Form, Select, Checkbox } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { arrayMove, SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Context } from '../index';
import { IColumnItemNew, IColumnTypes } from '@/typings';
import i18n from '@/i18n';
import { EditColumnOperationType, DatabaseTypeCode, NullableType } from '@/constants';
import CustomSelect from '@/components/CustomSelect';
import Iconfont from '@/components/Iconfont';

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
}

interface IProps {}

// 编辑配置
interface IEditingConfig extends IColumnTypes {
  editKey: string;
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
    primaryKeyOrder: null,
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
    charSetName: null,
    collationName: null,
    value: null,
    editStatus: EditColumnOperationType.Add,
  };
};

const ColumnList = forwardRef((props: IProps, ref: ForwardedRef<IColumnListRef>) => {
  const { databaseSupportField, databaseName, schemaName, tableDetails, databaseType } = useContext(Context);
  const [dataSource, setDataSource] = useState<IColumnItemNew[]>([createInitialData()]);
  const [form] = Form.useForm();
  const [editingData, setEditingData] = useState<IColumnItemNew | null>(null);
  const [editingConfig, setEditingConfig] = useState<IEditingConfig | null>(null);
  const tableRef = useRef<HTMLDivElement>(null);

  const isEditing = (record: IColumnItemNew) => record.key === editingData?.key;

  const edit = (record: IColumnItemNew) => {
    if (record.key) {
      form.setFieldsValue({ ...record });
      setEditingData(record);
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
      setEditingConfig(null);
      setDataSource(list);
    }
  }, [tableDetails]);

  const columns = [
    {
      key: 'sort',
      width: '40px',
      align: 'center',
      fixed: 'left',
    },
    // {
    //   title: 'O T',
    //   dataIndex: 'editStatus',
    //   width: '60px',
    //   align: 'center',
    //   render: (text: EditColumnOperationType) => {
    //     return text === EditColumnOperationType.Add ? (
    //       <span style={{ color: '#52c41a' }}>新</span>
    //     ) : (
    //       <span style={{ color: '#f5222d' }}>原</span>
    //     );
    //   },
    // },
    {
      title: i18n('editTable.label.columnName'),
      dataIndex: 'name',
      width: '160px',
      fixed: 'left',
      render: (text: string, record: IColumnItemNew) => {
        const editable = isEditing(record);
        return (
          <div className={styles.cellContent}>
            {editable ? (
              <Form.Item name="name" style={{ margin: 0 }}>
                <Input autoComplete="off" />
              </Form.Item>
            ) : (
              <div className={styles.editableCell}>{text}</div>
            )}
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
              <div style={{ maxWidth: '184px' }} className={styles.editableCell}>
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
            <InputNumber disabled={!editingConfig?.supportLength} />
          </Form.Item>
        ) : (
          <div className={styles.editableCell}>{text}</div>
        );
      },
    },
    {
      title: i18n('editTable.label.nullable'),
      dataIndex: 'nullable',
      width: '100px',
      render: (nullable: NullableType | null, record: IColumnItemNew) => {
        // const editable = isEditing(record);
        return (
          <div>
            <Checkbox
              onChange={() => {
                if (databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add) {
                  return null;
                }
                handelNullable(record);
              }}
              checked={nullable === NullableType.Null}
              disabled={
                editingConfig?.supportNullable === false ||
                !!record.primaryKey ||
                (databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add)
              }
            />
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.primaryKey'),
      dataIndex: 'primaryKey',
      width: '50px',
      render: (primaryKey: boolean, record: IColumnItemNew) => {
        return (
          <div>
            <div
              className={classnames(styles.keyBox, {
                [styles.disabledKeyBox]:
                  databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add,
              })}
              onClick={() => {
                if (databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add) {
                  return null;
                }
                handelPrimaryKey(record);
              }}
            >
              {primaryKey && <Iconfont code="&#xe775;" />}
              {primaryKey && <span>{record.primaryKeyOrder}</span>}
            </div>
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
            <Input autoComplete="off" disabled={!editingConfig?.supportComments} />
          </Form.Item>
        ) : (
          <div className={styles.editableCell}>{text}</div>
        );
      },
    },
    {
      width: '40px',
      render: (text: string, record: IColumnItemNew) => {
        // sqlLite不支持删除字段，新增的字段可以删除
        if (databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add) {
          return null;
        }
        return (
          <div
            className={styles.operationBar}
            onClick={() => {
              deleteData(record);
            }}
          >
            <div className={styles.deleteIconBox}>
              <Iconfont code="&#xe64e;" />
            </div>
          </div>
        );
      },
    },
  ];

  const handelPrimaryKey = (_data: IColumnItemNew) => {
    const newData = dataSource.map((item) => {
      let primaryKeyOrder: null | number = item.primaryKeyOrder;

      // 取消主键if
      if (_data.primaryKey) {
        // 如果取消的时当前的字段，主键顺序为null
        if (_data.key === item.key) {
          primaryKeyOrder = null;
        } else {
          // 如果当前字段是主键，取消主键的时候，比当前字段顺序大的字段顺序-1
          if (_data.primaryKeyOrder && item.primaryKeyOrder && item.primaryKeyOrder >= _data.primaryKeyOrder) {
            primaryKeyOrder = item.primaryKeyOrder - 1;
          }
        }
      } else {
        // 增加主键if
        // 增加主键的时候，主键顺序为当前表的最大主键顺序+1
        if (_data.key === item.key) {
          primaryKeyOrder =
            Math.max(
              ...dataSource.map((i) => {
                return i.primaryKeyOrder || 0;
              }),
            ) + 1;
        }
        // 对于当前字段之前的字段，主键顺序不变
      }

      if (item.key === _data?.key) {
        // 判断当前数据是新增的数据还是编辑后的数据
        let editStatus = item.editStatus;
        if (editStatus !== EditColumnOperationType.Add) {
          editStatus = EditColumnOperationType.Modify;
        }

        const editingDataItem = {
          ...item,
          primaryKey: !item.primaryKey,
          primaryKeyOrder,
          nullable: !item.primaryKey ? NullableType.NotNull : item.nullable,
          editStatus,
        };
        return editingDataItem;
      }

      return {
        ...item,
        primaryKeyOrder,
      };
    });
    setDataSource(newData);
  };

  const handelNullable = (_data: IColumnItemNew) => {
    const newData = dataSource.map((item) => {
      if (item.key === _data?.key) {
        // 判断当前数据是新增的数据还是编辑后的数据
        let editStatus = item.editStatus;
        if (editStatus !== EditColumnOperationType.Add) {
          editStatus = EditColumnOperationType.Modify;
        }
        const editingDataItem = {
          ...item,
          nullable: !item.nullable ? NullableType.Null : NullableType.NotNull,
          editStatus,
        };
        return editingDataItem;
      }
      return item;
    });
    setDataSource(newData);
  };

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (active.id !== over?.id) {
      setDataSource((previous) => {
        const activeIndex = previous.findIndex((i) => i.key === active.id);
        const overIndex = previous.findIndex((i) => i.key === over?.id);
        return arrayMove(previous, activeIndex, overIndex);
      });
    }
  };

  const handleFieldsChange = (field: any) => {
    let { value } = field[0];
    const { name: nameList } = field[0];
    const name = nameList[0];
    if (name === 'nullable') {
      value = value ? NullableType.Null : NullableType.NotNull;
    }

    const newData = dataSource.map((item) => {
      if (item.key === editingData?.key) {
        // 判断当前数据是新增的数据还是编辑后的数据
        let editStatus = item.editStatus;
        if (editStatus !== EditColumnOperationType.Add) {
          editStatus = EditColumnOperationType.Modify;
        }
        const editingDataItem = {
          ...item,
          [name]: value,
          editStatus,
        };

        if (name === 'columnType') {
          // 根据当前字段类型，设置编辑配置
          databaseSupportField.columnTypes.forEach((i) => {
            if (i.typeName === value) {
              setEditingConfig({
                ...editingConfig!,
                ...i,
              });
            }
          });
          // 特殊处理VARCHAR的默认长度 为255
          if (value === 'VARCHAR' && editingDataItem.columnSize === null) {
            editingDataItem.columnSize = 255;
            form.setFieldsValue({
              columnSize: 255,
            });
          }
        }
        return editingDataItem;
      }
      return item;
    });
    setDataSource(newData);
  };

  const addData = () => {
    const newData = {
      ...createInitialData(),
    };
    setDataSource([...dataSource, newData]);
    edit(newData);
    setTimeout(() => {
      tableRef.current?.scrollTo(0, tableRef.current?.scrollHeight + 100);
    }, 0);
  };

  const deleteData = (record) => {
    let list: any = [];
    if (record?.editStatus === EditColumnOperationType.Add) {
      list = dataSource.filter((i) => i.key !== record?.key);
    } else {
      list = dataSource.map((i) => {
        if (i.key === record?.key) {
          setEditingData(null);
          setEditingConfig(null);
          return {
            ...i,
            editStatus: EditColumnOperationType.Delete,
          };
        }
        return i;
      });
    }
    setDataSource(list);
  };

  function getColumnListInfo(): IColumnItemNew[] {
    return dataSource.map((i) => {
      const data = {
        ...i,
        tableName: tableDetails?.name,
        databaseName,
        schemaName: schemaName || null,
      };
      delete data.key;
      return data;
    });
  }

  useImperativeHandle(ref, () => ({
    getColumnListInfo,
  }));

  const renderOtherInfoForm = () => {
    const labelCol = {
      style: { width: 100 },
    };

    return (
      <>
        {editingConfig?.supportAutoIncrement && (
          <Form.Item
            labelCol={labelCol}
            label={i18n('editTable.label.autoIncrement')}
            name="autoIncrement"
            valuePropName="checked"
          >
            <Checkbox />
          </Form.Item>
        )}
        {databaseType === DatabaseTypeCode.SQLSERVER && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.sparse')} name="sparse" valuePropName="checked">
            <Checkbox />
          </Form.Item>
        )}
        {editingConfig?.supportDefaultValue && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.defaultValue')} name="defaultValue">
            <CustomSelect options={databaseSupportField.defaultValues} />
          </Form.Item>
        )}
        {editingConfig?.supportCharset && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.characterSet')} name="charSetName">
            <CustomSelect options={databaseSupportField.charsets} />
          </Form.Item>
        )}
        {editingConfig?.supportCollation && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.collation')} name="collationName">
            <CustomSelect options={databaseSupportField.collations} />
          </Form.Item>
        )}
        {editingConfig?.supportScale && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.decimalPoint')} name="decimalDigits">
            <Input autoComplete="off" />
          </Form.Item>
        )}
        {editingConfig?.supportUnit && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.unit')} name="unit">
            <Select style={{ width: '100%' }}>
              {['CHAR', 'BYTE'].map((i) => (
                <Select.Option key={i} value={i}>
                  {i}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        )}
        {editingConfig?.supportValue && (
          <Form.Item labelCol={labelCol} label={i18n('editTable.label.value')} name="value">
            <Input autoComplete="off" />
          </Form.Item>
        )}
      </>
    );
  };

  const onRow = (record: any) => {
    return {
      onClick: () => {
        // sqlLite不支持修改字段,新增的字段可以修改
        if (databaseType === DatabaseTypeCode.SQLITE && record.editStatus !== EditColumnOperationType.Add) {
          return;
        }
        if (editingData?.key !== record.key) {
          edit(record);
        }
      },
    };
  };

  return (
    <div className={styles.columnList}>
      {/* <div className={styles.columnListHeader}>
        <Button onClick={addData}>{i18n('editTable.button.add')}</Button>
        <Button onClick={deleteData}>{i18n('editTable.button.delete')}</Button>
        <Button onClick={moveData.bind(null, 'up')}>{i18n('editTable.button.up')}</Button>
        <Button onClick={moveData.bind(null, 'down')}>{i18n('editTable.button.down')}</Button>
      </div> */}
      <Form className={styles.formBox} form={form} onFieldsChange={handleFieldsChange}>
        <div className={styles.tableBox}>
          <DndContext modifiers={[restrictToVerticalAxis]} onDragEnd={onDragEnd}>
            <SortableContext items={dataSource.map((i) => i.key!)} strategy={verticalListSortingStrategy}>
              <Table
                ref={tableRef}
                components={{
                  body: {
                    row: Row,
                  },
                }}
                style={{
                  maxHeight: '100%',
                  overflow: 'auto',
                }}
                sticky
                onRow={onRow}
                pagination={false}
                rowKey="key"
                columns={columns as any}
                scroll={{ x: '100%' }}
                dataSource={dataSource.filter((i) => i.editStatus !== EditColumnOperationType.Delete)}
              />
            </SortableContext>
          </DndContext>
          <div onClick={addData} className={styles.addColumnButton}>
            <Iconfont code="&#xe631;" />
            {i18n('editTable.button.addColumn')}
          </div>
        </div>

        <div className={styles.otherInfo}>
          <div className={styles.otherInfoFormBox}>{renderOtherInfoForm()}</div>
        </div>
      </Form>
    </div>
  );
});

export default ColumnList;
