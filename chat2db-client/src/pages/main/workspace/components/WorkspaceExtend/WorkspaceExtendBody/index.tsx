import React from 'react';
import styles from './index.less';
import {extendConfig} from '../config';


export default () => {
  return  <div className={styles.WorkspaceExtendBody}>
  {extendConfig[0].components}
  </div>
};
