import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';

let fieldList: Record<string, string[]> = {};

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
    triggerCharacters: [' ', '.'],
    provideCompletionItems: async (model, position) => {
      console.log('registerIntelliSenseField start');
      // 获取到当前行文本
      const textUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      // 查找最后一个单词，这里通过正则获取空格或句号之前的最后一个单词
      const match = textUntilPosition.match(/([\w]+)[\s\.]$/);

      if (!match) {
        return; // 如果没有匹配到，直接返回
      }

      const word = match[1]; // 获取匹配到的单词

      if (word && tableList.includes(word) && !fieldList[word]) {
        console.log('registerIntelliSenseField start word');
        const response = await fetch(
          `/api/rdb/table/column_list?dataSourceId=${dataSourceId}&databaseName=${databaseName}&tableName=${word}`,
          {},
        );
        const data = await response.json();

        fieldList[word] = data.data;
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
