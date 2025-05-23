/**
 * 这个组件只负责拿到用户选择的表名
 *  */
import React, {
  useMemo,
  useState,
  useRef,
  useContext,
  useEffect,
  forwardRef,
  ForwardedRef,
  useImperativeHandle,
} from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Table, Form, Select, Button } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { Context } from '../index';
import { IColumnItemNew, IIndexIncludeColumnItem } from '@/typings';
import { DatabaseTypeCode } from '@/constants';
import i18n from '@/i18n';
import lodash from 'lodash';
import Iconfont from '@/components/Iconfont';

interface IProps {
  includedColumnList: IIndexIncludeColumnItem[];
}

const createInitialData = () => {
  return {
    key: uuidv4(),
    ascOrDesc: null, // 升序还是降序
    cardinality: null, // 基数
    collation: null, // 排序规则
    columnName: null, // 列名
    comment: null, // 注释
    filterCondition: null, // 过滤条件
    indexName: null, // 索引名
    indexQualifier: null, // 索引限定符
    nonUnique: null, // 是否唯一
    ordinalPosition: null, // 位置
    schemaName: null, // 模式名
    type: null, // 类型
    pages: null, // 页数

    databaseName: null, // 数据库名
    tableName: null, // 表名
  };
};

export interface IIncludeColRef {
  getIncludeColInfo: () => IIndexIncludeColumnItem[];
}

const IncludeCol = forwardRef((props: IProps, ref: ForwardedRef<IIncludeColRef>) => {
  const { includedColumnList } = props;
  const { columnListRef, databaseType } = useContext(Context);
  const [dataSource, setDataSource] = useState<IIndexIncludeColumnItem[]>([createInitialData()]);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const isEditing = (record: IIndexIncludeColumnItem) => record.key === editingKey;
  const tableRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (includedColumnList.length) {
      setDataSource(
        includedColumnList.map((t) => {
          return {
            ...t,
            key: uuidv4(),
          };
        }),
      );
    }
  }, [includedColumnList]);

  const columnList: IColumnItemNew[] = useMemo(() => {
    const columnListInfo = columnListRef.current?.getColumnListInfo()?.filter((i) => i.name !== null);
    return columnListInfo || [];
  }, []);

  const edit = (record: any) => {
    form.setFieldsValue({ ...record });
    setEditingKey(record.key || null);
  };

  const addData = () => {
    const newData = createInitialData();
    setDataSource([...dataSource, newData]);
    edit(newData);
    setTimeout(() => {
      tableRef.current?.scrollTo(0, tableRef.current?.scrollHeight + 100);
    }, 0);
  };

  const deleteData = (record) => {
    setDataSource(dataSource.filter((i) => i.key !== record.key));
  };

  const columns = [
    {
      title: i18n('editTable.label.index'),
      dataIndex: 'index',
      width: '50px',
      align: 'center',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        return dataSource.findIndex((i) => i.key === record.key) + 1;
      },
    },
    {
      title: i18n('editTable.label.columnName'),
      dataIndex: 'columnName',
      // width: '45%',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="columnName" style={{ margin: 0 }}>
            <Select options={columnList.map((i) => ({ label: i.name, value: i.name }))} />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
          </div>
        );
      },
    },
    {
      title: i18n('editTable.label.order'),
      dataIndex: 'ascOrDesc',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="ascOrDesc" style={{ margin: 0 }}>
            <Select
              options={[
                { label: 'ASC', value: 'ASC' },
                { label: 'DESC', value: 'DESC' },
              ]}
            />
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
      render: (text: string, record: IIndexIncludeColumnItem) => {
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

    // {
    //   title: i18n('editTable.label.prefixLength'),
    //   dataIndex: 'prefixLength',
    //   width: '45%',
    //   render: (text: string, record: IIndexIncludeColumnItem) => {
    //     const editable = isEditing(record);
    //     return editable ? (
    //       <Form.Item name="prefixLength" style={{ margin: 0 }}>
    //         <InputNumber style={{ width: '100%' }} />
    //       </Form.Item>
    //     ) : (
    //       <div className={styles.editableCell} onClick={() => edit(record)}>
    //         {text}
    //       </div>
    //     );
    //   },
    // },
  ];
  // sqlLite 添加排序规则
  if (databaseType === DatabaseTypeCode.SQLITE) {
    columns.splice(2, 0, {
      title: i18n('editTable.label.collation'),
      dataIndex: 'collation',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="collation" style={{ margin: 0 }}>
            <Select
              options={[
                { label: 'BINARY', value: 'BINARY' },
                { label: 'NOCASE', value: 'NOCASE' },
                { label: 'RTRIM', value: 'RTRIM' },
              ]}
            />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
          </div>
        );
      },
    });
  }

  const handleFieldsChange = (field: any) => {
    const { value } = field[0];
    const { name: nameList } = field[0];
    const name = nameList[0];
    const newData = dataSource.map((item) => {
      if (item.key === editingKey) {
        return {
          ...item,
          [name]: value,
        };
      }
      return item;
    });
    setDataSource(newData);
  };

  const getIncludeColInfo = (): IIndexIncludeColumnItem[] => {
    return dataSource
      .map((t) => {
        return lodash.omit(t, 'key');
      })
      .filter((t) => t.columnName);
  };

  useImperativeHandle(ref, () => ({
    getIncludeColInfo,
  }));

  return (
    <div className={classnames(styles.includeCol)}>
      <div className={styles.indexListHeader}>
        <Button onClick={addData}>{i18n('editTable.button.add')}</Button>
        {/* <Button onClick={deleteData}>{i18n('editTable.button.delete')}</Button> */}
      </div>
      <Form className={styles.formBox} form={form} onFieldsChange={handleFieldsChange}>
        <Table
          ref={tableRef}
          style={{
            maxHeight: '100%',
            overflow: 'auto',
          }}
          sticky
          pagination={false}
          rowKey="key"
          columns={columns as any}
          dataSource={dataSource}
        />
      </Form>
    </div>
  );
});

export default IncludeCol;
