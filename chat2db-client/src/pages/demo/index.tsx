import React from 'react';
import styles from './index.less';
import MonacoEditor from '@/components/MonacoEditor';

function Test() {
  return (
    <div className={styles.introduce}>
      <MonacoEditor language='sql' id='121212121' />
    </div>
  );
}

export default Test;
