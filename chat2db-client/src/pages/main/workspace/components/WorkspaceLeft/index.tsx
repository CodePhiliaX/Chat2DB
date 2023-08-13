import React, { memo, useState, useEffect, useRef, useContext, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Cascader, Divider, Input, Dropdown, Button, Spin } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import historyServer from '@/service/history';
import Tree from '../Tree';
import { TreeNodeType, ConsoleStatus, ConsoleOpenedStatus } from '@/constants';
import { IConsole, ITreeNode, ICreateConsole } from '@/typings';
import styles from './index.less';
import { approximateTreeNode, approximateList } from '@/utils';
import historyService from '@/service/history';
import TableList from '../TableList';
import SaveList from '../SaveList';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'],
  dispatch: any;
  tableLoading: boolean;
  databaseLoading: boolean;
}

const dvaModel = connect(
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
    tableLoading: loading.effects['workspace/fetchGetCurTableList'],
    databaseLoading: loading.effects['workspace/fetchDatabaseAndSchema'],
  }),
);

const WorkspaceLeft = memo<IProps>(function (props) {
  const { className, workspaceModel, dispatch } = props;
  const { curWorkspaceParams, openConsoleList } = workspaceModel;

  function getConsoleList() {
    let p: any = {
      pageNo: 1,
      pageSize: 999,
      orderByDesc: false,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      ...curWorkspaceParams,
    };

    dispatch({
      type: 'workspace/fetchGetSavedConsole',
      payload: p,
      callback: (res: any) => {
        dispatch({
          type: 'workspace/setOpenConsoleList',
          payload: res.data,
        })
      }
    })
  }

  const addConsole = (params?: ICreateConsole) => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams
    let p = {
      name: `new console${openConsoleList?.length || ''}`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
    }
    historyService.saveConsole(params || p).then(res => {
      dispatch({
        type: 'workspace/setCurConsoleId',
        payload: res,
      });
      getConsoleList();
    })
  }

  function createConsole() {
    addConsole();
  }

  useEffect(() => {
    document.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 't') {
        e.preventDefault();
        addConsole();
      }
    }, false)
  }, [])

  return (
    <div className={classnames(styles.box, className)}>
      <SaveList />
      <Divider className={styles.divider} />
      <TableList />
      <div className={styles.createButtonBox}>
        <Button className={styles.createButton} type="primary" onClick={createConsole}>
          <Iconfont code="&#xe63a;" />
          {i18n('common.button.createConsole')}
        </Button>
      </div>
    </div>
  );
});

export default dvaModel(WorkspaceLeft);
