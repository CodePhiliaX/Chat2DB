import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import sqlService from '@/service/sql';
import i18n from '@/i18n';

let fieldList: Record<string, Array<{ name: string; tableName: string }>> = {};

/** 当前库下的表 */
let intelliSenseField = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

const addIntelliSenseField = async (props: {
  tableName: string;
  dataSourceId: number;
  databaseName: string;
  schemaName?: string;
}) => {
  const { tableName, dataSourceId, databaseName, schemaName } = props;

  if (!fieldList[tableName]) {
    const data = await sqlService.getAllFieldByTable({
      dataSourceId,
      databaseName,
      schemaName,
      tableName,
    });
    fieldList[tableName] = data;
  }
};

function checkFieldContext(text) {
  const normalizedText = text.trim().toUpperCase();
  const columnKeywords = ['SELECT', 'WHERE', 'AND', 'OR', 'GROUP BY', 'ORDER BY', 'SET'];

  for (const keyword of columnKeywords) {
    if (normalizedText.endsWith(keyword)) {
      return true;
    }
  }

  return false;
}

const registerIntelliSenseField = (tableList: string[], dataSourceId, databaseName, schemaName) => {
  intelliSenseField.dispose();
  fieldList = {};
  intelliSenseField = monaco.languages.registerCompletionItemProvider('sql', {
    triggerCharacters: [' ', ',', '.', '('],
    provideCompletionItems: async (model, position) => {
      // 获取到当前行文本
      const textUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      const isFieldContext = checkFieldContext(textUntilPosition);
      const match = textUntilPosition.match(/(\b\w+\b)[^\w]*$/);

      let word;
      if (match) {
        word = match[1];
      }

      if (!word) {
        return; // 如果没有匹配到，直接返回
      }
      if (word && tableList.includes(word) && !fieldList[word]) {
        const data = await sqlService.getAllFieldByTable({
          dataSourceId,
          databaseName,
          schemaName,
          tableName: word,
        });
        fieldList[word] = data;
      }

      const suggestions: monaco.languages.CompletionItem[] = Object.keys(fieldList).reduce((acc, cur) => {
        const arr = fieldList[cur].map((fieldObj) => ({
          label: {
            label: fieldObj.name,
            detail: `(${fieldObj.tableName})`,
            description: i18n('sqlEditor.text.fieldName'),
          },
          kind: monaco.languages.CompletionItemKind.Field,
          insertText: fieldObj.name,
          sortText: isFieldContext ? '01' : '08',
        }));

        return [...acc, ...arr];
      }, []);

      return {
        suggestions,
      };
    },
  });
};

export { intelliSenseField, registerIntelliSenseField, addIntelliSenseField };
