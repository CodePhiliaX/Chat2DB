import { useEffect, useState } from 'react';
import { addColorSchemeListener, colorSchemeListeners } from '@/layouts';
import { getOsTheme } from '@/utils';
import { ITheme } from '@/typings';
import { ThemeType, PrimaryColorType } from '@/constants';
import { getPrimaryColor, getTheme, setPrimaryColor, setTheme } from '@/utils/localStorage';

const initialTheme = () => {
  const localStorageTheme = getTheme();
  const localStoragePrimaryColor = getPrimaryColor();

  // 判断localStorage的theme在不在ThemeType中, 如果存在就用localStorageTheme
  let backgroundColor = ThemeType.Light
  if (Object.values(ThemeType).includes(localStorageTheme)) {
    backgroundColor = localStorageTheme;
  }

  let primaryColor = PrimaryColorType.Golden_Purple
  if (Object.values(PrimaryColorType).includes(localStoragePrimaryColor)) {
    primaryColor = localStoragePrimaryColor;
  }

  if (backgroundColor === ThemeType.FollowOs) {
    backgroundColor = getOsTheme();
  }
  document.documentElement.setAttribute('theme', backgroundColor);
  document.documentElement.setAttribute('primary-color', primaryColor);
  return {
    backgroundColor,
    primaryColor,
  };
};

export function useTheme<T = ITheme>(): [T, React.Dispatch<React.SetStateAction<ITheme>>] {
  const [appTheme, setAppTheme] = useState<ITheme>(initialTheme());

  // const isDark = useMemo(() => appTheme.backgroundColor === ThemeType.Dark, [appTheme]);

  useEffect(() => {
    const uuid = addColorSchemeListener(setAppTheme as any);
    return () => {
      delete colorSchemeListeners[uuid];
    };
  }, []);

  function handleAppThemeChange(theme: { backgroundColor: ThemeType; primaryColor: PrimaryColorType }) {
    if (theme.backgroundColor === ThemeType.FollowOs) {
      theme.backgroundColor =
        window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
          ? ThemeType.DarkDimmed
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
