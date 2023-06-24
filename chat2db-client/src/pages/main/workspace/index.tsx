import React, { memo, useEffect, useRef, useState, useReducer, useContext } from 'react';
import styles from './index.less';
import DraggableContainer from '@/components/DraggableContainer';
import WorkspaceLeft from './components/WorkspaceLeft';
import WorkspaceRight from './components/WorkspaceRight';
import { reducer, initState, IState } from './context';

interface IProps {
  className?: string;
}

interface IContext {
  state: IState;
  dispatch: any;
}
export const WorkspaceContext = React.createContext<IContext>({
  state: initState,
  dispatch: () => {},
});

export const useReducerContext = () => {
  return useContext(WorkspaceContext);
};

export default memo<IProps>(function workspace(props) {
  const draggableRef = useRef<any>();
  const [state, dispatch] = useReducer(reducer, initState);

  return (
    <WorkspaceContext.Provider value={{ state, dispatch }}>
      <DraggableContainer className={styles.box}>
        <div ref={draggableRef} className={styles.box_left}>
          <WorkspaceLeft />
        </div>
        <div className={styles.box_right}>
          <WorkspaceRight />
        </div>
      </DraggableContainer>
    </WorkspaceContext.Provider>
  );
});
