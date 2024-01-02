import React, { memo, useEffect, useContext } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import Iconfont from '@/components/Iconfont';
import SingleFileMonacoEditor from '@/components/SingleFileMonacoEditor';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { IExecuteSqlParams } from '@/service/sql';
import { Context } from '@/components/SearchResult';

interface IProps {
  className?: string;
  promptWord: any[];
  getTableData: (params?: Partial<IExecuteSqlParams>) => void;
}

const keywordHintList = [
  'AND',
  'OR',
  'NOT',
  'IS',
  'NULL',
  'IN',
  'IS NOT NULL',
  'IS NULL',
  'IS NOT',
  'NOT IN',
  'EXISTS',
  'BETWEEN',
  'LIKE',
  'ASC',
  'DESC',
]

export default memo<IProps>((props) => {
  const { promptWord, getTableData } = props;
  const { notChangedSql } = useContext(Context);
  const [isActive, setIsActive] = React.useState(false);
  const keywordHintRef = React.useRef<any>(null);
  const fieldHintRef = React.useRef<any>(null);
  const whereSingleFileMonacoEditorRef = React.useRef<any>(null);
  const orderBySingleFileMonacoEditorRef = React.useRef<any>(null);

  useEffect(() => {
    keywordHintRef.current && keywordHintRef.current.dispose();
    fieldHintRef.current && fieldHintRef.current.dispose();
    if (isActive) {
      registerPromptWord();
    }
  }, [promptWord, isActive]);

  const registerPromptWord = () => {
    const suggestions = promptWord.slice(1).map((item) => {
      return {
        insertText: item.name,
        kind: monaco.languages.CompletionItemKind.Field,
        label: item.name,
      };
    });

    const provideCompletionItems: any = () => {
      return {
        suggestions,
      };
    };

    fieldHintRef.current = monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems,
      triggerCharacters: [],
    });

    keywordHintRef.current = monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems: () => {
        return {
          suggestions: keywordHintList.map((item) => {
            return {
              insertText: item,
              kind: monaco.languages.CompletionItemKind.Keyword,
              label: item,
            };
          }),
        };
      },
      triggerCharacters: [],
    });
  };

  const search = () => {
    const whereValue = whereSingleFileMonacoEditorRef.current?.getAllContent().trim() || '';
    const orderByValue = orderBySingleFileMonacoEditorRef.current?.getAllContent().trim() || '';
    let sql = whereValue ? notChangedSql + ' WHERE ' + whereValue : notChangedSql;
    sql = orderByValue ? sql + ' ORDER BY ' + orderByValue : sql;
    getTableData({ sql });
  };

  const focusChange = (_isActive: boolean) => {
    setIsActive(_isActive);
  };

  return (
    <div className={styles.screeningResult}>
      <div className={styles.whereBox}>
        <div className={styles.titleBox}>
          <Iconfont box boxSize={20} classNameBox={styles.titleIcon} code="&#xe66a;" />
          <div
            className={classnames(styles.title, {
              [styles.activeTitle]: true,
            })}
          >
            WHERE
          </div>
        </div>
        <SingleFileMonacoEditor
          ref={whereSingleFileMonacoEditorRef}
          focusChange={focusChange}
          handelEnter={search}
          className={styles.monacoEditor}
        />
      </div>
      <div className={styles.orderByBox}>
        <div className={styles.titleBox}>
          <Iconfont box boxSize={20} classNameBox={styles.titleIcon} code="&#xe69a;" />
          <div
            className={classnames(styles.title, {
              [styles.activeTitle]: true,
            })}
          >
            ORDER BY
          </div>
        </div>
        <SingleFileMonacoEditor
          ref={orderBySingleFileMonacoEditorRef}
          focusChange={focusChange}
          handelEnter={search}
          className={styles.monacoEditor}
        />
      </div>
    </div>
  );
});
