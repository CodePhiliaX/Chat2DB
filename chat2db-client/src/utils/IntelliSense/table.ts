import { DatabaseTypeCode } from '@/constants';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { addIntelliSenseField } from './field';

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
  databaseCode?: DatabaseTypeCode,
  dataSourceId?: number,
  databaseName?: string,
  schemaName?: string,
) => {
  monaco.editor.registerCommand('myCustomCommand', (_: any, ...args: any[]) => {
    // access the arguments here
    console.log('trigger suggest', args[0]);
    // addIntelliSenseField(tableName)
    addIntelliSenseField(args[0]);
    return;
  });

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
            command: {
              id: 'myCustomCommand',
              title: 'operator_additional_suggestions',
              arguments: [
                {
                  tableName: tableName.name,
                  dataSourceId,
                  databaseName,
                  schemaName,
                },
              ],
            },
          };
        }),
      };
    },
  });
};

export { intelliSenseTable, registerIntelliSenseTable };
