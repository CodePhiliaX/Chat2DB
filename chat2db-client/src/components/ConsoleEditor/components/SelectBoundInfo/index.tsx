import React, { useEffect, useMemo, useState } from 'react';
import { Select } from 'antd';
import { IBoundInfo } from '../../index';
import { useConnectionStore } from '@/store/connection';
import connectionService from '@/service/connection';

interface IProps {
  boundInfo: IBoundInfo;
  setBoundInfo : (params: IBoundInfo) => void;
}

interface IOption {
  label: string;
  value: any;
}

const SelectBoundInfo = (props:IProps) => {
  const { boundInfo, setBoundInfo } = props;
  const connectionList = useConnectionStore((state) => state.connectionList);
  const [databaseNameList, setDatabaseNameList] = useState<IOption[]>();
  const [schemaList, setSchemaList] = useState<IOption[]>();

  const dataSourceList = useMemo(()=>{
    return connectionList?.map((item) => ({
      label: item.alias,
      value: item.id,
    }));
  },[connectionList])

  useEffect(() => {
    if(boundInfo.dataSourceId === null || boundInfo.dataSourceId === undefined){
      return
    }
    connectionService.getDBList({
      dataSourceId: boundInfo.dataSourceId,
    }).then((res) => {
      const _databaseNameList = res.map((item) => ({
        label: item.name,
        value: item.name,
      }));
      if(!_databaseNameList.length){
        getSchemaList()
      }
      setDatabaseNameList(_databaseNameList);
    });
  }, [boundInfo.dataSourceId]);

  const getSchemaList = () => {
    if(boundInfo.databaseName === null || boundInfo.databaseName === undefined){
      return
    }
    connectionService.getSchemaList({
      dataSourceId: boundInfo.dataSourceId,
      databaseName: boundInfo?.databaseName
    }).then((res: any) => {
      setSchemaList(res.map((item) => ({
        label: item.name,
        value: item.name,
      })));
    });
  }

  useEffect(() => {
    getSchemaList();
  }, [boundInfo.databaseName]);

  return (
    <div>
      <Select
        defaultValue={boundInfo.dataSourceId}
        onChange={(value)=>{
          setBoundInfo({
            ...boundInfo,
            dataSourceId: value,
          })
        }}
        options={dataSourceList}
      />
      {
        boundInfo.dataSourceId && !!databaseNameList?.length &&
        <Select
          defaultValue={boundInfo.databaseName}
          onChange={(value) => {
            setBoundInfo({
              ...boundInfo,
              databaseName: value,
            })
          }}
          options={databaseNameList}
        />
      }
      {
        boundInfo.databaseName && !!schemaList?.length &&
        <Select
          defaultValue={boundInfo.schemaName}
          onChange={(value) => {
            setBoundInfo({
              ...boundInfo,
              schemaName: value,
            })
          }}
          options={schemaList}
        />
      }
    </div>
  );
};

export default SelectBoundInfo;
