import React from 'react';
import { useWorkspaceStore } from '@/pages/main/workspace/store';
import {  GlobalComponents } from '../config';
import ViewDDL from '@/components/ViewDDL';
import styles from './index.less';

const GlobalExtendComponents = () => {
  const { currentWorkspaceGlobalExtend } = useWorkspaceStore((state) => {
    return {
      currentWorkspaceGlobalExtend: state.currentWorkspaceGlobalExtend,
    };
  });

  switch (currentWorkspaceGlobalExtend?.code) {
    case GlobalComponents.view_ddl:
      return  <div className={styles.viewDDLBox}>
      <div className={styles.viewDDLHeader}>{`${currentWorkspaceGlobalExtend.uniqueData.tableName}-DDL`}</div>
      <ViewDDL data={currentWorkspaceGlobalExtend.uniqueData} />;
    </div>
    default:
      return <div className={styles.noInformation}>No information</div>;
  }
};

export default GlobalExtendComponents;
