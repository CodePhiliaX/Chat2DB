import { useEffect } from 'react';
import { useWorkspaceStore } from '@/store/workspace';

// 如果用户点击的不是可复制的元素，就清空选中的内容
// TODO: 命名待调整
function useCancelCopyFocusData() {
  const {  setFocusedContent } = useWorkspaceStore((state) => {
    return {
      setFocusedContent: state.setFocusedContent
    }
  });
  useEffect(() => {
    const handleClick = (event) => {
      const targetElement = event.target  as Element;
      if (!targetElement.closest('[data-chat2db-can-copy-element]')) {
        setFocusedContent(null)
      }
    };
    document.addEventListener('click', handleClick);
    document.addEventListener('contextmenu', handleClick);
    return () => {
      document.removeEventListener('click', handleClick);
      document.removeEventListener('contextmenu', handleClick);
    };
  }, []);
}

export default useCancelCopyFocusData;
