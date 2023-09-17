import React, { memo, useRef, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRightNew from './components/WorkspaceRightNew';
import WorkspaceHeader from './components/WorkspaceHeader';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import LoadingContent from '@/components/Loading/LoadingContent';
import { ConsoleOpenedStatus } from '@/constants';
import Iconfont from '@/components/Iconfont';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'];
  connectionModel: IConnectionModelType['state'];
  pageLoading: any;
  dispatch: any;
}

const dvaModel = connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);

const workspacePage = memo<IProps>((props) => {
  const draggableRef = useRef<any>();
  const { workspaceModel, connectionModel, dispatch } = props;
  const { curConnection } = connectionModel;
  const { curWorkspaceParams } = workspaceModel;
  const isReady = curWorkspaceParams?.dataSourceId && ((curWorkspaceParams?.databaseName || curWorkspaceParams?.schemaName) || (curWorkspaceParams?.databaseName === null && curWorkspaceParams?.schemaName == null))

  useEffect(() => {
    clearData();
  }, [curConnection]);

  useEffect(() => {
    if (isReady) {
      getConsoleList();
    }
  }, [curWorkspaceParams]);

  function clearData() {
    dispatch(({
      type: 'workspace/setOpenConsoleList',
      payload: [],
    }))
    dispatch(({
      type: 'workspace/setConsoleList',
      payload: [],
    }))
    dispatch(({
      type: 'workspace/setDatabaseAndSchema',
      payload: undefined,
    }))
    dispatch(({
      type: 'workspace/setCurTableList',
      payload: [],
    }))
  }

  function getConsoleList() {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    dispatch({
      type: 'workspace/fetchGetSavedConsoleLoading',
      payload: p,
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setOpenConsoleList',
          payload: res.data,
        });
      },
    });
  }

  return (
    <div className={styles.workspace}>
      <WorkspaceHeader></WorkspaceHeader>
      <LoadingContent className={styles.loadingContent} coverLoading={true} isLoading={false}>
        <DraggableContainer className={styles.workspaceMain}>
          <div ref={draggableRef} className={styles.boxLeft}>
            <WorkspaceLeft />
          </div>
          <div className={styles.boxRight}>
            <WorkspaceRightNew />
          </div>
        </DraggableContainer>
      </LoadingContent >
    </div>
  );
});

export default dvaModel(workspacePage)