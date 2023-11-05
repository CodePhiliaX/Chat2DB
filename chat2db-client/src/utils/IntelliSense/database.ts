import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import i18n from '@/i18n';

let intelliSenseDatabase = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: () => {
    return { suggestions: [] };
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

const registerIntelliSenseDatabase = (databaseName: Array<{ name: string; dataSourceName: string }>) => {
  intelliSenseDatabase.dispose();
  intelliSenseDatabase = monaco.languages.registerCompletionItemProvider('sql', {
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
        suggestions: (databaseName || []).map(({ name, dataSourceName }) => ({
          label: {
            label: name,
            detail: dataSourceName ? `(${dataSourceName})` : null,
            description: i18n('sqlEditor.text.databaseName'),
          },
          insertText: name,
          sortText: isTableContext ? '01' : '08',
          kind: monaco.languages.CompletionItemKind.Property,
        })),
      };
    },
  });
};

export { intelliSenseDatabase, registerIntelliSenseDatabase };
