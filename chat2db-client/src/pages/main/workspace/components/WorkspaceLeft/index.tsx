import React, { memo, useState, useEffect, useRef, useContext, useMemo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Cascader, Divider, Input, Dropdown, Button, Spin } from 'antd';
import Iconfont from '@/components/Iconfont';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { TreeNodeType, ConsoleStatus, ConsoleOpenedStatus, WorkspaceTabType } from '@/constants';
import { IConsole, ITreeNode, ICreateConsole } from '@/typings';
import styles from './index.less';
import historyService from '@/service/history';
import TableList from '../TableList';
import SaveList from '../SaveList';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'],
  dispatch: any;
}

const dvaModel = connect(
  ({ connection, workspace, loading }: { connection: IConnectionModelType; workspace: IWorkspaceModelType, loading: any }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);

const WorkspaceLeft = memo<IProps>(function (props) {
  const { className, workspaceModel, dispatch } = props;
  const { curWorkspaceParams, openConsoleList } = workspaceModel;

  const addConsole = () => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams
    let params = {
      name: `new console${openConsoleList?.length || ''}`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: WorkspaceTabType.CONSOLE,
      tabType: WorkspaceTabType.CONSOLE,
    }

    historyService.saveConsole(params).then(res => {
      dispatch({
        type: 'workspace/setCurConsoleId',
        payload: res,
      });
      dispatch({
        type: 'workspace/setOpenConsoleList',
        payload: [...openConsoleList, { ...params, id: res }],
      })
    })
  }

  // useEffect(() => {
  //   document.addEventListener('keydown', (e) => {
  //     if ((e.ctrlKey || e.metaKey) && e.key === 't') {
  //       e.preventDefault();
  //       addConsole();
  //     }
  //   }, false)
  // }, [])

  return (
    <div className={classnames(styles.box, className)}>
      <SaveList />
      <Divider className={styles.divider} />
      <TableList />
      <div className={styles.createButtonBox}>
        <Button className={styles.createButton} type="primary" onClick={addConsole}>
          <Iconfont code="&#xe63a;" />
          {i18n('common.button.createConsole')}
        </Button>
      </div>
    </div>
  );
});

export default dvaModel(WorkspaceLeft);
