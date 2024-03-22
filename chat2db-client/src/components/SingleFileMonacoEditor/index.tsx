import React, { memo, useCallback, useMemo, ForwardedRef, forwardRef, useImperativeHandle, useRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import MonacoEditor, { IExportRefFunction } from '@/components/MonacoEditor';
import { v4 as uuid } from 'uuid';

interface IProps {
  className?: string;
  handelEnter?: (value: string) => void;
  focusChange?: (isActive: boolean) => void;
  ref: any; // ref不是写在这里吧？？？
}

export interface ISingleFileMonacoEditorRefFunction {
  getAllContent?: () => string;
}

const options = {
  lineNumbers: false,
  renderLineHighlight: 'none',
  scrollBeyondLastLine: false,
  wordWrap: 'off',
  minimap: {
    enabled: false,
  },
  // 不显示滚动条
  scrollbar: {
    vertical: 'hidden',
    horizontal: 'hidden',
  },
  overviewRulerBorder: false,
  glyphMargin: false,
  folding: false,
  lineDecorationsWidth: 0, // 行号宽度
  lineNumbersMinChars: 0, // 行号最小宽度
};

const SingleFileMonacoEditor = memo<IProps>(
  forwardRef((props, ref: ForwardedRef<ISingleFileMonacoEditorRefFunction>) => {
    const { className, handelEnter, focusChange } = props;
    const editorRef = useRef<any>(null);
    const monacoEditorRef = useRef<IExportRefFunction>(null);

    const editorId = useMemo(() => {
      return uuid();
    }, []);

    const handleKeydown = useCallback((event) => {
      if (event.key === 'Enter' && editorRef.current) {
        const controller = editorRef.current.getContribution('editor.contrib.suggestController') as any;
        const suggestWidget = controller._widget;
        if (suggestWidget && suggestWidget.suggestWidgetVisible.get()) {
          return;
        }
        // 否则，阻止回车键的默认行为
        event.preventDefault();
        const value = monacoEditorRef.current?.getAllContent().trim() || '';
        handelEnter && handelEnter(value);
      }
    }, []);

    // 监听keydown事件，阻止回车键的默认行为
    const registerShortcutKey = useCallback((_editor, _monaco, isActive) => {
      if (isActive) {
        editorRef.current = _editor;
        window.addEventListener('keydown', handleKeydown);
      } else {
        window.removeEventListener('keydown', handleKeydown);
      }
    }, []);

    const getAllContent = () => {
      return monacoEditorRef.current?.getAllContent() || '';
    };

    useImperativeHandle(ref, () => ({
      getAllContent,
    }));

    return (
      <div ref={ref as any} className={classnames(styles.singleFileMonacoEditor, className)}>
        <MonacoEditor
          ref={monacoEditorRef}
          id={editorId}
          options={options as any}
          shortcutKey={registerShortcutKey}
          focusChange={focusChange}
        />
      </div>
    );
  }),
);

export default SingleFileMonacoEditor;
