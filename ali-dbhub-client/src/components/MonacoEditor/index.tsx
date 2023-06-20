import React, { memo, useEffect, useState } from 'react';
import classnames from 'classnames';
import { setLocaleData } from 'monaco-editor-nls';
const monaco = require('monaco-editor/esm/vs/editor/editor.api');
import zh_CN from 'monaco-editor-nls/locale/zh-hans.json';
import { language } from 'monaco-editor/esm/vs/basic-languages/sql/sql';
const { keywords } = language;
import { useTheme } from '@/utils/hooks';

import styles from './index.less';
setLocaleData(zh_CN);

interface IProps {
  id: string | number;
  className?: string;
  height?: number;
  getEditor?: any;
  onSave?: Function;
  onExecute?: Function;
  onChange?: Function;
  [key: string]: any;
  isActive?: boolean;
}

export default memo(function MonacoEditor(props: IProps) {
  const {
    className,
    getEditor,
    id = 0,
    onChange,
    onSave,
    onExecute,
    value,
    isActive,
    ...option
  } = props;
  const [editor, setEditor] = useState<any>();
  const themeColor = useTheme();

  useEffect(() => {
    const editor = monaco.editor.create(
      document.getElementById(`monaco-editor-${id}`)!,
      {
        value: '',
        language: 'sql',
        roundedSelection: false,
        scrollBeyondLastLine: false,
        readOnly: false,
        folding: false, // 不显示折叠
        minimap: {
          enabled: false, // 是否启用预览图
        }, // 预览图设置
        theme:
          localStorage.getItem('theme') == 'default' ? 'default' : 'vs-dark',
        tabSize: 2,
        insertSpaces: true,
        autoClosingQuotes: 'always',
        detectIndentation: false,
        automaticLayout: true,
        wordWrap: 'on',
        fixedOverflowWidgets: true,
        fontSize: 12,
        lineHeight: 18,
        padding: {
          top: 2,
          bottom: 2,
        },
        renderLineHighlight: 'none',
        codeLens: false,
        scrollbar: {
          alwaysConsumeMouseWheel: false,
        },
        ...option,
      },
    );
    setMonacoValue(editor, value, true);

    // Editor onChange
    editor.onDidChangeModelContent(() => {
      onChange && onChange();
    });

    // 自定义菜单 TODO:

    // resize
    window.onresize = function () {
      editor.layout();
    };

    getEditor && getEditor(editor);
    setEditor(editor);
    monaco.editor.defineTheme('BlackTheme', {
      base: 'vs-dark',
      inherit: true,
      rules: [{ background: '#15161a' }],
      colors: {
        // 相关颜色属性配置
        'editor.foreground': '#ffffff',
        'editor.background': '#15161a', //背景色
      },
    });

    monaco.editor.defineTheme('Default1', {
      base: 'vs',
      inherit: true,
      rules: [{ background: '#15161a' }],
      colors: {
        'editor.foreground': '#000000',
        'editor.background': '#f8f8fa', //背景色
      },
    });
  }, []);

  useEffect(() => {
    if (isActive && editor) {
      // 自定义快捷键
      editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
        const value = editor.getValue();
        onSave && onSave(value);
      });

      editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, (event: Event) => {
        onExecute && onExecute();
      });
    }
  }, [editor, isActive])

  useEffect(() => {
    setMonacoValue(editor, value, true);
  }, [value]);

  useEffect(() => {
    monaco.editor.setTheme(themeColor == 'dark' ? 'BlackTheme' : 'Default');
  }, [themeColor]);

  const pushValue = (editor: any, value: any) => {
    const v = value.toString();
    const model = editor?.getModel && editor.getModel(editor);
    model?.setValue && model.setValue(`${model.getValue()}${v}`);
  };

  // 获取编辑器的值
  const getValue = () => {
    if (editor?.getModel) {
      const model = editor.getModel(editor);
      const value = model.getValue();
      return value;
    }
  };

  return (
    <div className={classnames(className, styles.box)}>
      <div id={`monaco-editor-${id}`} className={styles.editorContainer} />
    </div>
  );
});

