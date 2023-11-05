import React, { useEffect, useState } from 'react';
import { Divider, Select, Typography } from 'antd';
import connection from '@/service/connection';
import cs from 'classnames';
import styles from './index.less';
import Iconfont from '../Iconfont';
import { databaseMap } from '@/constants/database';
import { registerIntelliSenseDatabase } from '@/utils/IntelliSense';

interface IProps {
  className?: string;
  curConnectionId?: number;
  onChange?: (value: { dataSourceId: number; databaseName: string; schemaName: string }) => void;
}

interface IOption {
  label: string | React.ReactNode;
  value: number | string;
}

function CascaderDB(props: IProps) {
  const [dataSourceOptions, setDataSourceOptions] = useState<IOption[]>([]);
  const [curDataSourceId, setCurDataSourceId] = useState<number | undefined>(props.curConnectionId);

  const [databaseOptions, setDatabaseOptions] = useState<IOption[]>([]);
  const [curDatabaseName, setCurDatabaseName] = useState<string>('');

  const [schemaOptions, setSchemaOptions] = useState<IOption[]>([]);
  const [curSchemeName, setCurSchemeName] = useState<string>('');

  useEffect(() => {
    loadDataSource();
  }, []);
  useEffect(() => {
    loadDatabase();
  }, [curDataSourceId]);
  useEffect(() => {
    loadSchema();
  }, [curDatabaseName]);

  const handleChangeDataSource = (value) => {
    setCurDataSourceId(value);
    setDatabaseOptions([]);
    setSchemaOptions([]);
    setCurDatabaseName('');
    setCurSchemeName('');

    props.onChange &&
      props.onChange({
        dataSourceId: value,
        databaseName: '',
        schemaName: '',
      });
  };
  const handleChangeDatabase = (value) => {
    setCurDatabaseName(value);
    setSchemaOptions([]);
    setCurSchemeName('');

    props.onChange &&
      props.onChange({
        dataSourceId: curDataSourceId!,
        databaseName: value,
        schemaName: '',
      });
  };

  const handleChangeSchema = (value) => {
    setCurSchemeName(value);

    props.onChange &&
      props.onChange({ dataSourceId: curDataSourceId!, databaseName: curDatabaseName, schemaName: value });
  };

  /** 加载DataSource数据 */
  const loadDataSource = async () => {
    // 请求 dataSource 数据
    const dataSourceList = await connection.getList({
      pageNo: 1,
      pageSize: 999,
      refresh: true,
    });
    const formattedData = (dataSourceList?.data || []).map((item) => ({
      ...item,
      key: `dataSource-${item.id}`,
      value: item.id,
      label: (
        <div className={styles.optionItem}>
          <Iconfont className={styles.optionItemIcon} code={databaseMap[item.type]?.icon} />
          <div className={styles.optionItemText}>{item.alias}</div>
        </div>
      ),
    }));
    setDataSourceOptions(formattedData);

    if (curDataSourceId === undefined) {
      setCurDataSourceId(formattedData[0]?.value);
    }
  };

  /** 加载Database数据 */
  const loadDatabase = async () => {
    if (curDataSourceId === undefined) {
      return;
    }
    const databaseList = await connection.getDBList({
      dataSourceId: curDataSourceId,
    });

    const formattedData = (databaseList || []).map((item) => ({
      ...item,
      key: `database-${item.name}`,
      value: item.name,
      label: (
        <div className={styles.optionItem}>
          <Iconfont className={styles.optionItemIcon} code="&#xe744;" />
          <div className={styles.optionItemText}>{item.name}</div>
        </div>
      ),
    }));

    setDatabaseOptions(formattedData);
    if (!curDatabaseName) {
      setCurDatabaseName(formattedData[0]?.value);
    }
  };

  const loadSchema = async () => {
    if (curDataSourceId === undefined || !curDatabaseName) {
      return;
    }
    const schemaList = await connection.getSchemaList({
      dataSourceId: curDataSourceId,
      databaseName: curDatabaseName,
      refresh: false,
    });

    const formattedData = (schemaList || []).map((item) => ({
      ...item,
      key: `schema-${item.name}`,
      value: item.name,
      label: (
        <div className={styles.optionItem}>
          <Iconfont className={styles.optionItemIcon} code="&#xe696;" />
          <div className={styles.optionItemText}>{item.name}</div>
        </div>
      ),
    }));

    setSchemaOptions(formattedData);
    if (!curSchemeName) {
      setCurSchemeName(formattedData[0]?.value);
    }
  };

  return (
    <div className={cs(props.className, styles.cascaderDB)}>
      <Select
        bordered={false}
        placeholder="请选择链接"
        showSearch
        popupMatchSelectWidth={false}
        options={dataSourceOptions}
        value={curDataSourceId}
        onChange={handleChangeDataSource}
        className={styles.select}
        dropdownRender={(menu) => (
          <>
            <div style={{ padding: '8px 16px' }}>
              <Typography.Title level={4}>链接源</Typography.Title>
              <Divider style={{ margin: '4px 0' }} />
            </div>
            {menu}
          </>
        )}
      />
      <Select
        bordered={false}
        placeholder="请选择数据库"
        showSearch
        popupMatchSelectWidth={false}
        options={databaseOptions}
        value={curDatabaseName}
        onChange={handleChangeDatabase}
        className={styles.select}
        dropdownRender={(menu) => (
          <>
            <div style={{ padding: '8px 16px' }}>
              <Typography.Title level={4}>数据库</Typography.Title>
              <Divider style={{ margin: '4px 0' }} />
            </div>
            {menu}
          </>
        )}
      />
      {!!schemaOptions.length && (
        <Select
          bordered={false}
          placeholder="请选择Schema"
          showSearch
          popupMatchSelectWidth={false}
          options={schemaOptions}
          value={curSchemeName}
          onChange={handleChangeSchema}
          className={styles.select}
          dropdownRender={(menu) => (
            <>
              <div style={{ padding: '8px 16px' }}>
                <Typography.Title level={4}>Schema</Typography.Title>
                <Divider style={{ margin: '4px 0' }} />
              </div>
              {menu}
            </>
          )}
        />
      )}
    </div>
  );
}

export default CascaderDB;
