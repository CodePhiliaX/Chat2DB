import React, { memo, useRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console from '@/components/Console';

interface IProps {
  className?: string;
}

export default memo<IProps>(function WorkspaceRight(props) {
  const { className } = props;
  const draggableRef = useRef<any>();
  return <div className={classnames(styles.box, className)}>
    <DraggableContainer layout="column" className={styles.box_right_center}>
      <div ref={draggableRef} className={styles.box_right_console}>
        <Console hasAiChat={true}  hasAi2Lang={true}/>
      </div>
      <div className={styles.box_right_result}>
        <p>Result</p>
      </div>
    </DraggableContainer>
  </div>
})