export interface IHintData {
  [keys: string]: string[];
}

export function setEditorHint(hintData: IHintData) {
  // 获取 SQL 语法提示
  const getSQLSuggest = () => {
    return keywords.map((key: any) => ({
      label: key,
      kind: monaco.languages.CompletionItemKind.Keyword,
      insertText: key,
      detail: '<keywords>',
    }));
  };

  // 获取一级数据
  const getFirstSuggest = () => {
    return Object.keys(hintData).map((key) => ({
      label: key,
      kind: monaco.languages.CompletionItemKind.Method,
      insertText: key,
      detail: '<Database>',
    }));
  };

  // 获取二级数据
  const getSecondSuggest = (keys: string) => {
    const secondNames = hintData[keys];
    if (!secondNames) {
      return [];
    }
    return secondNames?.map((name: any) => ({
      label: name,
      kind: monaco.languages.CompletionItemKind.片段,
      insertText: name,
      detail: '<Table>',
    }));
  };

  // 编辑器提示的提示实例
  const editorHintExamples = monaco.languages.registerCompletionItemProvider(
    'sql',
    {
      triggerCharacters: ['.', ' ', ...keywords],
      provideCompletionItems: (model: any, position: any) => {
        let suggestions: any = [];
        const { lineNumber, column } = position;
        const textBeforePointer = model.getValueInRange({
          startLineNumber: lineNumber,
          startColumn: 0,
          endLineNumber: lineNumber,
          endColumn: column,
        });

        const tokens = textBeforePointer.trim().split(/\s+/);
        const lastToken = tokens[tokens.length - 1]; // 获取最后一段非空字符串

        if (lastToken.endsWith('.')) {
          const tokenNoDot = lastToken.slice(0, lastToken.length - 1);
          suggestions = [...getSecondSuggest(tokenNoDot)];
        } else if (lastToken === '.') {
          suggestions = [];
        } else {
          suggestions = [...getFirstSuggest(), ...getSQLSuggest()];
        }
        return {
          suggestions,
        };
      },
    },
  );

  return editorHintExamples;
}

// editor编辑器实例 
// text需要添加的文本 
// revocable是否覆盖，覆盖以后无法执行撤销操作
export const setMonacoValue = (editor: any, text: any, revocable: boolean = false) => {
  const model = editor?.getModel && editor.getModel(editor);

  // set后是否需要保留撤回记录
  if(revocable){
    if (text !== undefined && text !== null) {
      if (text.constructor === Number) {
        text = text.toString();
      }
    } else {
      text = '';
    }
    model?.setValue && model.setValue(text);
  }else{
    appendMonacoValue(editor, text, 'all');
  }
};


// 向编辑器中追加文本
// editor 编辑器实例
// text 需要添加的文本
// range 添加到的位置
// 'end' 末尾 
// 'front' 开头
// 'cover' 覆盖掉原有的文字
// new monaco.Range 自定义位置 
export const appendMonacoValue =  (editor: any, text: any, range: 'end' | 'front' | 'cover' | any = 'end') => {
  const model = editor?.getModel && editor.getModel(editor);
  // 创建编辑操作，将当前文档内容替换为新内容
  let newRange = range;
  switch(range){
    case 'cover':
      newRange = model.getFullModelRange();
      break;
    case 'front':
      newRange = new monaco.Range(1,1,1,1);
      break;
    case 'end':
      const lastLine = model.getLineCount();
      const lastColumn = model.getLineMaxColumn(lastLine);   
      newRange = new monaco.Range(lastLine, lastColumn, lastLine, lastColumn);
      break;
    default:
      break;
  }
  const op = { 
    range: newRange,
    text, 
    forceMoveMarkers: true 
  };
  // 将编辑操作添加到撤销历史记录中
  editor.executeEdits('setValue', [op]);
}
