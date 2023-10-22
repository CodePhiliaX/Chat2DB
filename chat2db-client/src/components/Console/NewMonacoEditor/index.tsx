import React, { useEffect, useState } from 'react';
import MonacoEditor from 'react-monaco-editor';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { language } from 'monaco-editor/esm/vs/basic-languages/sql/sql';
import styles from './index.less';

const { keywords: SQLKeys, builtinFunctions: FunctionKeys } = language;

const getSQLKeywords = () => {
  return SQLKeys.map((key: any) => ({
    label: key,
    kind: monaco.languages.CompletionItemKind.Text,
    insertText: key,
    detail: '关键词',
  }));
};

/** 数据库关键词 */
let providerKeyword = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    const textUntilPosition = model.getValueInRange({
      startLineNumber: position.lineNumber,
      startColumn: 1,
      endLineNumber: position.lineNumber,
      endColumn: position.column,
    });

    // 简化的示例：你可以通过正则或其他方法检查是否输入了表名
    if (textUntilPosition.endsWith('FROM ')) {
      return { suggestions: [] };
    }

    return { suggestions: [...getSQLKeywords()] };
  },
});

/** 当前库下的表 */
let providerTable = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [
        {
          label: 'table1',
          kind: monaco.languages.CompletionItemKind.Function,
          insertText: 'table1',
        },
      ],
    };
  },
});

function SQLEditor({ id, dataSource, database }) {
  const [suggestions, setSuggestions] = useState([]);

  // useEffect(() => {
  //   if (!monaco) return;

  //   return () => {
  //     // 当组件卸载时，注销提示提供者
  //     provider.dispose();
  //   };
  // }, [suggestions]);

  useEffect(() => {
    //
    providerKeyword.dispose();
    providerKeyword = monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems: (model, position) => {
        const textUntilPosition = model.getValueInRange({
          startLineNumber: position.lineNumber,
          startColumn: 1,
          endLineNumber: position.lineNumber,
          endColumn: position.column,
        });

        // 简化的示例：你可以通过正则或其他方法检查是否输入了表名
        if (textUntilPosition.endsWith('FROM ')) {
          return { suggestions: [] };
        }

        if (dataSource === 'MYSQL') {
          return { suggestions: [...getSQLKeywords()] };
        } else {
          return {
            suggestions: [
              {
                label: 'id',
                kind: monaco.languages.CompletionItemKind.Property,
                insertText: 'id',
              },
            ],
          };
        }
      },
    });
    if (dataSource === 'MYSQL') {
      providerTable.dispose();
      providerTable = monaco.languages.registerCompletionItemProvider('sql', {
        provideCompletionItems: () => {
          return {
            suggestions: [
              {
                label: {
                  label: 'table_mysql',
                  detail: '(detail)',
                  description: 'description',
                },
                kind: monaco.languages.CompletionItemKind.Function,
                insertText: 'table_mysql',
              },
            ],
          };
        },
      });
    }
  }, [dataSource]);

  useEffect(() => {
    // 当数据源或数据库变化时，获取相应的表的列提示
    if (dataSource && database) {
      // 模拟 API 调用来获取表的列提示
      // fetch(`/api/columns?source=${dataSource}&database=${database}`)
      //   .then((response) => response.json())
      //   .then((columns) => {
      //     const newSuggestions = columns.map((column) => ({
      //       label: column,
      //       kind: monaco.languages.CompletionItemKind.Property,
      //       insertText: column,
      //     }));
      //     setSuggestions(newSuggestions);
      //   });

      Promise.resolve([
        {
          label: 'id',
          kind: monaco.languages.CompletionItemKind.Property,
          insertText: 'id',
        },
      ]);
    }
  }, [dataSource, database]);

  return (
    <MonacoEditor
      id={id}
      language="sql"
      options={
        {
          // ...其他 Monaco 选项...
        }
      }
    />
  );
}

export default SQLEditor;
