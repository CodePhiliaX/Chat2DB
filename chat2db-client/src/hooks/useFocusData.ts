import { useEffect } from 'react';
import { useCommonStore } from '@/store/common';
import { tableCopy, copy } from '@/utils'

// 如果用户点击的不是可复制的元素，就清空选中的内容
function useCopyFocusData() {
  const { setFocusedContent, focusedContent } = useCommonStore((state) => {
    return {
      setFocusedContent: state.setFocusedContent,
      focusedContent: state.focusedContent
    }
  });
  
  // 注册快捷键监听cmd+c或ctrl+c复制focusedContent
  useEffect(() => {
    const handleCopy = (e: KeyboardEvent) => {
      if (e.key === 'c' && (e.metaKey || e.ctrlKey)) {
        if (!focusedContent) return
        // 如果是数据是数组，就调用tableCopy
        if (Array.isArray(focusedContent)) {
          tableCopy(focusedContent as any)
          return
        }
        copy(focusedContent as any);
      }
    };
    document.addEventListener('keydown', handleCopy);
    return () => {
      document.removeEventListener('keydown', handleCopy);
    };
  }, [focusedContent]);

  useEffect(() => {
    const handleClick = (event) => {
      const targetElement = event.target  as Element;
      if (!targetElement.closest('[data-chat2db-general-can-copy-element]')) {
        setFocusedContent(null)
      }
    };
    document.addEventListener('click', handleClick);
    document.addEventListener('contextmenu', handleClick);
    return () => {
      document.removeEventListener('click', handleClick);
      document.removeEventListener('contextmenu', handleClick);
    };
  }, [focusedContent]);
}

export default useCopyFocusData;
