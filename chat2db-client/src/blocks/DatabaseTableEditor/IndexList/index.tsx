import React, {
  useState,
  forwardRef,
  ForwardedRef,
  useImperativeHandle,
  useContext,
  useRef,
  useMemo,
  useEffect,
} from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { MenuOutlined } from '@ant-design/icons';
import { type DragEndEvent, DndContext } from '@dnd-kit/core';
import { restrictToVerticalAxis } from '@dnd-kit/modifiers';
import { arrayMove, SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Table, Input, Form, Select, Modal } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import IncludeCol, { IIncludeColRef } from '../IncludeCol';
import { IIndexItem, IIndexIncludeColumnItem } from '@/typings';
import { EditColumnOperationType, DatabaseTypeCode } from '@/constants';
import Iconfont from '@/components/Iconfont';
import { Context } from '../index';
import i18n from '@/i18n';
import lodash from 'lodash';

interface IProps {}

export type IIndexListInfo = IIndexItem[];

export interface IIndexListRef {
  getIndexListInfo: () => IIndexListInfo;
}

const createInitialData = (): IIndexItem => {
  return {
    key: uuidv4(),
    columnList: [],
    name: '',
    type: null,
    comment: null,
    editStatus: EditColumnOperationType.Add,
  };
};

interface RowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  'data-row-key': string;
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

