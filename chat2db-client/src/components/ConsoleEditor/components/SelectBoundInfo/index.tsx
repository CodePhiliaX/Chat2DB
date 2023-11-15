import React, { useEffect, useMemo, useState } from 'react';
import { IBoundInfo } from '../../index';
import { Select } from 'antd';
import { useConnectionStore } from '@/store/connection';
import connectionService from '@/service/connection';

import {
  registerIntelliSenseField,
  registerIntelliSenseKeyword,
  registerIntelliSenseTable,
  registerIntelliSenseDatabase,
} from '@/utils/IntelliSense';

interface IProps {
  boundInfo: IBoundInfo;
  setBoundInfo: (params: IBoundInfo) => void;
}

interface IOption {
  label: string;
  value: any;
}

const SelectBoundInfo = (props: IProps) => {
  const { boundInfo, setBoundInfo } = props;
  const connectionList = useConnectionStore((state) => state.connectionList);
  const [databaseNameList, setDatabaseNameList] = useState<IOption[]>();
  const [schemaList, setSchemaList] = useState<IOption[]>();

  const dataSourceList = useMemo(() => {
    return connectionList?.map((item) => ({
      label: item.alias,
      value: item.id,
    }));
  }, [connectionList]);

  // 编辑器绑定的数据库类型变化时，重新注册智能提示
  useEffect(() => {
    registerIntelliSenseKeyword(boundInfo.type);
  }, [boundInfo.type]);

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
        dataSourceId: boundInfo.dataSourceId,
        databaseName: boundInfo?.databaseName,
      })
      .then((res: any) => {
        setSchemaList(
          res.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        );
      });
  };

  useEffect(() => {
    getSchemaList();
  }, [boundInfo.databaseName]);

  return (
    <div>
      <Select
        defaultValue={boundInfo.dataSourceId}
        onChange={(value) => {
          setBoundInfo({
            ...boundInfo,
            dataSourceId: value,
          });
        }}
        options={dataSourceList}
      />
      {boundInfo.dataSourceId && !!databaseNameList?.length && (
        <Select
          defaultValue={boundInfo.databaseName}
          onChange={(value) => {
            setBoundInfo({
              ...boundInfo,
              databaseName: value,
            });
          }}
          options={databaseNameList}
        />
      )}
      {boundInfo.databaseName && !!schemaList?.length && (
        <Select
          defaultValue={boundInfo.schemaName}
          onChange={(value) => {
            setBoundInfo({
              ...boundInfo,
              schemaName: value,
            });
          }}
          options={schemaList}
        />
      )}
    </div>
  );
};

export default SelectBoundInfo;
