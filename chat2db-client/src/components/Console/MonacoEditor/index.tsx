import React, { ForwardedRef, forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';
import cs from 'classnames';
import { useTheme } from '@/hooks';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { language } from 'monaco-editor/esm/vs/basic-languages/sql/sql';
const { keywords: SQLKeys } = language;
import { editorDefaultOptions } from '@/constants/monacoEditor';
import styles from './index.less';
import { ThemeType } from '@/constants/common';
import { monacoSqlAutocomplete } from './syntax-parser/plugin/monaco-plugin';

export type IEditorIns = monaco.editor.IStandaloneCodeEditor;
export type IEditorOptions = monaco.editor.IStandaloneEditorConstructionOptions;
export type IEditorContentChangeEvent = monaco.editor.IModelContentChangedEvent;

interface IProps {
  id: string | number;
  value?: string;
  language?: string;
  className: string;
  onChange?: (v: string, e?: IEditorContentChangeEvent) => void;
  didMount?: (editor: IEditorIns) => any;
  options?: IEditorOptions;
  needDestroy?: boolean;
  addAction?: Array<{ id: string; label: string; action: (selectedText: string) => void }>;
}

export interface IExportRefFunction {
  getCurrentSelectContent: () => string;
  getAllContent: () => string;
}

function MonacoEditor(props: IProps, ref: ForwardedRef<IExportRefFunction>) {
  const { id, className, value = '', language = 'sql', didMount, options } = props;

  const editorRef = useRef<IEditorIns>();
  const [editorVal, setEditorVal] = useState('');

  // 受控暂存value
  const valRef = useRef<string>('');

  const [appTheme] = useTheme();

  useImperativeHandle(ref, () => ({
    getCurrentSelectContent,
    getAllContent,
  }));

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

  // init
  useEffect(() => {
    const editorIns = monaco.editor.create(document.getElementById(`monaco-editor-${id}`)!, {
      ...editorDefaultOptions,
      ...options,
      value,
      language: 'sql',
      theme: appTheme.backgroundColor === ThemeType.Light ? 'Default' : 'BlackTheme',
    });
    editorRef.current = editorIns;
    didMount && didMount(editorIns); // incase parent component wanna handle editor

    monaco.editor.defineTheme('BlackTheme', {
      base: 'vs-dark',
      inherit: true,
      rules: [{ background: '#15161a' }],
      colors: {
        // 相关颜色属性配置
        'editor.foreground': '#ffffff',
        'editor.background': '#0A0B0C', //背景色
      },
    });

    monaco.editor.defineTheme('Default', {
      base: 'vs',
      inherit: true,
      rules: [{ background: '#15161a' }],
      colors: {
        'editor.foreground': '#000000',
        'editor.background': '#fff', //背景色
      },
    });

    // monacoSqlAutocomplete(monaco, editorIns);
    handleRegisterTigger();

    createAction(editorIns);
    return () => {
      if (props.needDestroy) editorRef.current && editorRef.current.dispose();
    };
  }, []);

  // value 变了，直接设置editorValue
  useEffect(() => {
    updateEditor(value);
    valRef.current = value;
  }, [value]);

  // editor 变了，val 没变，设置 editorValue 为 value
  // useEffect(() => {
  //   if (editorVal !== valRef.current) {
  //     updateEditor(valRef.current);
  //   }
  // }, [editorVal]);

  useEffect(() => {
    const _ref = editorRef.current?.onDidChangeModelContent((e) => {
      const curVal = editorRef.current?.getValue();
      props.onChange?.(curVal || '', e);
      setEditorVal(curVal || '');
    });

    return () => _ref && _ref.dispose();
  }, [props.onChange]);

  const updateEditor = (value: string) => {
    if (editorRef.current) {
      if (value === editorRef.current.getValue()) {
        return;
      }
      const model = editorRef.current.getModel();
      if (!model) return;

      editorRef.current.pushUndoStop();

      model.pushEditOperations(
        [],
        [
          {
            range: model.getFullModelRange(),
            text: value,
          },
        ],
        () => [editorRef.current.getSelection()],
      );
      editorRef.current.pushUndoStop();
    }
  };

  useEffect(() => {
    monaco.editor.setTheme(appTheme.backgroundColor === ThemeType.Dark ? 'BlackTheme' : 'Default');
  }, [appTheme.backgroundColor]);

  const handleRegisterTigger = () => {
    // SQL关键词、 数据库、 表 、列

    const hintData = { table1: 'table1', table2: 'table2' };
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
        insertText: key,
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
    monaco.languages.registerCompletionItemProvider('sql', {
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
  return <div ref={ref} id={`monaco-editor-${id}`} className={cs(className, styles.editorContainer)} />;
}

export default forwardRef(MonacoEditor);
