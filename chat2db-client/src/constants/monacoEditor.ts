import { IEditorOptions } from '@/components/Console/MonacoEditor';

export const editorDefaultOptions: IEditorOptions = {
  fontFamily: `"Menlo", "DejaVu Sans Mono", "Liberation Mono", "Consolas", "Ubuntu Mono", "Courier New", "andale mono", "lucida console", monospace`,
  scrollBeyondLastLine: false,
  automaticLayout: true,
  dragAndDrop: false,
  fontSize: 12,
  tabSize: 2,
  lineHeight: 18,
  theme: 'vscode',
  roundedSelection: false,
  readOnly: false,
  folding: false, // 不显示折叠
  insertSpaces: true,
  autoClosingQuotes: 'always',
  detectIndentation: false,
  wordWrap: 'on',
  fixedOverflowWidgets: true,
  renderLineHighlight: 'none',
  codeLens: false,
  scrollbar: {
    alwaysConsumeMouseWheel: false,
  },
  padding: {
    top: 2,
    bottom: 2,
  },
  minimap: {
    enabled: false,
  },
};
