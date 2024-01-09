import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import MonacoEditor, { IExportRefFunction } from '@/components/MonacoEditor';
import { v4 as uuid } from 'uuid';
import sqlServer from '@/service/sql';

interface IProps {
  className?: string;
  data: any;
}

export default memo<IProps>((props) => {
  const { className, data } = props;
  const [monacoEditorId] = React.useState(uuid());
  const monacoEditorRef = React.useRef<IExportRefFunction>(null);
  const [sql,setSql] = React.useState('');

  useEffect(() => {
    if(data){
      sqlServer
      .exportCreateTableSql({
        ...data,
      } as any)
      .then((res) => {
        setSql(res);
      });
    }
  }, [data]);

  useEffect(() => {
    monacoEditorRef.current?.setValue(sql || '', 'reset');
  }, [sql]);

  return (
    <div className={classnames(styles.viewDDL, className)}>
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
