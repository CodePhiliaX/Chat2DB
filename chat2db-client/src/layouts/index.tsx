import React, { useEffect, useLayoutEffect, useState } from 'react';
import i18n, { isEn } from '@/i18n';
import { Outlet, useNavigate } from 'umi';
import { ConfigProvider, theme, Spin } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { getAntdThemeConfig, injectThemeVar } from '@/theme';
import { IVersionResponse } from '@/typings';
import miscService from '@/service/misc';
import { getUser } from '@/service/user';
import antdEnUS from 'antd/locale/en_US';
import antdZhCN from 'antd/locale/zh_CN';
import { useTheme } from '@/hooks';
import { ThemeType, LangType } from '@/constants/';
import styles from './index.less';
import { getLang, setLang } from '@/utils/localStorage';
import { clearOlderLocalStorage, getCookie } from '@/utils';
import registerMessage from './init/registerMessage';
import registerNotification from './init/registerNotification';
import MyNotification from '@/components/MyNotification';
import indexedDB from '@/indexedDB';

declare global {
  interface Window {
    _Lang: string;
    _APP_PORT: string;
    _BUILD_TIME: string;
    _BaseURL: string;
    _AppThemePack: { [key in string]: string };
    _appGatewayParams: IVersionResponse;
    _notificationApi: any;
    _indexedDB: any;
  }
  const __APP_VERSION__: string;
  const __BUILD_TIME__: string;
  const __ENV__: string;
  const __APP_PORT__: string;
}

const initConfig = () => {
  registerMessage();
  registerNotification();
  clearOlderLocalStorage();
};

initConfig();

window._Lang = getLang();

const { useToken } = theme;

export const colorSchemeListeners: { [key: string]: () => void } = {};

export function addColorSchemeListener(callback: () => void) {
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
      <AppContainer />
    </ConfigProvider>
  );
}

/** 重启次数 */
const restartCount = 200;

function AppContainer() {
  const navigate = useNavigate();
  const { token } = useToken();
  const [initEnd, setInitEnd] = useState(false);
  const [appTheme, setAppTheme] = useTheme();
  const [startSchedule, setStartSchedule] = useState(1); // 0 初始状态 1 服务启动中 2 启动成功
  const [serviceFail, setServiceFail] = useState(false);
  const [isLogin, setIsLogin] = useState<boolean | null>(null);

  useEffect(() => {
    const date = new Date('2030-12-30 12:30:00').toUTCString();
    document.cookie = `CHAT2DB.LOCALE=${getLang()};Expires=${date}`;
    indexedDB.createDB('chat2db', 1).then((db) => {
      window._indexedDB = {
        chat2db: db,
      };
    });
    getUser().then((res) => {
      if (!res) {
        navigate('/login');
      }
      // 向cookie中写入当前用户id
      document.cookie = `CHAT2DB.USER_ID=${res?.id};Expires=${date}`;
      setIsLogin(!!res);
    });
  }, []);

  useEffect(() => {
    injectThemeVar(token as any, appTheme.backgroundColor, appTheme.primaryColor);
  }, [token]);

  useLayoutEffect(() => {
    collectInitApp();
  }, []);

  // 初始化app
  function collectInitApp() {
    monitorOsTheme();
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
      miscService
        .testService()
        .then(() => {
          clearInterval(time);
          // if (__ENV__ === 'desktop') {
          //   window.location.href = 'http://127.0.0.1:10824/'
          // }
          setStartSchedule(2);
          flag++;
        })
        .catch(() => {
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
          {/* 服务启动中 */}
          {(startSchedule === 1 || isLogin === null) && (
            <div className={styles.loadingBox}>
              <Spin spinning={!serviceFail} size="large" />
              {/* <div className={styles.hint}>
                    <Setting />
                  </div> */}
              {serviceFail && (
                <>
                  <div className={styles.github}>
                    {i18n('common.text.contactUs')}：
                    <a target="_blank" href="https://github.com/chat2db/Chat2DB" rel="noreferrer">
                      github
                    </a>
                  </div>
                  <div className={styles.restart} onClick={detectionService}>
                    {i18n('common.text.tryToRestart')}
                  </div>
                </>
              )}
            </div>
          )}
          {/* 服务启动完成 */}
          {startSchedule === 2 && isLogin !== null && <Outlet />}
        </div>
      )}
      {/* 全局的弹窗 */}
      <MyNotification />
    </div>
  );
}
