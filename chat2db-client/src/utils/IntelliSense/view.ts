import { DatabaseTypeCode } from '@/constants';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import i18n from '@/i18n';

/** 当前库下的表 */
let intelliSenseView = monaco.languages.registerCompletionItemProvider('sql', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [],
    };
  },
});

const checkViewContext = (text) => {
  const normalizedText = text.trim().toUpperCase();
  const tableKeywords = ['FROM', 'JOIN', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'UPDATE'];

  for (const keyword of tableKeywords) {
    if (normalizedText.endsWith(keyword)) {
      return true;
    }
  }

  return false;
};

const registerIntelliSenseView = (
  viewList: string[],
  databaseName?: string | null,
) => {
  intelliSenseView.dispose();
  intelliSenseView = monaco.languages.registerCompletionItemProvider('sql', {
    triggerCharacters: [' '],
    provideCompletionItems: (model, position) => {
      const lineContentUntilPosition = model.getValueInRange({
        startLineNumber: position.lineNumber,
        startColumn: 1,
        endLineNumber: position.lineNumber,
        endColumn: position.column,
      });

      const isViewContext = checkViewContext(lineContentUntilPosition);

      return {
        suggestions: (viewList || []).map((viewName) => {
          return {
            label: {
              label: viewName,
              detail: databaseName ? `(${databaseName})` : null,
              description: i18n('sqlEditor.text.viewName'),
            },
            kind: monaco.languages.CompletionItemKind.Unit,
            insertText: viewName,
            // range: monaco.Range.fromPositions(position),
            // documentation: tableName.comment,
            sortText: isViewContext ? '01' : '08'
          };
        }),
      };
    },
  });
};

export { intelliSenseView, registerIntelliSenseView };
