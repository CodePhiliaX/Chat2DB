import React, { memo, useRef, useState, createContext, useEffect, useMemo } from 'react';
import { Button, Modal, message } from 'antd';
import styles from './index.less';
import classnames from 'classnames';
import IndexList, { IIndexListRef } from './IndexList';
import ColumnList, { IColumnListRef } from './ColumnList';
import BaseInfo, { IBaseInfoRef } from './BaseInfo';
import sqlService, { IModifyTableSqlParams, IExecuteSqlParams } from '@/service/sql';
import MonacoEditor, { IExportRefFunction } from '@/components/Console/MonacoEditor';
import { IEditTableInfo, IWorkspaceTab, IManageResultData } from '@/typings';
import { DatabaseTypeCode } from '@/constants';
import i18n from '@/i18n';
import lodash from 'lodash';
import Iconfont from '@/components/Iconfont';
import { formatSql } from '@/utils';

interface IProps {
  dataSourceId: number;
  databaseName: string;
  schemaName: string | undefined;
  tableName?: string;
  databaseType: DatabaseTypeCode;
  changeTabDetails: (data: IWorkspaceTab) => void;
  tabDetails: IWorkspaceTab;
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
  const { databaseName, dataSourceId, tableName, schemaName, changeTabDetails, tabDetails, databaseType } = props;
  const [tableDetails, setTableDetails] = useState<IEditTableInfo>({} as any);
  const [oldTableDetails, setOldTableDetails] = useState<IEditTableInfo>({} as any);
  const [viewSqlModal, setViewSqlModal] = useState<boolean>(false);
  const baseInfoRef = useRef<IBaseInfoRef>(null);
  const columnListRef = useRef<IColumnListRef>(null);
  const indexListRef = useRef<IIndexListRef>(null);
  const monacoEditorRef = useRef<IExportRefFunction>(null);
  const [executeSqlResult, setExecuteSqlResult] = useState<IManageResultData[]>();
  const [executeLoading, setExecuteLoading] = useState<boolean>(false);
  const [appendValue, setAppendValue] = useState<string>('');
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
    if (!tableName) return;
    const params = {
      databaseName,
      dataSourceId,
      tableName,
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

      if (tableName) {
        // params.tableName = tableName;
        params.oldTable = oldTableDetails;
      }
      sqlService.getModifyTableSql(params).then((res) => {
        setViewSqlModal(true);
        setAppendValue(res?.[0].sql);
      });
    }
  }

  const executeSql = () => {
    const executeSQLParams: IExecuteSqlParams = {
      sql: monacoEditorRef.current?.getAllContent() || '',
      dataSourceId,
      databaseName,
      schemaName,
    };
    setExecuteLoading(true);
    sqlService
      .executeSql(executeSQLParams)
      .then((res) => {
        if (!tableName) {
          const newTableName = baseInfoRef.current?.getBaseInfo().name;
          changeTabDetails({
            ...tabDetails,
            title: `edit-${newTableName}`,
            uniqueData: {
              ...(tabDetails.uniqueData || {}),
              tableName: newTableName,
            },
          });
        }
        if (res.filter((t) => !t.success).length === 0) {
          setViewSqlModal(false);
          message.success(i18n('common.text.successfulExecution'));
        }
        setExecuteSqlResult(res);
        getTableDetails();
      })
      .finally(() => {
        setExecuteLoading(false);
      });
  };

  //
  const renderMonacoEditor = useMemo(() => {
    return (
      <MonacoEditor
        className={styles.monacoEditor}
        id="view_sql"
        ref={monacoEditorRef}
        appendValue={{
          text: appendValue,
          range: 'reset',
        }}
      />
    );
  }, [appendValue]);

  const handleFormatSql = () => {
    const sql = monacoEditorRef.current?.getAllContent() || '';
    formatSql(sql, databaseType).then((res) => {
      setAppendValue(res);
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
        width="60vw"
        maskClosable={false}
        footer={false}
      >
        <div className={styles.monacoEditorModal}>
          <div className={styles.monacoEditorContent}>
            <div className={styles.monacoEditorHeader}>
              <div className={styles.formatButton} onClick={handleFormatSql}>
                <Iconfont code="&#xe64f;" />
                {i18n('common.button.format')}
              </div>
              <Button className={styles.executeButton} type="primary" onClick={executeSql} loading={executeLoading}>
                <Iconfont code="&#xe656;" />
                {i18n('common.button.execute')}
              </Button>
            </div>
            {renderMonacoEditor}
          </div>
          {executeSqlResult && (
            <div className={styles.result}>
              <div className={styles.resultHeader}>{i18n('common.text.errorMessage')}</div>
              <div className={styles.resultContent}>
                {executeSqlResult
                  ?.filter((t) => !t.success)
                  .map((t, i) => {
                    return (
                      <div key={i}>
                        <div className={styles.errorTitle}>
                          <Iconfont code="&#xe87c;" />
                          sql{i + 1}:{t.sql}
                        </div>
                        <div className={styles.errorMessage}>{t.message}</div>
                      </div>
                    );
                  })}
              </div>
            </div>
          )}
        </div>
      </Modal>
    </Context.Provider>
  );
});
