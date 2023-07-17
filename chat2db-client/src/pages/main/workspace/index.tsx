import React, { memo, useRef, useEffect, useMemo, useState } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';
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
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    pageLoading: loading.effects['workspace/fetchDatabaseAndSchemaLoading'] || loading.effects['workspace/fetchGetSavedConsoleLoading'],
  }),
);

interface Option {
  value: string;
  label: string;
  children?: Option[];
}

function handleDatabaseAndSchema(databaseAndSchema: IWorkspaceModelType['state']['databaseAndSchema']) {
  let newCascaderOptions: Option[] = [];
  if (databaseAndSchema?.databases) {
    newCascaderOptions = (databaseAndSchema?.databases || []).map((t) => {
      let schemasList: Option[] = [];
      if (t.schemas) {
        schemasList = t.schemas.map((t) => {
          return {
            value: t.name,
            label: t.name,
          };
        });
      }
      return {
        value: t.name,
        label: t.name,
        next: schemasList,
      };
    });
  } else if (databaseAndSchema?.schemas) {
    newCascaderOptions = (databaseAndSchema?.schemas || []).map((t) => {
      return {
        value: t.name,
        label: t.name,
      };
    });
  }
  return newCascaderOptions;
}

const workspace = memo<IProps>((props) => {
  const draggableRef = useRef<any>();
  const { workspaceModel, connectionModel, dispatch, pageLoading } = props;
  const { curConnection } = connectionModel;
  const { curWorkspaceParams, databaseAndSchema } = workspaceModel;
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (pageLoading === true) {
      setLoading(true);
    } else {
      setLoading(false);
    }
  }, [pageLoading])

  const cascaderOptions = useMemo(() => {
    if (!databaseAndSchema) {
      return
    }
    const res = handleDatabaseAndSchema(databaseAndSchema);
    if (!curWorkspaceParams?.dataSourceId || curWorkspaceParams?.dataSourceId !== curConnection?.id) {
      // 如果databaseAndSchema 发生切变 并且没选中确切的database时，需要默认选中第一个
      const curWorkspaceParams = {
        dataSourceId: curConnection?.id,
        databaseSourceName: curConnection?.alias,
        databaseName: res?.[0]?.value,
        schemaName: res?.[0]?.children?.[0]?.value,
        databaseType: curConnection?.type,
      };
      dispatch({
        type: 'workspace/setCurWorkspaceParams',
        payload: curWorkspaceParams,
      });
    }
    return res;
  }, [databaseAndSchema]);

  useEffect(() => {
    if (curConnection?.id) {
      dispatch({
        type: 'workspace/fetchDatabaseAndSchemaLoading',
        payload: {
          dataSourceId: curConnection.id,
        },
      });
    }
    clearData();
  }, [curConnection]);

  useEffect(() => {
    if (curWorkspaceParams.dataSourceId) {
      getConsoleList();
    }
  }, [curWorkspaceParams]);

  function clearData() {
    dispatch(({
      type: 'workspace/setCurWorkspaceParams',
      payload: {},
    }))
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
    <LoadingContent isLoading={loading}>
      <div className={styles.workspace}>
        <WorkspaceHeader cascaderOptions={cascaderOptions}></WorkspaceHeader>
        <DraggableContainer className={styles.workspaceMain}>
          <div ref={draggableRef} className={styles.boxLeft}>
            <WorkspaceLeft cascaderOptions={cascaderOptions} />
          </div>
          <div className={styles.boxRight}>
            <WorkspaceRight />
          </div>
        </DraggableContainer>
      </div>
    </LoadingContent >
  );
});

export default dvaModel(workspace)