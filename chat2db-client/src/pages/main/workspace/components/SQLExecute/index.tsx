import React, { memo, useEffect, useRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import ConsoleEditor, { IConsoleRef } from '@/components/ConsoleEditor';
import SearchResult, { ISearchResultRef } from '@/components/SearchResult';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

interface IProps {
  boundInfo: {
    dataSourceId: number;
    dataSourceName: string;
    databaseType: DatabaseTypeCode;
    databaseName?: string;
    schemaName?: string;
    status: ConsoleStatus;
    consoleId: number;
  };
  initDDL: string;
  // 异步加载sql
  loadSQL: () => Promise<string>;
}

const SQLExecute = memo<IProps>((props) => {
  const { boundInfo: _boundInfo, initDDL, loadSQL } = props;
  const draggableRef = useRef<any>();
  const searchResultRef = useRef<ISearchResultRef>(null);
  const consoleRef = useRef<IConsoleRef>(null);
  const [boundInfo, setBoundInfo] = useState(_boundInfo);
  const activeConsoleId = useWorkspaceStore((state) => state.activeConsoleId);

  useEffect(() => {
    if (loadSQL) {
      loadSQL().then((sql) => {
        consoleRef.current?.editorRef?.setValue(sql, 'cover');
      });
    }
  }, []);

  return (
    <div className={classnames(styles.sqlExecute)}>
      <DraggableContainer layout="column" className={styles.boxRightCenter}>
        <div ref={draggableRef} className={styles.boxRightConsole}>
          <ConsoleEditor
            ref={consoleRef}
            source="workspace"
            defaultValue={initDDL}
            boundInfo={boundInfo}
            setBoundInfo={setBoundInfo}
            hasAiChat={true}
            hasAi2Lang={true}
            isActive={activeConsoleId === boundInfo.consoleId}
            onExecuteSQL={(sql) => {
              searchResultRef.current?.handleExecuteSQL(sql);
            }}
          />
        </div>
        <div className={styles.boxRightResult}>
          <SearchResult ref={searchResultRef} executeSqlParams={boundInfo} />
        </div>
      </DraggableContainer>
    </div>
  );
});

export default SQLExecute;
