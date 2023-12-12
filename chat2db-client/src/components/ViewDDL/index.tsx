import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import MonacoEditor, { IExportRefFunction } from '@/components/MonacoEditor';
import { v4 as uuid } from 'uuid';

interface IProps {
  className?: string;
  sql: string;
}

export default memo<IProps>((props) => {
  const { className, sql } = props;
  const [monacoEditorId] = React.useState(uuid());
  const monacoEditorRef = React.useRef<IExportRefFunction>(null);

  useEffect(() => {
    monacoEditorRef.current?.setValue(sql, 'reset');
  }, [sql]);

  return (
    <div className={classnames(styles.box, className)}>
      <MonacoEditor
        id={monacoEditorId}
        ref={monacoEditorRef}
        options={{
          lineNumbers: 'off',
          readOnly: true,
          glyphMargin: false,
          folding: false,
        }}
      />
    </div>
  );
});
