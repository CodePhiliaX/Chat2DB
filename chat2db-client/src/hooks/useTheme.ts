import { useEffect, useRef, useState } from 'react';
import { addColorSchemeListener, colorSchemeListeners } from '@/layouts';
import { getOsTheme } from '@/utils';
import { ITheme } from '@/typings/theme';
import { ThemeType, PrimaryColorType } from '@/constants/common';
import {
  getPrimaryColor,
  getTheme,
  setPrimaryColor,
  setTheme,
} from '@/utils/localStorage';

const initialTheme = () => {
  let backgroundColor = getTheme() || ThemeType.Dark;

  let primaryColor = getPrimaryColor() || PrimaryColorType.Golden_Purple;

  if (backgroundColor === ThemeType.FollowOs) {
    backgroundColor = getOsTheme();
  }
  return {
    backgroundColor,
    primaryColor,
  };
};

export function useTheme<T = ITheme>(): [
  T,
  React.Dispatch<React.SetStateAction<ITheme>>,
] {
  const [appTheme, setAppTheme] = useState<ITheme>(initialTheme());

  useEffect(() => {
    const uuid = addColorSchemeListener(setAppTheme);
    return () => {
      delete colorSchemeListeners[uuid];
    };
  }, []);

  function handleAppThemeChange(theme: {
    backgroundColor: ThemeType;
    primaryColor: PrimaryColorType;
  }) {
    if (theme.backgroundColor === ThemeType.FollowOs) {
      theme.backgroundColor =
        window.matchMedia &&
          window.matchMedia('(prefers-color-scheme: dark)').matches
          ? ThemeType.Dark
          : ThemeType.Light;
    }
    Object.keys(colorSchemeListeners)?.forEach((t) => {
      colorSchemeListeners[t]?.(theme);
    });
    document.documentElement.setAttribute('theme', theme.backgroundColor);
    setTheme(theme.backgroundColor);
    document.documentElement.setAttribute('primary-color', theme.primaryColor);
    setPrimaryColor(theme.primaryColor);
  }

  return [appTheme, handleAppThemeChange] as any;
}
