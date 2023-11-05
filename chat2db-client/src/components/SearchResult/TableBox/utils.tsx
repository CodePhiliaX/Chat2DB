import { useEffect } from 'react';
import { USER_FILLED_VALUE } from './index';

// 在input中把USER_FILLED_VALUE转换为null
export const transformInputValue = (value: string) => {
  if (value === USER_FILLED_VALUE.DEFAULT) {
    return null;
  }
  return value;
};


// 判断是否聚焦在了可粘贴的区域中 hooks
export const useCheckCanPaste = (setCanPaste)=>{
  useEffect(()=>{
    const handleClick = (event) => {
      const targetElement = event.target  as Element;
      if (targetElement.closest('[data-chat2db-edit-table-data-can-paste]')) {
        setCanPaste(true);
      }else{
        setCanPaste(false);
      }
    };
    document.addEventListener('click', handleClick);
    document.addEventListener('contextmenu', handleClick);
    return () => {
      document.removeEventListener('click', handleClick);
      document.removeEventListener('contextmenu', handleClick);
    };
  },[])
}
