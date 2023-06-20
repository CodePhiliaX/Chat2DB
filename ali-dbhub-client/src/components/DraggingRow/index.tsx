import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';


export default memo(function DraggingRow({ children }) {
  return <div className={styles.dragItem}>{children}</div>
})
