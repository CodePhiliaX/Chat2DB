import React, { memo, useRef, useState, createContext, useEffect, useMemo } from 'react';
import { Button, Modal, message } from 'antd';
import styles from './index.less';
import classnames from 'classnames';
import IndexList, { IIndexListRef } from './IndexList';
import ColumnList, { IColumnListRef } from './ColumnList';
import BaseInfo, { IBaseInfoRef } from './BaseInfo';
import sqlService, { IModifyTableSqlParams, IExecuteSqlParams } from '@/service/sql';
import MonacoEditor from '@/components/Console/MonacoEditor';
import { IEditTableInfo } from '@/typings';
import i18n from '@/i18n';
import lodash from 'lodash';

interface IProps {
  dataSourceId: number;
  databaseName: string;
  schemaName: string | undefined;
  tableName?: string;
}

interface ITabItem {
  index: number;
  title: string;
  key: string;
  component: any; // TODO: 组件的Ts是什么
}

interface IContext extends IProps {
  tableDetails: IEditTableInfo;
  baseInfoRef: React.RefObject<IBaseInfoRef>;
  columnListRef: React.RefObject<IColumnListRef>;
  indexListRef: React.RefObject<IIndexListRef>;
}

export const Context = createContext<IContext>({} as any);

export default memo((props: IProps) => {
  const { databaseName, dataSourceId, tableName, schemaName } = props;
  const [tableDetails, setTableDetails] = useState<IEditTableInfo>({} as any);
  const [oldTableDetails, setOldTableDetails] = useState<IEditTableInfo>({} as any);
  const [viewSqlModal, setViewSqlModal] = useState<string | false>(false);
  const [newTableName, setNewTableName] = useState<string | null | undefined>(tableName);
  const baseInfoRef = useRef<IBaseInfoRef>(null);
  const columnListRef = useRef<IColumnListRef>(null);
  const indexListRef = useRef<IIndexListRef>(null);
  const tabList = useMemo(() => {
    return [
      {
        index: 0,
        title: i18n('editTable.tab.basicInfo'),
        key: 'basic',
        component: <BaseInfo ref={baseInfoRef} />,
      },
      {
        index: 1,
        title: i18n('editTable.tab.columnInfo'),
        key: 'column',
        component: <ColumnList ref={columnListRef} />,
      },
      {
        index: 2,
        title: i18n('editTable.tab.indexInfo'),
        key: 'index',
        component: <IndexList ref={indexListRef} />,
      },
    ];
  }, []);
  const [currentTab, setCurrentTab] = useState<ITabItem>(tabList[0]);

  function changeTab(item: ITabItem) {
    setCurrentTab(item);
  }

  useEffect(() => {
    if (tableName) {
      getTableDetails();
    }
  }, []);

  const getTableDetails = () => {
    if (!newTableName) return;
    const params = {
      databaseName,
      dataSourceId,
      tableName: newTableName,
      schemaName,
      refresh: true,
    };
    sqlService.getTableDetails(params).then((res) => {
      const newTableDetails = lodash.cloneDeep(res);
      setTableDetails(newTableDetails || {});
      setOldTableDetails(res);
    });
  };

  function submit() {
    if (baseInfoRef.current && columnListRef.current && indexListRef.current) {
      const newTable = {
        ...oldTableDetails,
        ...baseInfoRef.current.getBaseInfo(),
        columnList: columnListRef.current.getColumnListInfo()!,
        indexList: indexListRef.current.getIndexListInfo()!,
      };

      const params: IModifyTableSqlParams = {
        databaseName,
        dataSourceId,
        schemaName,
        refresh: true,
        newTable,
      };

      if (newTableName) {
        // params.tableName = tableName;
        params.oldTable = oldTableDetails;
      }
      sqlService.getModifyTableSql(params).then((res) => {
        setNewTableName(newTable.name);
        setViewSqlModal(res?.[0].sql);
      });
    }
  }

  const executeSql = () => {
    const executeSQLParams: IExecuteSqlParams = {
      sql: viewSqlModal || '',
      dataSourceId,
      databaseName,
      schemaName,
    };

    sqlService.executeSql(executeSQLParams).then(() => {
      message.success(i18n('common.text.successfulExecution'));
      getTableDetails();
      setViewSqlModal(false);
    });
  };

  return (
    <Context.Provider
      value={{
        ...props,
        tableDetails,
        baseInfoRef,
        columnListRef,
        indexListRef,
      }}
    >
      <div className={classnames(styles.box)}>
        <div className={styles.header}>
          <div className={styles.tabList} style={{ '--i': currentTab.index } as any}>
            {tabList.map((item) => {
              return (
                <div
                  key={item.key}
                  onClick={changeTab.bind(null, item)}
                  className={classnames(styles.tabItem, currentTab.key == item.key ? styles.currentTab : '')}
                >
                  {item.title}
                </div>
              );
            })}
          </div>
          <div className={styles.saveButton}>
            <Button type="primary" onClick={submit}>
              {i18n('common.button.save')}
            </Button>
          </div>
        </div>
        <div className={styles.main}>
          {tabList.map((t) => {
            return (
              <div key={t.key} className={classnames(styles.tab, { [styles.hidden]: currentTab.key !== t.key })}>
                {t.component}
              </div>
            );
          })}
        </div>
      </div>
      <Modal
        title={i18n('editTable.title.sqlPreview')}
        open={!!viewSqlModal}
        onCancel={() => {
          setViewSqlModal(false);
        }}
        okText="执行"
        onOk={executeSql}
        width="60vw"
        maskClosable={false}
      >
        <div className={styles.monacoEditor}>
          <MonacoEditor
            id="view_sql"
            appendValue={{
              text: viewSqlModal,
              range: 'reset',
            }}
          />
        </div>
      </Modal>
    </Context.Provider>
  );
});
