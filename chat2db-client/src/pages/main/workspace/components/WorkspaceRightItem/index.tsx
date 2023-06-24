import React, { memo, useRef,useState, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console from '@/components/Console';
import LoadingContent from '@/components/Loading/LoadingContent';
import SearchResult from '@/components/SearchResult';
import {DatabaseTypeCode} from '@/constants/common';
import { IManageResultData } from '@/typings/database';
import { useReducerContext } from '@/pages/main/workspace';

interface IProps {
  className?: string;
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

export default memo<IProps>(function WorkspaceRightItem(props) {
  const { className, data } = props;
  const draggableRef = useRef<any>();
  const [consoleValue, setConsoleValue] = useState<string>(data.initDDL || '');
  const [resultData, setResultData] = useState<IManageResultData[]>([]);
  const { state, dispatch } = useReducerContext();
  const { dblclickTreeNodeData, currentWorkspaceData } = state;


  useEffect(() => {
    if(!dblclickTreeNodeData){
      return
    }
    const { extraParams } = dblclickTreeNodeData;
    const { databaseName, schemaName, dataSourceId, dataSourceName, databaseType, tableName } = extraParams || {};
    const ddl = `SELECT * FROM ${tableName};`;

    if (data.databaseName === databaseName && data.dataSourceId === dataSourceId) {
      setConsoleValue(`${consoleValue}\n${ddl}`)
    }
  }, [dblclickTreeNodeData]);

  return <div className={classnames(styles.box)}>
    <DraggableContainer layout="column" className={styles.boxRightCenter}>
      <div ref={draggableRef} className={styles.boxRightConsole}>
        <Console
          executeParams={{...data}}
          hasAiChat={true}
          hasAi2Lang={true}
          value={consoleValue}
          onChangeValue={
            (value:string)=>{
              setConsoleValue(value)
            }
          }
          onExecuteSQL={(result) => {
            setResultData(result);
          }}
        />
      </div>
      <div className={styles.boxRightResult}>
        <LoadingContent data={resultData} handleEmpty>
          <SearchResult manageResultDataList={resultData} />
        </LoadingContent>
      </div>
    </DraggableContainer>
  </div>
})
