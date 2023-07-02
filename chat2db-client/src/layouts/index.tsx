import React, { useEffect, useLayoutEffect } from 'react';
import i18n from '@/i18n';
import { Outlet } from 'umi';
import { ConfigProvider, theme, notification } from 'antd';
import { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { getAntdThemeConfig } from '@/theme';
import { IVersionResponse } from '@/typings';
import classnames from 'classnames';
import LoadingLiquid from '@/components/Loading/LoadingLiquid';
import Setting from '@/blocks/Setting';
import miscService from '@/service/misc';

import antdEnUS from 'antd/locale/en_US';
import antdZhCN from 'antd/locale/zh_CN';
import { useTheme } from '@/hooks';
import { isEn } from '@/utils/check';
import { ThemeType, PrimaryColorType, LangType } from '@/constants/';
import { InjectThemeVar } from '@/theme';
import styles from './index.less';
import { getLang, getPrimaryColor, getTheme, setLang } from '@/utils/localStorage';
import { clearOlderLocalStorage } from '@/utils';

declare global {
  interface Window {
    _Lang: string;
    _APP_PORT: string;
    _BUILD_TIME: string;
    _BaseURL: string;
    _AppThemePack: { [key in string]: string };
    _appGatewayParams: IVersionResponse;
  }
  const __APP_VERSION__: string;
  const __BUILD_TIME__: string;
  const __ENV: string;
}

clearOlderLocalStorage();

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

/** 重启次数 */
const restartCount = 200;

function AppContainer() {
  const { token } = useToken();
  const [initEnd, setInitEnd] = useState(false);
  const [appTheme, setAppTheme] = useTheme();
  const [startSchedule, setStartSchedule] = useState(0); // 0 初始状态 1 服务启动中 2 启动成功
  const [serviceFail, setServiceFail] = useState(false);

  useEffect(() => {
    let date = new Date('2030-12-30 12:30:00').toUTCString();
    document.cookie = `CHAT2DB.LOCALE=${getLang()};Expires=${date}`;
  }, []);

  useEffect(() => {
    InjectThemeVar(token as any, appTheme.backgroundColor, appTheme.primaryColor);
  }, [token]);

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
        (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
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

  useEffect(() => {
    detectionService();
  }, []);

  function detectionService() {
    setServiceFail(false);
    let flag = 0;
    const time = setInterval(() => {
      miscService.testService().then(() => {
        clearInterval(time);
        // if (__ENV === 'desktop') {
        //   window.location.href = 'http://127.0.0.1:10824/'
        // }
        setStartSchedule(2);
        flag++;
      }).catch(error => {
        setStartSchedule(1);
        flag++;
      });
      if (flag > restartCount) {
        setServiceFail(true);
        clearInterval(time);
      }
    }, 1000);
  }

  return (
    <div className={styles.appContainer}>
      {initEnd && (
        <div className={styles.app}>
          {/* 待启动状态 */}
          {startSchedule === 0 && <div></div>}
          {/* 服务启动中 */}
          {startSchedule === 1 && <div className={styles.starting}>
            <div className={styles.loadingBox}>
              {
                !serviceFail && <div>
                  <LoadingLiquid />
                </div>
              }
              <div className={styles.hint}>
                <Setting text={i18n('common.text.setting')} />
              </div>
              {serviceFail && (
                <>
                  <div className={styles.github}>
                    {i18n('common.text.contactUs')}-github：<a target="_blank" href="https://github.com/chat2db/Chat2DB">github</a>
                  </div>
                  <div className={styles.restart} onClick={detectionService}>
                    {i18n('common.text.tryToRestart')}
                  </div>
                </>
              )}
            </div>
          </div>}
          {/* 服务启动完成 */}
          {startSchedule === 2 && <Outlet />}
        </div>
      )}
    </div>
  );
}
