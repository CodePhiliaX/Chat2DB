// 置顶表格
import React from 'react';
import mysqlService from '@/service/sql';
import { v4 as uuid } from 'uuid';
import styles from './viewDDL.less';

import { openModal } from '@/store/common/components';

import MonacoEditor from '@/components/MonacoEditor';

export const viewDDL = (treeNodeData) => {
  const getSql = () => {
    return new Promise((resolve) => {
      mysqlService
        .exportCreateTableSql({
          dataSourceId: treeNodeData.extraParams.dataSourceId,
          databaseName: treeNodeData.extraParams.databaseName,
          schemaName: treeNodeData.extraParams.schemaName,
          name: treeNodeData.name,
        })
        .then((res) => {
          resolve(res);
        });
    });
  };

  openModal({
    title: `DDL-${treeNodeData.name}`,
    width: '60%',
    height: '60%',
    footer: false,
    content: (
      <div className={styles.monacoEditorBox}>
        <MonacoEditorAsync getSql={getSql} />
      </div>
    ),
  });
};

export const MonacoEditorAsync = (params: { getSql: any }) => {
  const { getSql } = params;
  const monacoEditorRef = React.useRef<any>();
  getSql().then((sql) => {
    monacoEditorRef.current.setValue(sql);
  });
  return <MonacoEditor id={uuid()} ref={monacoEditorRef} />;
};