const IndexList = forwardRef((props: IProps, ref: ForwardedRef<IIndexListRef>) => {
  const { databaseSupportField, tableDetails, databaseType } = useContext(Context);
  const [dataSource, setDataSource] = useState<IIndexItem[]>([createInitialData()]);
  const [oldDataSource, setOldDataSource] = useState<IIndexItem[]>([]); // 用于记录上一次的数据，用于对比是否有变化
  const [form] = Form.useForm();
  const [editingData, setEditingData] = useState<IIndexItem | null>(null);
  const [includeColModalOpen, setIncludeColModalOpen] = useState(false);
  const includeColRef = useRef<IIncludeColRef>(null);
  const tableRef = useRef<HTMLDivElement>(null);

  const isEditing = (record: IIndexItem) => record.key === editingData?.key;

  const edit = (record: IIndexItem) => {
    form.setFieldsValue({ ...record });
    if (record.key !== editingData?.key) {
      setEditingData(record || null);
    }
  };

  useEffect(() => {
    const data = tableDetails.indexList?.map((i) => {
      const key = uuidv4();
      return {
        ...i,
        oldName: i.name,
        key,
      };
    });

    setOldDataSource(lodash.cloneDeep(data) || []);
    setDataSource(lodash.cloneDeep(data) || []);
  }, [tableDetails]);

  const addData = () => {
    const newData = createInitialData();
    setDataSource([...dataSource, newData]);
    edit(newData);
    setTimeout(() => {
      tableRef.current?.scrollTo(0, tableRef.current?.scrollHeight + 100);
    }, 0);
  };

  const deleteData = (record) => {
    const newList: any[] = dataSource
      ?.map((i) => {
        if (i.key === record?.key) {
          setEditingData(null);
          if (i.editStatus === EditColumnOperationType.Add) {
            return null;
          }
          return {
            ...i,
            editStatus: EditColumnOperationType.Delete,
          };
        }
        return i;
      })
      ?.filter((i) => i);
    setDataSource(newList || []);
  };

  const handleFieldsChange = (field: any) => {
    let { value } = field[0];
    const { name: nameList } = field[0];
    const name = nameList[0];
    if (name === 'nullable') {
      value = value ? 1 : 0;
    }
    const newData = dataSource.map((item) => {
      if (item.key === editingData?.key) {
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

  function getIndexListInfo(): IIndexListInfo {
    return dataSource.map((i) => {
      return lodash.omit(i, 'key');
    });
  }

  useImperativeHandle(ref, () => ({
    getIndexListInfo,
  }));

  const columns = useMemo(() => {
    const _columns = [
      {
        key: 'sort',
        width: '40px',
        align: 'center',
        fixed: 'left',
      },
      // {
      //   title: i18n('editTable.label.index'),
      //   width: '70px',
      //   align: 'center',
      //   render: (text: string, record: IIndexItem) => {
      //     return dataSource.findIndex((i) => i.key === record.key) + 1;
      //   },
      // },
      {
        title: i18n('editTable.label.indexName'),
        dataIndex: 'name',
        width: '180px',
        fixed: 'left',
        render: (text: string, record: IIndexItem) => {
          const editable = isEditing(record);
          return editable ? (
            <Form.Item name="name" style={{ margin: 0 }}>
              <Input autoComplete="off" />
            </Form.Item>
          ) : (
            <div className={styles.editableCell}>{text}</div>
          );
        },
      },
      {
        title: i18n('editTable.label.indexType'),
        dataIndex: 'type',
        width: '100px',
        render: (text: string, record: IIndexItem) => {
          const editable = isEditing(record);
          return editable ? (
            <Form.Item name="type" style={{ margin: 0 }}>
              <Select style={{ width: '100%' }}>
                {databaseSupportField?.indexTypes?.map((i) => (
                  <Select.Option key={i.value} value={i.value}>
                    {i.label}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          ) : (
            <div className={styles.editableCell}>{text}</div>
          );
        },
      },
      {
        title: i18n('editTable.label.includeColumn'),
        dataIndex: 'columnList',
        render: (columnList: IIndexIncludeColumnItem[], record: IIndexItem) => {
          const editable = isEditing(record);
          const text = columnList
            ?.map((t) => {
              return `${t.columnName}`;
            })
            .join(',');
          return editable ? (
            <div className={styles.columnListCell}>
              <span
                onClick={() => {
                  setIncludeColModalOpen(true);
                }}
              >
                {i18n('common.button.edit')}
              </span>
              {text}
            </div>
          ) : (
            <div className={styles.editableCell}>{text}</div>
          );
        },
      },
      {
        title: i18n('editTable.label.comment'),
        dataIndex: 'comment',
        render: (text: string, record: IIndexItem) => {
          const editable = isEditing(record);
          return editable ? (
            <Form.Item name="comment" style={{ margin: 0 }}>
              <Input autoComplete="off" />
            </Form.Item>
          ) : (
            <div className={styles.editableCell} onClick={() => edit(record)}>
              {text}
            </div>
          );
        },
      },
      {
        width: '40px',
        render: (text: string, record: IIndexItem) => {
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
    if (databaseType === DatabaseTypeCode.MYSQL) {
      _columns.splice(3, 0, {
        title: i18n('editTable.label.indexMethod'),
        dataIndex: 'method',
        width: '120px',
        render: (text: string, record: IIndexItem) => {
          const editable = isEditing(record);
          return editable ? (
            <Form.Item name="method" style={{ margin: 0 }}>
              <Select style={{ width: '100%' }}>
                {['HASH', 'BTREE'].map((i) => (
                  <Select.Option key={i} value={i}>
                    {i}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          ) : (
            <div className={styles.editableCell}>{text}</div>
          );
        },
      });
    }
    if (databaseType === DatabaseTypeCode.ORACLE) {
      _columns.splice(-2, 1);
    }
    return _columns;
    // TODO: isEditing 每次都会变所以这里是无意义的，后续在优化一下
  }, [isEditing]);

  const getIncludeColInfo = () => {
    setDataSource(
      dataSource.map((i) => {
        const columnList = includeColRef.current?.getIncludeColInfo();
        // 对比新老的IncludeColInfo有没有变化
        if (i.key === editingData?.key && columnList) {
          i.columnList = columnList;
          oldDataSource.map((old) => {
            if (old.key === editingData?.key) {
              if (!lodash.isEqual(old.columnList, columnList)) {
                i.editStatus = EditColumnOperationType.Modify;
              } else {
                i.editStatus = null;
              }
            }
          });
        }
        return i;
      }),
    );

    setIncludeColModalOpen(false);
  };

  const onRow = (record: any) => {
    return {
      onClick: () => {
        if (editingData?.key !== record.key) {
          edit(record);
        }
      },
    };
  };

  const indexIncludedColumnList: IIndexIncludeColumnItem[] = useMemo(() => {
    let list: IIndexIncludeColumnItem[] = [];
    dataSource.forEach((i) => {
      if (i.key === editingData?.key) {
        list = i.columnList || [];
      }
    });
    return list;
  }, [includeColModalOpen]);

  return (
    <div className={classnames(styles.indexList)}>
      {/* <div className={styles.indexListHeader}>
        <Button onClick={addData}>{i18n('editTable.button.add')}</Button>
        <Button onClick={deleteData}>{i18n('editTable.button.delete')}</Button>
        <Button onClick={moveData.bind(null, 'up')}>上移</Button>
        <Button onClick={moveData.bind(null, 'down')}>下移</Button>
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
            {i18n('editTable.button.addIndex')}
          </div>
        </div>
      </Form>
      <Modal
        className={styles.includeColModal}
        open={includeColModalOpen}
        width={460}
        title={i18n('editTable.label.includeColumn')}
        onOk={getIncludeColInfo}
        onCancel={() => {
          setIncludeColModalOpen(false);
        }}
        maskClosable={false}
        destroyOnClose={true}
      >
        <IncludeCol includedColumnList={indexIncludedColumnList} ref={includeColRef} />
      </Modal>
    </div>
  );
});

export default IndexList;
