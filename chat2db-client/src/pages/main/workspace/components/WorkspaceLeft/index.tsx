import React, { memo } from 'react';
import classnames from 'classnames';
import { connect } from 'umi';
import { Divider } from 'antd';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';
import styles from './index.less';
import TableList from '../TableList';
import SaveList from '../SaveList';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import { IExportParams } from '@/service/sql';
import { downloadFile } from '@/utils/file';

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
  const { className, workspaceModel } = props;
  const { curWorkspaceParams } = workspaceModel;

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
    </div>
  );
});

export default dvaModel(WorkspaceLeft);
