import {useRef, useEffect} from 'react';

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