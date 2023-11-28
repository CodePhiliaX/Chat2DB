import React from 'react';
import i18n from '@/i18n';
import { Button } from 'antd';
import { IBoundInfo } from '../../index';
import styles from './index.less';
import Iconfont from '@/components/Iconfont';
import SelectBoundInfo from '../SelectBoundInfo';
import { formatSql } from '@/utils/sql';

interface IProps {
  boundInfo: IBoundInfo;
  saveConsole: (sql: string) => void;
  executeSQL: () => void;
  setBoundInfo: (boundInfo: IBoundInfo) => void;
  editorRef: any;
  hasSaveBtn: boolean;
}

const OperationLine = (props: IProps) => {
  const { boundInfo, saveConsole, editorRef, hasSaveBtn, executeSQL, setBoundInfo } = props;

  /**
   * 格式化sql
   */
  const handleSQLFormat = () => {
    let setValueType = 'select';
    let sql = editorRef?.current?.getCurrentSelectContent();
    if (!sql) {
      sql = editorRef?.current?.getAllContent() || '';
      setValueType = 'cover';
    }
    formatSql(sql, boundInfo.databaseType!).then((res) => {
      editorRef?.current?.setValue(res, setValueType);
    });
  };

  return (
    <div className={styles.consoleOptionsWrapper}>
      <div className={styles.consoleOptionsLeft}>
        <Button type="primary" className={styles.runButton} onClick={() => executeSQL()}>
          <Iconfont code="&#xe637;" />
          {i18n('common.button.execute')}
        </Button>
        {hasSaveBtn && (
          <Button
            type="default"
            className={styles.saveButton}
            onClick={() => saveConsole(editorRef?.current?.getAllContent())}
          >
            {i18n('common.button.save')}
          </Button>
        )}
        <Button type="default" onClick={handleSQLFormat}>
          {i18n('common.button.format')}
        </Button>
      </div>
      <SelectBoundInfo setBoundInfo={setBoundInfo} boundInfo={boundInfo} />
    </div>
  );
};

export default OperationLine;
