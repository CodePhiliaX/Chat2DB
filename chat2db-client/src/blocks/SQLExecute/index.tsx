import React, { memo, useRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import ConsoleEditor, { IConsoleRef } from '@/components/ConsoleEditor';
import SearchResult, { ISearchResultRef } from '@/components/SearchResult';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
interface IProps {
  boundInfo: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName?: string;
    consoleId: number;
    status: ConsoleStatus;
  };
  initDDL: string;
}

interface IContext {
  boundInfoContext: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName?: string;
    consoleId: number;
    status: ConsoleStatus;
  };
  setBoundInfoContext: (boundInfo: IContext['boundInfoContext']) => void;
}

const SQLExecute = memo<IProps>((props) => {
  const { boundInfo: _boundInfo, initDDL } = props;
  const draggableRef = useRef<any>();
  const searchResultRef = useRef<ISearchResultRef>(null);
  const consoleRef = useRef<IConsoleRef>(null);
  const [boundInfo, setBoundInfo] = useState(_boundInfo)

  useUpdateEffect(() => {
    consoleRef.current?.editorRef?.setValue(initDDL, 'cover');
  }, [initDDL]);

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
            onExecuteSQL={(sql) => {
              searchResultRef.current?.handleExecuteSQL(sql);
            }}
            // isActive={isActive}
            // tables={curTableList || []}
            // remainingUse={aiModel.remainingUse}
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
