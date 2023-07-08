import React, { memo, useRef, } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';
import { IConnectionModelType } from '@/models/connection';
import { IWorkspaceModelType } from '@/models/workspace';

interface IProps {
  className?: string;
  workspaceModel: IWorkspaceModelType['state']
  connectionModel: IConnectionModelType['state']
}

const dvaModel = connect(
  ({ connection, workspace }: { connection: IConnectionModelType; workspace: IWorkspaceModelType }) => ({
    connectionModel: connection,
    workspaceModel: workspace,
  }),
);


const workspace = memo<IProps>((props) => {
  const draggableRef = useRef<any>();
  const { workspaceModel, connectionModel } = props;

  return (
    <DraggableContainer className={styles.box}>
      <div ref={draggableRef} className={styles.boxLeft}>
        <WorkspaceLeft />
      </div>
      <div className={styles.boxRight}>
        <WorkspaceRight />
      </div>
    </DraggableContainer>
  );
});

export default dvaModel(workspace)