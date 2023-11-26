import React, { useEffect, useMemo, useState, memo } from 'react';
import { IBoundInfo } from '../../index';
import { Dropdown } from 'antd';
import { useConnectionStore } from '@/pages/main/store/connection';
import connectionService from '@/service/connection';
import historyService from '@/service/history';
import Iconfont from '@/components/Iconfont';
import { databaseMap } from '@/constants/database';
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

const SelectBoundInfo = memo(
  (props: IProps) => {
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
      })) || [];
    }, [connectionList]);

    // 编辑器绑定的数据库类型变化时，重新注册智能提示
    useEffect(() => {
      registerIntelliSenseKeyword(boundInfo.databaseType);
    }, [boundInfo.dataSourceId]);

    // 当数据源变化时，重新获取数据库列表
    useEffect(() => {
      getDatabaseList();
    }, [boundInfo.dataSourceId]);
      
    // 当数据库名变化时，重新获取schema列表
    useEffect(() => {
      getSchemaList();
    }, [boundInfo.databaseName]);

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
        setDatabaseNameList(_databaseNameList);
        registerIntelliSenseDatabase(editorDatabaseTips);
      });
    }

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
          setSchemaList(
            res.map((item) => ({
              key: item.name,
              label: item.name,
              value: item.name,
            })),
          );
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
      })
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
      })
    };

    console.log(boundInfo)
  
    return (
      <div className={styles.consoleOptionsRight}>
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
  
        {boundInfo.dataSourceId && !!databaseNameList?.length && (
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

        {boundInfo.databaseName && !!schemaList?.length && (
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
  }
) 

export default SelectBoundInfo;
