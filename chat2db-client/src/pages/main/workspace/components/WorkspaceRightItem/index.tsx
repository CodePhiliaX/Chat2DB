import React, { memo, useRef, useState, useEffect } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console, { IAppendValue } from '@/components/Console';
import SearchResult from '@/components/SearchResult';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import { IManageResultData } from '@/typings';
import { IWorkspaceModelType } from '@/models/workspace';

interface IProps {
  className?: string;
  isActive: boolean;
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
  data: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName: string;
    consoleId: number;
    consoleName: string;
    initDDL: string;
  };
}

const WorkspaceRightItem = memo<IProps>(function (props) {
  const { className, data, workspaceModel, isActive, dispatch } = props;
  const draggableRef = useRef<any>();
  const [appendValue, setAppendValue] = useState<IAppendValue>({ text: data.initDDL });
  const [resultData, setResultData] = useState<IManageResultData[]>([]);
  const { doubleClickTreeNodeData, curTableList, curWorkspaceParams } = workspaceModel;
  const [showResult, setShowResult] = useState(false);

  useEffect(() => {
    if (!doubleClickTreeNodeData) {
      return;
    }
    const { extraParams } = doubleClickTreeNodeData;
    const { tableName } = extraParams || {};
    const ddl = `SELECT * FROM ${tableName};\n`;
    if (isActive) {
      setAppendValue({ text: ddl });
    }
    dispatch({
      type: 'workspace/setDoubleClickTreeNodeData',
      payload: '',
    });
  }, [doubleClickTreeNodeData]);

  return (
    <div className={classnames(styles.box)}>
      <DraggableContainer layout="column" className={styles.boxRightCenter}>
        <div ref={draggableRef} className={styles.boxRightConsole}>
          <Console
            isActive={isActive}
            appendValue={appendValue}
            executeParams={{ ...data }}
            hasAiChat={true}
            hasAi2Lang={true}
            onExecuteSQL={(res: any) => {
              setResultData(res);
              setShowResult(true);
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
                  })
                }
              });
            }}
            tables={curTableList || []}
          />
        </div>

        <div className={styles.boxRightResult}>{<SearchResult manageResultDataList={resultData} />}</div>
      </DraggableContainer>
    </div>
  );
});

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace,
}));

export default dvaModel(WorkspaceRightItem);
