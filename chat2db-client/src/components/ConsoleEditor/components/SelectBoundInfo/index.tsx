import React, { useEffect, useMemo, useState } from 'react';
import { IBoundInfo } from '../../index';
import { Dropdown } from 'antd';
import { useConnectionStore } from '@/store/connection';
import connectionService from '@/service/connection';
import styles from './index.less';

import {
  // registerIntelliSenseField,
  registerIntelliSenseKeyword,
  // registerIntelliSenseTable,
  registerIntelliSenseDatabase,
} from '@/utils/IntelliSense';

interface IProps {
  boundInfo: IBoundInfo;
  setBoundInfo: (params: IBoundInfo) => void;
}

interface IOption<T> {
  key: string;
  label: string;
  value: T;
}

const SelectBoundInfo = (props: IProps) => {
  const { boundInfo, setBoundInfo } = props;
  const connectionList = useConnectionStore((state) => state.connectionList);
  const [databaseNameList, setDatabaseNameList] = useState<IOption<string>[]>();
  const [schemaList, setSchemaList] = useState<IOption<string>[]>();

  const dataSourceList = useMemo(() => {
    return connectionList?.map((item) => ({
      key: item.id.toString(),
      label: item.alias,
      value: item.id,
      type: item.type,
    }));
  }, [connectionList]);

  // 编辑器绑定的数据库类型变化时，重新注册智能提示
  useEffect(() => {
    registerIntelliSenseKeyword(boundInfo.type);
  }, [boundInfo.dataSourceId]);

  // 编辑器绑定的数据库类型变化时，重新注册智能提示
  useEffect(() => {}, [boundInfo.dataSourceId]);

  useEffect(() => {
    if (boundInfo.dataSourceId === null || boundInfo.dataSourceId === undefined) {
      return;
    }
    connectionService
      .getDBList({
        dataSourceId: boundInfo.dataSourceId,
      })
      .then((res) => {
        const editorDatabaseTips: any = [];
        const _databaseNameList = res.map((item) => {
          editorDatabaseTips.push({
            name: item.name,
            dataSourceName: boundInfo.dataSourceName,
          });
          return {
            key: item.name,
            label: item.name,
            value: item.name,
          };
        });
        if (!_databaseNameList.length) {
          getSchemaList();
        }
        setDatabaseNameList(_databaseNameList);
        registerIntelliSenseDatabase(editorDatabaseTips);
      });
  }, [boundInfo.dataSourceId]);

  const getSchemaList = () => {
    if (boundInfo.databaseName === null || boundInfo.databaseName === undefined) {
      return;
    }
    connectionService
      .getSchemaList({
        dataSourceId: boundInfo.dataSourceId!,
        databaseName: boundInfo?.databaseName,
      })
      .then((res: any) => {
        setSchemaList(
          res.map((item) => ({
            key: item.name,
            label: item.name,
            value: item.name,
          })),
        );
      });
  };

  useEffect(() => {
    getSchemaList();
  }, [boundInfo.databaseName]);

  const changeDataSource = (item) => {
    const curData = dataSourceList?.find((i) => i.key === item.key);
    setBoundInfo({
      ...boundInfo,
      dataSourceId: curData?.value,
      dataSourceName: curData?.label,
      type: curData?.type,  
      databaseName: void 0,
      schemaName: void 0,
    });
  };

  const changeDataBase = (item) => {
    const _databaseName = databaseNameList?.find((i) => i.key === item.key)?.value;

    setBoundInfo({
      ...boundInfo,
      databaseName: _databaseName,
    });
  };

  const changeSchema = (item) => {
    const _schemaName = schemaList?.find((i) => i.key === item.key)?.value;
    setBoundInfo({
      ...boundInfo,
      schemaName: _schemaName,
    });
  };

  return (
    <div className={styles.consoleOptionsRight}>
      <Dropdown
        menu={{
          items: dataSourceList,
          onClick: changeDataSource,
        }}
      >
        <div>{boundInfo.dataSourceName || '请选择数据库'}</div>
      </Dropdown>

      {boundInfo.dataSourceId && !!databaseNameList?.length && (
        <Dropdown
          menu={{
            items: databaseNameList,
            onClick: changeDataBase,
          }}
        >
          <div>{boundInfo.databaseName || '请选择数据库'}</div>
        </Dropdown>
      )}
      {boundInfo.databaseName && !!schemaList?.length && (
        <Dropdown
          menu={{
            items: schemaList,
            onClick: changeSchema,
          }}
        >
          <div>{boundInfo.schemaName || '请选择schema'}</div>
        </Dropdown>
      )}
    </div>
  );
};

export default SelectBoundInfo;
