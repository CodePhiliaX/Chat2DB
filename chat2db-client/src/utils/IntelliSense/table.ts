import { DatabaseTypeCode } from '@/constants';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

/** 当前库下的表 */
let intelliSenseTable = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

const registerIntelliSenseTable = (
  tableList: Array<{ name: string; comment: string }>,
  databaseName?: string,
  databaseCode?: DatabaseTypeCode,
) => {
  intelliSenseTable.dispose();
  intelliSenseTable = monaco.languages.registerCompletionItemProvider('sql', {
    triggerCharacters: [' '],
    provideCompletionItems: (model, position) => {
      const handleInsertText = (text) => {
        if (databaseCode === DatabaseTypeCode.POSTGRESQL) {
          return `${text}`;
        } else {
          return `${text}`;
        }
      };
      return {
        suggestions: (tableList || []).map((tableName) => {
          return {
            label: {
              label: tableName.name,
              detail: databaseName ? `(${databaseName})` : null,
              description: '表名',
            },
            kind: monaco.languages.CompletionItemKind.Variable,
            insertText: handleInsertText(tableName.name),
            // range: monaco.Range.fromPositions(position),
            documentation: tableName.comment,
          };
        }),
      };
    },
  });
};

export { intelliSenseTable, registerIntelliSenseTable };
