import React, { useEffect, useMemo, useState, memo, useContext } from 'react';
import { IBoundInfo, IntelligentEditorContext } from '../../index';
import { Dropdown } from 'antd';
import { useConnectionStore } from '@/pages/main/store/connection';
import connectionService from '@/service/connection';
import historyService from '@/service/history';
import Iconfont from '@/components/Iconfont';
import { databaseMap } from '@/constants/database';
import styles from './index.less';
import { setRegisterProvider } from '@/store/monaco';
import sqlService from '@/service/sql';

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

interface IOption<T> {
  key: string;
  label: string;
  value: T;
}

const emptyOption = {
  key: '',
  label: '',
  value: '',
};

const SelectBoundInfo = memo((props: IProps) => {
  const { boundInfo, setBoundInfo } = props;
  const { selectedTables, setSelectedTables, setTableNameList } = useContext(IntelligentEditorContext);
  const connectionList = useConnectionStore((state) => state.connectionList);
  const [databaseNameList, setDatabaseNameList] = useState<IOption<string>[]>([emptyOption]);
  const [schemaList, setSchemaList] = useState<IOption<string>[]>([emptyOption]);

  const dataSourceList = useMemo(() => {
    return (
      connectionList?.map((item) => ({
        key: item.id.toString(),
        label: item.alias,
        value: item.id,
        type: item.type,
      })) || []
    );
  }, [connectionList]);

  const supportDatabase = useMemo(() => {
    return connectionList?.find((item) => item.id === boundInfo.dataSourceId)?.supportDatabase;
  }, [boundInfo.dataSourceId]);

  const supportSchema = useMemo(() => {
    return connectionList?.find((item) => item.id === boundInfo.dataSourceId)?.supportSchema;
  }, [boundInfo.dataSourceId]);

  // 编辑器绑定的数据库类型变化时，重新注册智能提示
  useEffect(() => {
    registerIntelliSenseKeyword(boundInfo.databaseType);
  }, [boundInfo.dataSourceId]);

  // 当数据源变化时，重新获取数据库列表
  useEffect(() => {
    if (supportDatabase) {
      setSchemaList([]);
      setDatabaseNameList([]);
      getDatabaseList();
    } else {
      setSchemaList([]);
      getSchemaList();
    }
  }, [boundInfo.dataSourceId]);

  // 当数据库名变化时，重新获取schema列表
  useEffect(() => {
    if (supportSchema) {
      getSchemaList();
    }
    if (!supportSchema) {
      getAllTableNameList(boundInfo.dataSourceId, boundInfo.databaseName);
    }
  }, [boundInfo.databaseName]);

  useEffect(() => {
    if (supportSchema) {
      getAllTableNameList(boundInfo.dataSourceId, boundInfo.databaseName, boundInfo.schemaName);
    }
  }, [boundInfo.schemaName]);

  // 获取数据库列表
  const getDatabaseList = () => {
    connectionService
      .getDatabaseList({
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
        setRegisterProvider(boundInfo.dataSourceId, editorDatabaseTips);
        setDatabaseNameList([emptyOption, ..._databaseNameList]);
        registerIntelliSenseDatabase(editorDatabaseTips);
      });
  };

  // 获取schema列表
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
        const _schemaList = res.map((item) => ({
          key: item.name,
          label: item.name,
          value: item.name,
        }));
        setSchemaList([emptyOption, ..._schemaList]);
      });
  };

  // 选择数据源
  const changeDataSource = (item) => {
    const currentData = dataSourceList.find((i) => i.key === item.key)!;
    setBoundInfo({
      ...boundInfo,
      dataSourceId: currentData.value,
      dataSourceName: currentData.label,
      databaseType: currentData.type,
      databaseName: void 0,
      schemaName: void 0,
    });
    historyService.updateSavedConsole({
      id: boundInfo.consoleId,
      dataSourceId: currentData.value,
      dataSourceName: currentData.label,
      type: currentData.type,
    });
  };

  // 选择数据库
  const changeDataBase = (item) => {
    const _databaseName = databaseNameList?.find((i) => i.key === item.key)?.value;

    setBoundInfo({
      ...boundInfo,
      databaseName: _databaseName,
      schemaName: void 0,
    });

    historyService.updateSavedConsole({
      id: boundInfo.consoleId,
      databaseName: _databaseName,
    });
  };

  // 选择schema
  const changeSchema = (item) => {
    const _schemaName = schemaList?.find((i) => i.key === item.key)?.value;
    setBoundInfo({
      ...boundInfo,
      schemaName: _schemaName,
    });

    historyService.updateSavedConsole({
      id: boundInfo.consoleId,
      schemaName: _schemaName,
    });
  };

  const getAllTableNameList = (dataSourceId, databaseName, schemaName?) => {
    sqlService
      .getAllTableList({
        dataSourceId,
        databaseName,
        schemaName,
      })
      .then((data) => {
        const tableNameListTemp = data.map((t) => t.name);

        registerIntelliSenseTable(
          data,
          boundInfo.databaseType,
          boundInfo.dataSourceId,
          boundInfo.databaseName,
          boundInfo.schemaName,
        );

        registerIntelliSenseField(
          tableNameListTemp,
          boundInfo.dataSourceId,
          boundInfo.databaseName,
          boundInfo.schemaName,
        );

        setTableNameList(tableNameListTemp);
        setSelectedTables(tableNameListTemp.slice(0, 1));
      });
  };

  return (
    <div className={styles.consoleOptionsRight}>
      <div className={styles.boundInfoBoxSpacer} />
      <Dropdown
        menu={{
          items: dataSourceList,
          onClick: changeDataSource,
        }}
        trigger={['click']}
      >
        <div className={styles.boundInfoBox}>
          <Iconfont code={databaseMap[boundInfo.databaseType!]?.icon} />
          <div className={styles.boundInfoName}>{boundInfo.dataSourceName || `<${'dataSource'}>`}</div>
          <Iconfont code="&#x100be;" />
        </div>
      </Dropdown>

      {supportDatabase && (
        <Dropdown
          menu={{
            items: databaseNameList,
            onClick: changeDataBase,
          }}
          trigger={['click']}
        >
          <div className={styles.boundInfoBox}>
            <Iconfont code="&#xe669;" />
            <div className={styles.boundInfoName}>{boundInfo.databaseName || `<${'database'}>`}</div>
            <Iconfont code="&#x100be;" />
          </div>
        </Dropdown>
      )}

      {supportSchema && (
        <Dropdown
          menu={{
            items: schemaList,
            onClick: changeSchema,
          }}
          trigger={['click']}
        >
          <div className={styles.boundInfoBox}>
            <Iconfont code="&#xe663;" />
            <div className={styles.boundInfoName}>{boundInfo.schemaName || `<${'schema'}>`}</div>
            <Iconfont code="&#x100be;" />
          </div>
        </Dropdown>
      )}
    </div>
  );
});

export default SelectBoundInfo;
