import React, { memo, useEffect, useRef, useState, useReducer, useContext } from 'react';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';

interface IProps {
  className?: string;
}

export default memo<IProps>(function workspace(props) {
  const draggableRef = useRef<any>();

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
