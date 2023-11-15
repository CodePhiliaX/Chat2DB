import React, { ForwardedRef, forwardRef, useEffect, useImperativeHandle, useRef } from 'react';
import cs from 'classnames';
import { useTheme } from '@/hooks';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { DatabaseTypeCode, EditorThemeType } from '@/constants';
import { editorDefaultOptions } from './monacoEditorConfig';
import { IQuickInputService } from 'monaco-editor/esm/vs/platform/quickinput/common/quickInput';

import styles from './index.less';

export type IEditorIns = monaco.editor.IStandaloneCodeEditor;
export type IEditorOptions = monaco.editor.IStandaloneEditorConstructionOptions;
export type IEditorContentChangeEvent = monaco.editor.IModelContentChangedEvent;

export type IAppendValue = {
  text: any;
  range?: IRangeType;
};

const databaseTypeList = Object.keys(DatabaseTypeCode).map((d) => ({
  type: d,
  id: d,
  label: d,
}));

interface IProps {
  id: string;
  isActive?: boolean;
  language?: string;
  className?: string;
  options?: IEditorOptions;
  needDestroy?: boolean;
  addAction?: Array<{ id: string; label: string; action: (selectedText: string, ext?: string) => void }>;
  defaultValue?: string;
  appendValue?: IAppendValue;
  // onChange?: (v: string, e?: IEditorContentChangeEvent) => void;
  didMount?: (editor: IEditorIns) => any;
  onSave?: (value: string) => void; // 快捷键保存的回调
  onExecute?: (value: string) => void; // 快捷键执行的回调
}

export interface IExportRefFunction {
  getCurrentSelectContent: () => string;
  getAllContent: () => string;
  setValue: (text: any, range?: IRangeType) => void;
  // toFocus: () => void;
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
  const quickInputCommand = useRef<any>();
  const [appTheme] = useTheme();

  // init
  useEffect(() => {
    const editorIns = monaco.editor.create(document.getElementById(`monaco-editor-${id}`)!, {
      ...editorDefaultOptions,
      ...options,
      value: defaultValue || '',
      language,
      theme: appTheme.backgroundColor,
    });
    editorRef.current = editorIns;
    didMount && didMount(editorIns);

    // Add a new command, for getting an accessor.
    quickInputCommand.current = editorIns.addCommand(0, (accessor, func) => {
      // a hacker way to get the input service
      const quickInputService = accessor.get(IQuickInputService);
      func(quickInputService);
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
      if (props.needDestroy) {
        editorRef.current && editorRef.current.dispose();
      }
    };
  }, []);

  useEffect(() => {
    if (isActive && editorRef.current) {
      // 自定义快捷键
      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
        const value = editorRef.current?.getValue();
        onSave?.(value || '');
      });

      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyR, () => {
        const value = getCurrentSelectContent();
        onExecute?.(value);
      });

      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyR, () => {
        const value = getCurrentSelectContent();
        onExecute?.(value);
      });

