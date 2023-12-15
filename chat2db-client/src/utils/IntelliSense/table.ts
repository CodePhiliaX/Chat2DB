import { DatabaseTypeCode } from '@/constants';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { addIntelliSenseField } from './field';
import i18n from '@/i18n';
import { compatibleDataBaseName } from '../database';

/** 当前库下的表 */
let intelliSenseTable = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

const checkTableContext = (text) => {
  const normalizedText = text.trim().toUpperCase();
  const tableKeywords = ['FROM', 'JOIN', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'UPDATE'];

  for (const keyword of tableKeywords) {
    if (normalizedText.endsWith(keyword)) {
      return true;
    }
  }

  return false;
};

const handleInsertText = (keyword: string, tableName: string, databaseCode: DatabaseTypeCode) => {
  if (/^[\"\`\[]/.test(keyword)) {
    return tableName;
  }

  return compatibleDataBaseName(tableName, databaseCode);
};

const registerIntelliSenseTable = (
  tableList: Array<{ name: string; comment: string }>,
  databaseCode: DatabaseTypeCode,
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
    triggerCharacters: [' ', '.'],
    provideCompletionItems: (model, position) => {
      const lineContentUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      const isTableContext = checkTableContext(lineContentUntilPosition);
      // 获取触发提示的字符
      const match = lineContentUntilPosition.match(/\S+$/);
      const word = match ? match[0] : '';

      return {
        suggestions: (tableList || []).map((tableName) => ({
          label: {
            label: tableName.name,
            detail: databaseName ? `(${databaseName})` : null,
            description: i18n('sqlEditor.text.tableName'),
          },
          kind: monaco.languages.CompletionItemKind.Folder,
          insertText: handleInsertText(word, tableName.name, databaseCode),
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
        })),
      };
    },
  });
};

export { intelliSenseTable, registerIntelliSenseTable };
