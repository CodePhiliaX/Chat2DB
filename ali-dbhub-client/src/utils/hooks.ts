import { useCallback, useEffect, useRef, useState } from 'react';
import { addColorSchemeListener } from '@/components/Setting';

export function useDebounce<A extends any[]>(
  callback: (...args: A) => void,
  timeout: number,
) {
  const timer = useRef<any>();
  return useCallback<(...args: A) => void>(
    (...args) => {
      if (timer.current) {
        clearTimeout(timer.current);
        timer.current = undefined;
      }
      timer.current = setTimeout(() => {
        callback(...args);
        timer.current = undefined;
      }, timeout);
    },
    [callback, timeout],
  );
}

export function useLogin() {
  const [isLogin, setIsLogin] = useState(1);
  return [isLogin];
}

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

export function useTheme() {
  let theme = localStorage.getItem('theme') || 'dark'
  if (theme === 'followOs') {
    theme = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'default'
  }
  const [currentColorScheme, setCurrentColorScheme] = useState(theme);
  useEffect(() => {
    addColorSchemeListener(setCurrentColorScheme);
  }, []);
  return currentColorScheme;
}

export function useCanDoubleClick() {
  const count = useRef(0);
  return ({
    onClick,
    onDoubleClick,
  }: {
    onClick?: Function;
    onDoubleClick?: Function;
  }) => {
    count.current = count.current + 1;
    if (count.current == 2) {
      onDoubleClick && onDoubleClick();
      count.current = 0;
    } else {
      setTimeout(() => {
        if (count.current == 1) {
          onClick && onClick();
        }
        count.current = 0;
      }, 200);
    }
  };
}

export function useOnlyOnceTask(fn: Function) {
  const [isFirst, setIsFirst] = useState(true);
  const [lastData, setLastData] = useState<any>();
  if (isFirst) {
    setIsFirst(false);
    const lastData = fn();
    setLastData(lastData);
    return lastData;
  } else {
    return lastData;
  }
}
