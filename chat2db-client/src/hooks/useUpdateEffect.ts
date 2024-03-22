import { useRef, useEffect } from 'react';

/**
 * 第一次Effect更新不执行 
 * @param fn 
 * @param arr 
 */
export function useUpdateEffect(fn: Function, arr: any[]) {
  const first = useRef(true);
  useEffect(() => {
    if (first.current) {
      first.current = false;
    } else {
      fn();
    }
  }, arr);
}
