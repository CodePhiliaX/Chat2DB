import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import sqlService from '@/service/sql';

let fieldList: Record<string, Array<{ name: string; tableName: string }>> = {};

/** 当前库下的表 */
let intelliSenseField = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

const registerIntelliSenseField = (tableList: string[], dataSourceId, databaseName, schemaName) => {
  intelliSenseField.dispose();
  fieldList = {};
  intelliSenseField = monaco.languages.registerCompletionItemProvider('sql', {
    triggerCharacters: [' ', '.', '`', "'", '"'],
    provideCompletionItems: async (model, position) => {
      // 获取到当前行文本
      const textUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      const match = textUntilPosition.match(/(\b\w+\b)[^\w]*$/);
      let word;
      if (match) {
        word = match[1];
        console.log(word); // 输出: text
      }

      console.log('registerIntelliSenseField start', textUntilPosition, word);
      if (!word) {
        return; // 如果没有匹配到，直接返回
      }
      if (word && tableList.includes(word) && !fieldList[word]) {
        console.log('registerIntelliSenseField start word');
        const data = await sqlService.getAllFieldByTable({
          dataSourceId,
          databaseName,
          schemaName,
          tableName: word,
        });
        fieldList[word] = data;
      }

      const suggestions = Object.keys(fieldList).reduce((acc, cur) => {
        const arr = fieldList[cur].map((fieldObj) => ({
          label: {
            label: fieldObj.name,
            detail: `(${fieldObj.tableName})`,
            description: '字段名',
          },
          kind: monaco.languages.CompletionItemKind.Field,
          insertText: fieldObj.name,
        }));

        return [...acc, ...arr];
      }, []);
      console.log('field suggestions', suggestions);

      return {
        suggestions,
      };
    },
  });
};

export { intelliSenseField, registerIntelliSenseField };
