import React, { memo, useRef } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console, { IConsoleRef } from '@/components/Console';
import SearchResult, { ISearchResultRef } from '@/components/SearchResult';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import { IWorkspaceModelState, IWorkspaceModelType } from '@/models/workspace';
import { IAIState } from '@/models/ai';
import { useUpdateEffect } from '@/hooks/useUpdateEffect';
interface IProps {
  className?: string;
  isActive: boolean;
  workspaceModel: IWorkspaceModelState;
  aiModel: IAIState;
  dispatch: any;
  data: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName?: string;
    consoleId: number;
    consoleName: string;
    initDDL: string;
    status?: ConsoleStatus;
  };
}

const SQLExecute = memo<IProps>((props) => {
  const { data, workspaceModel, aiModel, isActive, dispatch } = props;
  const draggableRef = useRef<any>();
  const { curTableList, curWorkspaceParams } = workspaceModel;
  // const [sql, setSql] = useState<string>('');
  const searchResultRef = useRef<ISearchResultRef>(null);
  const consoleRef = useRef<IConsoleRef>(null);

  // useEffect(() => {
  //   if (!doubleClickTreeNodeData) {
  //     return;
  //   }
  //   if (doubleClickTreeNodeData.treeNodeType === TreeNodeType.TABLE) {
  //     const { extraParams } = doubleClickTreeNodeData;
  //     const { tableName } = extraParams || {};
  //     const ddl = `SELECT * FROM ${tableName};\n`;
  //     if (isActive) {
  //       setAppendValue({ text: ddl });
  //     }
  //   }
  //   dispatch({
  //     type: 'workspace/setDoubleClickTreeNodeData',
  //     payload: '',
  //   });
  // }, [doubleClickTreeNodeData]);

  useUpdateEffect(() => {
    consoleRef.current?.editorRef?.setValue(data.initDDL, 'cover');
  }, [data.initDDL]);

  return (
    <div className={classnames(styles.box)}>
      <DraggableContainer layout="column" className={styles.boxRightCenter}>
        <div ref={draggableRef} className={styles.boxRightConsole}>
          <Console
            ref={consoleRef}
            source="workspace"
            defaultValue={data.initDDL}
            isActive={isActive}
            executeParams={{ ...data }}
            hasAiChat={true}
            hasAi2Lang={true}
            onExecuteSQL={(sql) => {
              searchResultRef.current?.handleExecuteSQL(sql);
            }}
            onConsoleSave={() => {
              dispatch({
                type: 'workspace/fetchGetSavedConsole',
                payload: {
                  status: ConsoleStatus.RELEASE,
                  orderByDesc: true,
                  ...curWorkspaceParams,
                },
                callback: (res: any) => {
                  dispatch({
                    type: 'workspace/setConsoleList',
                    payload: res.data,
                  });
                },
              });
            }}
            tables={curTableList || []}
            remainingUse={aiModel.remainingUse}
          />
        </div>
        <div className={styles.boxRightResult}>
          <SearchResult ref={searchResultRef} executeSqlParams={data} />
        </div>
      </DraggableContainer>
    </div>
  );
});

const dvaModel = connect(({ workspace, ai }: { workspace: IWorkspaceModelType; ai: IAIState }) => ({
  workspaceModel: workspace,
  aiModel: ai,
}));

export default dvaModel(SQLExecute);
