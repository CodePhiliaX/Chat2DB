import React, { useEffect, useLayoutEffect } from 'react';
import { Outlet } from 'umi';
import { ConfigProvider, theme } from 'antd';
import { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { getAntdThemeConfig } from '@/theme';

import antdEnUS from 'antd/locale/en_US';
import antdZhCN from 'antd/locale/zh_CN';
import { useTheme } from '@/hooks';
import { isEn } from '@/utils/check';
import { ThemeType, PrimaryColorType, LangType } from '@/constants/common';
import { InjectThemeVar } from '@/theme'
import styles from './index.less';
import {
  getLang,
  getPrimaryColor,
  getTheme,
  setLang,
} from '@/utils/localStorage';

declare global {
  interface Window {
    _Lang: string;
    _ENV: string;
    _APP_PORT: string;
    _BUILD_TIME: string;
    _BaseURL: string;
    _AppThemePack: { [key in string]: string };
  }
  const __APP_VERSION__: string;
  const __BUILD_TIME__: string;
}

console.log(process.env.UMI_ENV);

window._ENV = process.env.UMI_ENV! || 'local';
window._Lang = getLang();

const { getDesignToken, useToken } = theme;

export const colorSchemeListeners: { [key: string]: Function } = {};

export function addColorSchemeListener(callback: Function) {
  const uuid = uuidv4();
  colorSchemeListeners[uuid] = callback;
  return uuid;
}

export default function Layout() {
  const [appTheme] = useTheme();
  const [antdTheme, setAntdTheme] = useState<any>({});

  useLayoutEffect(() => {
    setAntdTheme(getAntdThemeConfig(appTheme));
  }, [appTheme]);

  return (
    <ConfigProvider locale={isEn ? antdEnUS : antdZhCN} theme={antdTheme}>
      <AppContainer></AppContainer>
    </ConfigProvider>
  );
}


function AppContainer() {
  const { token } = useToken();
  const [initEnd, setInitEnd] = useState(false);
  const [appTheme, setAppTheme] = useTheme();

  useEffect(() => {
    InjectThemeVar(token as any, appTheme.backgroundColor, appTheme.primaryColor);
  }, [token])

  useLayoutEffect(() => {
    collectInitApp();
  }, []);

  // 初始化app
  function collectInitApp() {
    monitorOsTheme();
    initTheme();
    initLang();
    setInitEnd(true);
  }

  // 监听系统(OS)主题变化
  function monitorOsTheme() {
    function change(e: any) {
      setAppTheme({
        ...appTheme,
        backgroundColor: e.matches ? ThemeType.Dark : ThemeType.Light,
      });
    }

    const themeMedia = window.matchMedia('(prefers-color-scheme: dark)');
    themeMedia.addListener(change);
    return () => {
      themeMedia.removeListener(change);
    };
  }

  // 初始化主题
  function initTheme() {
    let theme = getTheme();
    if (theme === ThemeType.FollowOs) {
      theme =
        (window.matchMedia &&
          window.matchMedia('(prefers-color-scheme: dark)').matches
          ? ThemeType.Dark
          : ThemeType.Light) || ThemeType.Dark;
    }
    document.documentElement.setAttribute('theme', theme);
    document.documentElement.setAttribute('primary-color', getPrimaryColor());
  }

  // 初始化语言
  function initLang() {
    if (!getLang()) {
      setLang(LangType.EN_US);
      document.documentElement.setAttribute('lang', LangType.EN_US);
    }
  }

  return <div className={styles.appContainer}>
    {
      initEnd &&
      <div className={styles.app}>
        <Outlet />
      </div>
    }
  </div>
}


