import React, { memo, useRef, createContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import ConsoleEditor, { IConsoleRef } from '@/components/ConsoleEditor';
import SearchResult, { ISearchResultRef } from '@/components/SearchResult';
import { DatabaseTypeCode } from '@/constants';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
interface IProps {
  boundInfo: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName?: string;
  };
  initDDL: string;
  consoleId: number;
}

interface IContext {
  boundInfo: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName?: string;
  };
  consoleId: number;
}

export const SQLExecuteContext = createContext<IContext>({} as any);

const SQLExecute = memo<IProps>((props) => {
  const { boundInfo, initDDL } = props;
  const draggableRef = useRef<any>();
  const searchResultRef = useRef<ISearchResultRef>(null);
  const consoleRef = useRef<IConsoleRef>(null);

  useUpdateEffect(() => {
    consoleRef.current?.editorRef?.setValue(initDDL, 'cover');
  }, [initDDL]);

  return (
    <SQLExecuteContext.Provider
      value={{
        boundInfo,
        consoleId: props.consoleId,
      }}
    >
      <div className={classnames(styles.sqlExecute)}>
        <DraggableContainer layout="column" className={styles.boxRightCenter}>
          <div ref={draggableRef} className={styles.boxRightConsole}>
            <ConsoleEditor
              ref={consoleRef}
              source="workspace"
              defaultValue={initDDL}
              executeParams={boundInfo}
              hasAiChat={true}
              hasAi2Lang={true}
              onExecuteSQL={(sql) => {
                searchResultRef.current?.handleExecuteSQL(sql);
              }}
              // isActive={isActive}
              // onConsoleSave={() => {
              //   dispatch({
              //     type: 'workspace/fetchGetSavedConsole',
              //     payload: {
              //       status: ConsoleStatus.RELEASE,
              //       orderByDesc: true,
              //       ...curWorkspaceParams,
              //     },
              //     callback: (res: any) => {
              //       dispatch({
              //         type: 'workspace/setConsoleList',
              //         payload: res.data,
              //       });
              //     },
              //   });
              // }}
              // tables={curTableList || []}
              // remainingUse={aiModel.remainingUse}
            />
          </div>
          <div className={styles.boxRightResult}>
            <SearchResult ref={searchResultRef} executeSqlParams={boundInfo} />
          </div>
        </DraggableContainer>
      </div>
    </SQLExecuteContext.Provider>
  );
});

export default SQLExecute;
