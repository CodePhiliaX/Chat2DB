import ExecuteSQL from '@/components/ExecuteSQL';
import LoadingContent from '@/components/Loading/LoadingContent';
import { DatabaseTypeCode, WorkspaceTabType } from '@/constants';
import i18n from '@/i18n';
import sqlService, { IModifyTableSqlParams } from '@/service/sql';
import { IColumnTypes, IEditTableInfo, IWorkspaceTab } from '@/typings';
import { Button, Modal, message, Spin } from 'antd';
import classnames from 'classnames';
import lodash from 'lodash';
import React, { createContext, memo, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import BaseInfo, { IBaseInfoRef } from './BaseInfo';
import ColumnList, { IColumnListRef } from './ColumnList';
import ForeignKeyList, { IForeignKeyListRef } from './ForeignKeyList';
import styles from './index.less';
import IndexList, { IIndexListRef } from './IndexList';
import { setCurrentWorkspaceExtend, setPendingAiChat, ITableCommentResult } from '@/pages/main/workspace/store/common';

interface IProps {
  dataSourceId: number;
  databaseName: string;
  schemaName?: string | null;
  tableName?: string;
  databaseType: DatabaseTypeCode;
  changeTabDetails: (data: IWorkspaceTab) => void;
  tabDetails: IWorkspaceTab;
  submitCallback: () => void;
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
  foreignKeyListRef: React.RefObject<IForeignKeyListRef>;
  databaseSupportField: IDatabaseSupportField;
}

export const Context = createContext<IContext>({} as any);

interface IOption {
  label: string;
  value: string | number | null;
}

// 列字段类型，select组件的options需要的数据结构
interface IColumnTypesOption extends IColumnTypes {
  label: string;
  value: string | number | null;
}
export interface IDatabaseSupportField {
  columnTypes: IColumnTypesOption[];
  charsets: IOption[];
  collations: IOption[];
  indexTypes: IOption[];
  defaultValues: IOption[];
}

export default memo((props: IProps) => {
  const {
    databaseName,
    dataSourceId,
    tableName,
    schemaName,
    changeTabDetails,
    tabDetails,
    databaseType,
    submitCallback,
  } = props;
  const [tableDetails, setTableDetails] = useState<IEditTableInfo>({} as any);
  const [oldTableDetails, setOldTableDetails] = useState<IEditTableInfo>({} as any);
  const [viewSqlModal, setViewSqlModal] = useState<boolean>(false);
  const [guessLoading, setGuessLoading] = useState<boolean>(false);
  const baseInfoRef = useRef<IBaseInfoRef>(null);
  const columnListRef = useRef<IColumnListRef>(null);
  const indexListRef = useRef<IIndexListRef>(null);
  const foreignKeyListRef = useRef<IForeignKeyListRef>(null);
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
      {
        index: 3,
        title: i18n('editTable.tab.foreignKeyInfo'),
        key: 'foreignKey',
        component: <ForeignKeyList ref={foreignKeyListRef} />,
      },
    ];
  }, []);
  const [currentTab, setCurrentTab] = useState<ITabItem>(tabList[0]);
  const [databaseSupportField, setDatabaseSupportField] = useState<IDatabaseSupportField>({
    columnTypes: [],
    charsets: [],
    collations: [],
    indexTypes: [],
    defaultValues: [],
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  function changeTab(item: ITabItem) {
    setCurrentTab(item);
  }

  useEffect(() => {
    if (tableName) {
      getTableDetails();
    }
    getDatabaseFieldTypeList();
  }, []);

  // 获取数据库字段类型列表
  const getDatabaseFieldTypeList = () => {
    sqlService
      .getDatabaseFieldTypeList({
        dataSourceId,
        databaseName,
      })
      .then((res) => {
        const columnTypes =
          res?.columnTypes?.map((i) => {
            return {
              ...i,
              value: i.typeName,
              label: i.typeName,
            };
          }) || [];

        const charsets =
          res?.charsets?.map((i) => {
            return {
              value: i.charsetName,
              label: i.charsetName,
            };
          }) || [];

        const collations =
          res?.collations?.map((i) => {
            return {
              value: i.collationName,
              label: i.collationName,
            };
          }) || [];

        const indexTypes =
          res?.indexTypes?.map((i) => {
            return {
              value: i.typeName,
              label: i.typeName,
            };
          }) || [];

        const defaultValues =
          res?.defaultValues?.map((i) => {
            return {
              value: i.defaultValue,
              label: i.defaultValue,
            };
          }) || [];

        setDatabaseSupportField({
          columnTypes,
          charsets,
          collations,
          indexTypes,
          defaultValues,
        });
      });
  };

  const getTableDetails = (myParams?: { tableNameProps?: string }) => {
    const { tableNameProps } = myParams || {};
    const myTableName = tableNameProps || tableName;
    if (myTableName) {
      const params = {
        databaseName,
        dataSourceId,
        tableName: myTableName,
        schemaName,
        refresh: true,
      };
      setIsLoading(true);
      sqlService
        .getTableDetails(params)
        .then((res) => {
          const newTableDetails = lodash.cloneDeep(res);
          setTableDetails(newTableDetails || {});
          setOldTableDetails(res);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  };

  function submit() {
    if (baseInfoRef.current && columnListRef.current && indexListRef.current) {
      const newTable = {
        ...oldTableDetails,
        ...baseInfoRef.current.getBaseInfo(),
        columnList: columnListRef.current.getColumnListInfo()!,
        indexList: indexListRef.current.getIndexListInfo()!,
        foreignKeyList: foreignKeyListRef.current?.getForeignKeyListInfo(),
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

  const handleCommentGenerated = useCallback((result: ITableCommentResult) => {
    if (result.table_comment) {
      baseInfoRef.current?.setTableComment(result.table_comment);
    }
    if (result.column_comments && result.column_comments.length > 0) {
      result.column_comments.forEach((col) => {
        columnListRef.current?.setColumnComment(col.column_name, col.comment);
      });
    }
    message.success('AI 注释已应用到表单，请查看并保存');
  }, []);

  const openAiChatForGuess = useCallback(() => {
    if (!tableName) {
      message.warning('请先选择一个表');
      return;
    }

    setGuessLoading(true);
    setPendingAiChat({
      dataSourceId,
      databaseName,
      schemaName,
      tableNames: [tableName],
      message: `请为表 ${tableName} 及其所有字段生成合适的中文注释`,
      promptType: 'NL_2_COMMENT',
      onCommentGenerated: handleCommentGenerated,
    });

    setCurrentWorkspaceExtend('ai');
    setGuessLoading(false);
    message.success('已切换到 AI 助手，请在 AI 聊天面板中查看推荐结果');
  }, [dataSourceId, databaseName, schemaName, tableName, handleCommentGenerated]);

  const executeSuccessCallBack = () => {
    setViewSqlModal(false);
    message.success(i18n('common.text.successfulExecution'));
    const newTableName = baseInfoRef.current?.getBaseInfo().name;
    getTableDetails({ tableNameProps: newTableName });
    if (!tableName) {
      changeTabDetails({
        ...tabDetails,
        title: `${newTableName}`,
        type: WorkspaceTabType.EditTable,
        uniqueData: {
          ...(tabDetails.uniqueData || {}),
          tableName: newTableName,
        },
      });
    }
    // 保存成功后，刷新左侧树
    submitCallback?.();
  };

  return (
    <Context.Provider
      value={{
        ...props,
        tableDetails,
        baseInfoRef,
        columnListRef,
        indexListRef,
        foreignKeyListRef,
        databaseSupportField,
        databaseType,
      }}
    >
      <LoadingContent coverLoading isLoading={isLoading} className={classnames(styles.box)}>
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
            <Spin spinning={guessLoading}>
              <Button type="link" onClick={openAiChatForGuess}>
                {i18n('common.button.guess')}
              </Button>
            </Spin>
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
      </LoadingContent>
      <Modal
        title={i18n('editTable.title.sqlPreview')}
        open={!!viewSqlModal}
        onCancel={() => {
          setViewSqlModal(false);
        }}
        width="60vw"
        maskClosable={false}
        footer={false}
        destroyOnHidden={true}
      >
        <ExecuteSQL
          initSql={appendValue}
          databaseName={databaseName}
          dataSourceId={dataSourceId}
          tableName={tableName}
          schemaName={schemaName}
          databaseType={databaseType}
          executeSuccessCallBack={executeSuccessCallBack}
        />
      </Modal>
    </Context.Provider>
  );
});
