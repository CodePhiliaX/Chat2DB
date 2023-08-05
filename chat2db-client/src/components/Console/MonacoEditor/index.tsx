import React, { ForwardedRef, forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import cs from 'classnames';
import { useTheme } from '@/hooks';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { language } from 'monaco-editor/esm/vs/basic-languages/sql/sql';
const { keywords: SQLKeys } = language;
const { keywords } = language;
import { editorDefaultOptions, EditorThemeType, ThemeType } from '@/constants';
import styles from './index.less';
import { useUpdateEffect } from '@/hooks';
export type IEditorIns = monaco.editor.IStandaloneCodeEditor;
export type IEditorOptions = monaco.editor.IStandaloneEditorConstructionOptions;
export type IEditorContentChangeEvent = monaco.editor.IModelContentChangedEvent;

export type IAppendValue = {
  text: any;
  range?: IRangeType;
};
interface IProps {
  id: string | number;
  isActive?: boolean;
  language?: string;
  className?: string;
  options?: IEditorOptions;
  needDestroy?: boolean;
  addAction?: Array<{ id: string; label: string; action: (selectedText: string) => void }>;
  appendValue?: IAppendValue;
  // onChange?: (v: string, e?: IEditorContentChangeEvent) => void;
  didMount?: (editor: IEditorIns) => any;
  onSave?: (value: string) => void; // 快捷键保存的回调
  defaultValue?: string;
  onExecute?: (value: string) => void; // 快捷键执行的回调
  tables?: any[];
}

export interface IExportRefFunction {
  getCurrentSelectContent: () => string;
  getAllContent: () => string;
  setValue: (text: any, range?: IRangeType) => void;
  handleRegisterTigger: (hintData: IHintData) => void;
}

export interface IHintData {
  [keys: string]: string[];
}

function MonacoEditor(props: IProps, ref: ForwardedRef<IExportRefFunction>) {
  const {
    id,
    className,
    language = 'sql',
    didMount,
    options,
    isActive,
    onSave,
    onExecute,
    defaultValue,
    appendValue,
  } = props;
  const editorRef = useRef<IEditorIns>();
  const [appTheme] = useTheme();

  // init
  useEffect(() => {
    const editorIns = monaco.editor.create(document.getElementById(`monaco-editor-${id}`)!, {
      ...editorDefaultOptions,
      ...options,
      value: defaultValue || '',
      language: language,
      theme: appTheme.backgroundColor === ThemeType.Light ? EditorThemeType.Default : EditorThemeType.BlackTheme,
    });
    editorRef.current = editorIns;
    didMount && didMount(editorIns);

    monaco.editor.defineTheme(EditorThemeType.Default, {
      base: 'vs',
      inherit: true,
      rules: [{ background: '#15161a' }] as any,
      colors: {
        'editor.foreground': '#000000',
        'editor.background': '#fff', //背景色
      },
    });

    monaco.editor.defineTheme(EditorThemeType.BlackTheme, {
      base: 'vs-dark',
      inherit: true,
      rules: [{ background: '#15161a' }] as any,
      colors: {
        // 相关颜色属性配置
        'editor.foreground': '#ffffff',
        'editor.background': '#0A0B0C', //背景色
      },
    });

    monaco.editor.defineTheme(EditorThemeType.DashboardLightTheme, {
      base: 'vs',
      inherit: true,
      rules: [{ background: '#15161a' }] as any,
      colors: {
        'editor.foreground': '#000000',
        'editor.background': '#f8f9fa', //背景色
      },
    });

    monaco.editor.defineTheme(EditorThemeType.DashboardBlackTheme, {
      base: 'vs-dark',
      inherit: true,
      rules: [{ background: '#15161a' }] as any,
      colors: {
        'editor.foreground': '#ffffff',
        'editor.background': '#131418', //背景色
      },
    });

    createAction(editorIns);
    return () => {
      if (props.needDestroy) editorRef.current && editorRef.current.dispose();
    };
  }, []);

  useEffect(() => {
    if (isActive && editorRef.current) {
      // 自定义快捷键
      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
        const value = editorRef.current?.getValue();
        onSave?.(value || '');
      });

      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, (event: Event) => {
        const value = getCurrentSelectContent();
        onExecute?.(value);
      });
    }
  }, [editorRef.current, isActive]);

  // 监听主题色变化切换编辑器主题色
  useEffect(() => {
    const isDark = appTheme.backgroundColor === ThemeType.Dark;
    if (options?.theme) {
      monaco.editor.setTheme(options.theme);
    } else {
      monaco.editor.setTheme(isDark ? 'BlackTheme' : 'Default');
    }
  }, [appTheme.backgroundColor, options?.theme]);

  // useEffect(() => {
  //   const _ref = editorRef.current?.onDidChangeModelContent((e) => {
  //     const curVal = editorRef.current?.getValue();
  //     props.onChange?.(curVal || '', e);
  //   });
  //   return () => _ref && _ref.dispose();
  // }, [props.onChange]);

  useImperativeHandle(ref, () => ({
    handleRegisterTigger,
    getCurrentSelectContent,
    getAllContent,
    setValue,
  }));

  useEffect(() => {
    if (appendValue) {
      appendMonacoValue(editorRef.current, appendValue?.text, appendValue?.range);
    }
  }, [appendValue]);

  const setValue = (text: any, range?: IRangeType) => {
    appendMonacoValue(editorRef.current, text, range);
  };

  /**
   * 获取当前选中的内容
   * @returns
   */
  const getCurrentSelectContent = () => {
    let selection = editorRef.current?.getSelection();
    if (!selection || selection.isEmpty()) {
      return '';
    } else {
      var selectedText = editorRef.current?.getModel()?.getValueInRange(selection);
      return selectedText || '';
    }
  };

  /** 获取文本所有内容 */
  const getAllContent = () => {
    const model = editorRef.current?.getModel();
    const value = model?.getValue();
    return value || '';
  };

  const handleRegisterTigger = (hintData: IHintData) => {
    // 获取 SQL 语法提示
    const getSQLSuggest = () => {
      return SQLKeys.map((key: any) => ({
        label: key,
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: key,
        detail: '<Keywords>',
      }));
    };

    // 获取一级数据
    const getFirstSuggest = () => {
      return Object.keys(hintData).map((key) => ({
        label: key,
        kind: monaco.languages.CompletionItemKind.Method,
        insertText: `\`${key}\``,
        detail: '<Database>',
      }));
    };

    // 获取二级数据
    // const getSecondSuggest = (keys: string) => {
    //   const secondNames = hintData[keys];
    //   if (!secondNames) {
    //     return [];
    //   }
    //   return (secondNames || []).map((name: any) => ({
    //     label: name,
    //     kind: monaco.languages.CompletionItemKind.Snippet,
    //     insertText: name,
    //     detail: '<Table>',
    //   }));
    // };
    const editorHintExamples = monaco.languages.registerCompletionItemProvider('sql', {
      triggerCharacters: [' ', ...SQLKeys],
      provideCompletionItems: (model: monaco.editor.ITextModel, position: monaco.Position) => {
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
          // suggestions = [...getSecondSuggest(tokenNoDot)];
          suggestions = [];
        } else if (lastToken === '.') {
          suggestions = [];
        } else {
          suggestions = [...getFirstSuggest(), ...getSQLSuggest()];
        }
        return {
          suggestions,
        };
      },
    });

    return editorHintExamples;
  };

  const createAction = (editor: IEditorIns) => {
    // 用于控制切换该菜单键的显示
    const shouldShowSqlRunnerAction = editor.createContextKey('shouldShowSqlRunnerAction', true);

    if (!props.addAction || !props.addAction.length) {
      return;
    }

    props.addAction.forEach((action) => {
      const { id, label, action: runFn } = action;
      editor.addAction({
        id,
        label,
        // 控制该菜单键显示
        precondition: 'shouldShowSqlRunnerAction',
        // 该菜单键位置
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        // 点击该菜单键后运行
        run: (event: monaco.editor.ICodeEditor) => {
          const selectedText = editor.getModel()?.getValueInRange(editor.getSelection()!) || '';
          runFn(selectedText);
        },
      });
    });
  };

  return <div ref={ref as any} id={`monaco-editor-${id}`} className={cs(className, styles.editorContainer)} />;
}

