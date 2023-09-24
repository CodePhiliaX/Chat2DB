/**
 * 这个组件只负责拿到用户选择的表名
 *  */
import React, { useMemo, useState, useContext, useEffect, forwardRef, ForwardedRef, useImperativeHandle } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Table, Form, Select, Button } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { Context } from '../index';
import { IColumnItemNew, IIndexIncludeColumnItem } from '@/typings';
import i18n from '@/i18n';

interface IProps {
  includedColumnList: IIndexIncludeColumnItem[];
}

const createInitialData = () => {
  return {
    key: uuidv4(),
    name: null,
  };
};

export interface IIncludeColRef {
  getIncludeColInfo: () => IIndexIncludeColumnItem[];
}

const InitialDataSource = [createInitialData()];

const IncludeCol = forwardRef((props: IProps, ref: ForwardedRef<IIncludeColRef>) => {
  const { includedColumnList } = props;
  const { columnListRef } = useContext(Context);
  const [dataSource, setDataSource] = useState<any[]>(InitialDataSource);
  const [form] = Form.useForm();
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const isEditing = (record: IIndexIncludeColumnItem) => record.key === editingKey;

  useEffect(() => {
    if (includedColumnList.length) {
      setDataSource(
        includedColumnList.map((t) => {
          return {
            key: uuidv4(),
            name: t.name,
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
    console.log([...dataSource, newData]);
    edit(newData);
  };

  const deleteData = () => {
    setDataSource(dataSource.filter((i) => i.key !== editingKey));
  };

  const columns = [
    {
      title: i18n('editTable.label.index'),
      dataIndex: 'index',
      width: '10%',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        return dataSource.findIndex((i) => i.key === record.key) + 1;
      },
    },
    {
      title: i18n('editTable.label.columnName'),
      dataIndex: 'name',
      // width: '45%',
      render: (text: string, record: IIndexIncludeColumnItem) => {
        const editable = isEditing(record);
        return editable ? (
          <Form.Item name="name" style={{ margin: 0 }}>
            <Select options={columnList.map((i) => ({ label: i.name, value: i.name }))} />
          </Form.Item>
        ) : (
          <div className={styles.editableCell} onClick={() => edit(record)}>
            {text}
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

  const handelFieldsChange = (field: any) => {
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

  const getIncludeColInfo = () => {
    const includeColInfo: IIndexIncludeColumnItem[] = [];
    dataSource.forEach((t) => {
      columnList.forEach((columnItem) => {
        if (t.name === columnItem.name) {
          const newColumnItem = {
            ...columnItem,
          };
          delete newColumnItem.key;
          includeColInfo.push({
            ...newColumnItem,
          });
        }
      });
    });
    return includeColInfo;
  };

  useImperativeHandle(ref, () => ({
    getIncludeColInfo,
  }));

  return (
    <div className={classnames(styles.box)}>
      <div className={styles.indexListHeader}>
        <Button onClick={addData}>{i18n('editTable.button.add')}</Button>
        <Button onClick={deleteData}>{i18n('editTable.button.delete')}</Button>
      </div>
      <Form form={form} onFieldsChange={handelFieldsChange}>
        <Table pagination={false} rowKey="key" columns={columns} dataSource={dataSource} />
      </Form>
    </div>
  );
});

export default IncludeCol;