      // 注册快捷键command+shift+L新建console
      editorRef.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyL, () => {
        // onShortcutKeyCallback?.(new KeyboardEvent('keydown', { ctrlKey: true, shiftKey: true, keyCode: 76 }));
      });
    }
  }, [editorRef.current, isActive]);

  useEffect(() => {
    // 监听浏览器窗口大小变化，重新渲染编辑器
    const resize = () => {
      editorRef.current?.layout();
    };
    window.addEventListener('resize', resize);
    return () => {
      window.removeEventListener('resize', resize);
    };
  }, []);

  // 设置报表里面的编辑器的主题
  useEffect(() => {
    if (options?.theme) {
      monaco.editor.setTheme(options.theme);
    }
  }, [options?.theme]);

  useImperativeHandle(ref, () => ({
    getCurrentSelectContent,
    getAllContent,
    setValue,
    // toFocus,
  }));

  useEffect(() => {
    if (appendValue) {
      appendMonacoValue(editorRef.current, appendValue?.text, appendValue?.range);
    }
  }, [appendValue]);

  const setValue = (text: any, range?: IRangeType) => {
    appendMonacoValue(editorRef.current, text, range);
  };

  // const toFocus = () => {
  //   editorRef.current?.focus();
  // };

  /**
   * 获取当前选中的内容
   * @returns
   */
  const getCurrentSelectContent = () => {
    const selection = editorRef.current?.getSelection();
    if (!selection || selection.isEmpty()) {
      return '';
    } else {
      const selectedText = editorRef.current?.getModel()?.getValueInRange(selection);
      return selectedText || '';
    }
  };

  /** 获取文本所有内容 */
  const getAllContent = () => {
    const model = editorRef.current?.getModel();
    const value = model?.getValue();
    return value || '';
  };

  const createAction = (editor: IEditorIns) => {
    // 用于控制切换该菜单键的显示
    editor.createContextKey('shouldShowSqlRunnerAction', true);

    if (!props.addAction || !props.addAction.length) {
      return;
    }

    props.addAction.forEach((action) => {
      const { id: _id, label, action: runFn } = action;
      editor.addAction({
        id: _id,
        label,
        // 控制该菜单键显示
        precondition: 'shouldShowSqlRunnerAction',
        // 该菜单键位置
        contextMenuGroupId: 'navigation',
        contextMenuOrder: 1.5,
        // 点击该菜单键后运行
        run: (ed: IEditorIns) => {
          const selectedText = editor.getModel()?.getValueInRange(editor.getSelection()!) || '';
          if (_id === 'changeSQL') {
            ed.trigger('', quickInputCommand.current, (quickInput) => {
              quickInput.pick(databaseTypeList).then((selected) => {
                console.log(selected);
                runFn(selectedText, selected?.label);
              });
            });
          } else {
            runFn(selectedText);
          }
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
  let newText = text;
  const lastLine = editor.getModel().getLineCount();
  const lastLineLength = editor.getModel().getLineMaxColumn(lastLine);

  switch (range) {
    // 覆盖所有内容
    case 'cover':
      newRange = model.getFullModelRange();
      editor.revealLine(lastLine);
      break;
    // 在开头添加内容
    case 'front':
      newRange = new monaco.Range(1, 1, 1, 1);
      editor.revealLine(1);
      editor.setPosition({ lineNumber: 1, column: 1 });
      break;
    // 格式化选中区域的sql
    case 'select': {
      const selection = editor.getSelection();
      if (selection) {
        newRange = new monaco.Range(
          selection.startLineNumber,
          selection.startColumn,
          selection.endLineNumber,
          selection.endColumn,
        );
      }
      break;
    }
    // 在末尾添加内容
    case 'end':
      newRange = new monaco.Range(lastLine, lastLineLength, lastLine, lastLineLength);
      newText = `${text}`;
      break;
    // 在光标处添加内容
    case 'cursor':
      {
        const position = editor.getPosition();
        if (position) {
          newRange = new monaco.Range(position.lineNumber, position.column, position.lineNumber, position.column);
        }
      }
      break;
    default:
      break;
  }

  const op = {
    range: newRange,
    text: newText,
  };

  // decorations?: IModelDeltaDecoration[]: 一个数组类型的参数，用于指定插入的文本的装饰。可以用来设置文本的样式、颜色、背景色等。如果不需要设置装饰，可以忽略此参数。
  const decorations = [{}]; // 解决新增的文本默认背景色为灰色
  editor.executeEdits('setValue', [op], decorations);
  const addedLastLine = editor.getModel().getLineCount();
  // const addedLastLineLength = editor.getModel().getLineMaxColumn(lastLine);

  if (range === 'end') {
    setTimeout(() => {
      editor.revealLine(addedLastLine + 1);
      // editor.setPosition({ lineNumber: addedLastLine, column: addedLastLineLength });
      // editor.focus();
    }, 0);
  }
};

export default forwardRef(MonacoEditor);