// text 需要添加的文本
// range 添加到的位置
// 'end' 末尾
// 'front' 开头
// 'cover' 覆盖掉原有的文字
// 自定义位置数组 new monaco.Range []
export type IRangeType = 'end' | 'front' | 'cover' | 'reset' | any;

export const appendMonacoValue = (editor: any, text: any, range: IRangeType = 'end') => {
  if (!editor) {
    return;
  }
  const model = editor?.getModel && editor.getModel(editor);
  // 创建编辑操作，将当前文档内容替换为新内容
  let newRange: IRangeType = range;
  if (range === 'reset') {
    editor.setValue(text);
    return;
  }
  switch (range) {
    case 'cover':
      newRange = model.getFullModelRange();
      break;
    case 'front':
      newRange = new monaco.Range(1, 1, 1, 1);
      break;
    case 'end':
      const lastLine = editor.getModel().getLineCount();
      const lastLineLength = editor.getModel().getLineMaxColumn(lastLine);
      newRange = new monaco.Range(lastLine, lastLineLength, lastLine, lastLineLength);
      text = `${text}`;
      break;
    default:
      break;
  }
  const op = {
    range: newRange,
    text,
  };
  // decorations?: IModelDeltaDecoration[]: 一个数组类型的参数，用于指定插入的文本的装饰。可以用来设置文本的样式、颜色、背景色等。如果不需要设置装饰，可以忽略此参数。
  const decorations = [{}]; // 解决新增的文本默认背景色为灰色
  editor.executeEdits('setValue', [op], decorations);
};

export default forwardRef(MonacoEditor);
