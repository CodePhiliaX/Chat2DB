import { DatabaseTypeCode } from '@/constants';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { addIntelliSenseField } from './field';
import i18n from '@/i18n';

/** 当前库下的表 */
let intelliSenseTable = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

/** 根据不同的数据库，插入不同的表名  */
const handleInsertText = (text: string, databaseCode: DatabaseTypeCode = DatabaseTypeCode.MYSQL) => {
  if (
    [DatabaseTypeCode.POSTGRESQL, DatabaseTypeCode.ORACLE, DatabaseTypeCode.DB2, DatabaseTypeCode.SQLITE].includes(
      databaseCode,
    )
  ) {
    return `\"${text}\"`;
  } else if ([DatabaseTypeCode.SQLSERVER].includes(databaseCode)) {
    return `[${text}]`;
  } else if ([DatabaseTypeCode.MYSQL].includes(databaseCode)) {
    return `\`${text}\``;
  } else {
    return `${text}`;
  }
};

function checkTableContext(text) {
  const normalizedText = text.trim().toUpperCase();
  const tableKeywords = ['FROM', 'JOIN', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'UPDATE'];

  for (const keyword of tableKeywords) {
    if (normalizedText.endsWith(keyword)) {
      return true;
    }
  }

  return false;
}

const registerIntelliSenseTable = (
  tableList: Array<{ name: string; comment: string }>,
  databaseCode?: DatabaseTypeCode,
  dataSourceId?: number,
  databaseName?: string | null,
  schemaName?: string | null,
) => {
  monaco.editor.registerCommand('addFieldList', (_: any, ...args: any[]) => {
    addIntelliSenseField(args[0]);
    return;
  });

  intelliSenseTable.dispose();
  intelliSenseTable = monaco.languages.registerCompletionItemProvider('sql', {
    triggerCharacters: [' '],
    provideCompletionItems: (model, position) => {
      const lineContentUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      const isTableContext = checkTableContext(lineContentUntilPosition);

      return {
        suggestions: (tableList || []).map((tableName) => {
          return {
            label: {
              label: tableName.name,
              detail: databaseName ? `(${databaseName})` : null,
              description: i18n('sqlEditor.text.tableName'),
            },
            kind: monaco.languages.CompletionItemKind.Folder,
            insertText: handleInsertText(tableName.name, databaseCode),
            // range: monaco.Range.fromPositions(position),
            // documentation: tableName.comment,
            sortText: isTableContext ? '01' : '08',
            command: {
              id: 'addFieldList',
              title: 'addFieldList',
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
