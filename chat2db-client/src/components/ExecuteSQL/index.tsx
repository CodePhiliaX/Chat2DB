import React, { memo, useRef, useState, useMemo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import MonacoEditor, { IExportRefFunction } from '@/components/Console/MonacoEditor';
import i18n from '@/i18n';
import { Button } from 'antd';
import { formatSql } from '@/utils/sql';
import sqlService, { IExecuteSqlParams } from '@/service/sql';
import { DatabaseTypeCode } from '@/constants';

interface IProps {
  className?: string;
  initSql: string | null;
  initError?: string | null;
  databaseType: DatabaseTypeCode;
  databaseName: string;
  dataSourceId: number;
  schemaName?: string | null;
  tableName?: string;
  executeSuccessCallBack: () => void;
  executeSqlApi?: 'executeUpdateDataSql'; // 两个地方用到了这个组件，但是两个需要的执行sql的接口不一样
}

export default memo<IProps>((props) => {
  const {
    className,
    initSql = null,
    initError = null,
    databaseType,
    databaseName,
    dataSourceId,
    schemaName,
    tableName,
    executeSqlApi = 'executeDDL',
    executeSuccessCallBack,
  } = props;
  const monacoEditorRef = useRef<IExportRefFunction>(null);
  const [executeLoading, setExecuteLoading] = useState<boolean>(false);
  const [appendValue, setAppendValue] = useState<string>('');
  const [executeSqlResult, setExecuteSqlResult] = useState<string | null>(null);

  useEffect(() => {
    setAppendValue(initSql || '');
    setExecuteSqlResult(initError);
  }, []);

  const handleFormatSql = () => {
    const sql = monacoEditorRef.current?.getAllContent() || '';
    formatSql(sql, databaseType).then((res) => {
      setAppendValue(res);
    });
  };

  const executeSql = () => {
    const executeSQLParams: IExecuteSqlParams = {
      sql: monacoEditorRef.current?.getAllContent() || '',
      dataSourceId,
      databaseName,
      schemaName,
      tableName,
    };
    setExecuteLoading(true);
    sqlService[executeSqlApi](executeSQLParams)
      .then((res) => {
        if (res.success) {
          executeSuccessCallBack?.();
        } else {
          setExecuteSqlResult(res.message);
        }
      })
      .finally(() => {
        setExecuteLoading(false);
      });
  };

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

  return (
    <div className={classnames(styles.executeSQL, className)}>
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
              <div className={styles.errorMessage}>{executeSqlResult}</div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
});
