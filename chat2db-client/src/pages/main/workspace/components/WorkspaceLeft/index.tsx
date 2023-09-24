import React, { memo } from 'react';
import classnames from 'classnames';
import i18n from '@/i18n';
import { connect } from 'umi';
import { Divider, Button } from 'antd';
import Iconfont from '@/components/Iconfont';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import { ConsoleStatus, ConsoleOpenedStatus, WorkspaceTabType } from '@/constants';
import styles from './index.less';
import historyService from '@/service/history';
import TableList from '../TableList';
import SaveList from '../SaveList';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import { IExportParams } from '@/service/sql';
import { downloadFile } from '@/utils/common';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

const dvaModel = connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);

const WorkspaceLeft = memo<IProps>((props) => {
  const { className, workspaceModel, dispatch } = props;
  const { curWorkspaceParams } = workspaceModel;

  const addConsole = () => {
    const { dataSourceId, databaseName, schemaName, databaseType } = curWorkspaceParams;
    const params = {
      name: `new console`,
      ddl: '',
      dataSourceId: dataSourceId!,
      databaseName: databaseName!,
      schemaName: schemaName!,
      type: databaseType,
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: WorkspaceTabType.CONSOLE,
      tabType: WorkspaceTabType.CONSOLE,
    };

    historyService.saveConsole(params).then((res) => {
      dispatch({
        type: 'workspace/setCreateConsoleIntro',
        payload: {
          id: res,
          type: WorkspaceTabType.CONSOLE,
          title: params.name,
          uniqueData: params,
        },
      });
    });
  };

  const handleExportTableStructure = async (exportType: ExportTypeEnum) => {
    const params: IExportParams = {
      ...curWorkspaceParams,
      originalSql: '',
      exportType,
      exportSize: ExportSizeEnum.ALL,
    };
    downloadFile(window._BaseURL + '/api/rdb/doc/export', params);
  };

  return (
    <div className={classnames(styles.box, className)}>
      <SaveList />
      <Divider className={styles.divider} />
      <TableList onExport={handleExportTableStructure} />
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
