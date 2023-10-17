import MonacoEditor from '@/components/Console/MonacoEditor';
import { Button } from 'antd';
import React, { useState } from 'react';
import styles from './index.less';

function Test() {
  const [value, setValue] = useState('select * from ');

  return (
    <>
      <Button onClick={() => setValue('select * from table1')}>Change1</Button>
      <Button onClick={() => setValue('select * from table2')}>Change2</Button>

      <MonacoEditor
        id={'0'}
        className={styles.container}
        language={'sql'}
        // value={value}
        onChange={(v, e) => {
          setValue(v);
        }}
      />
    </>
  );
}

export default Test;
