import React, { useEffect, useLayoutEffect, useState } from 'react';
import i18n, { isEn } from '@/i18n';
import { Outlet } from 'umi';
import { ConfigProvider, Spin } from 'antd';
import { v4 as uuidv4 } from 'uuid';
import { getAntdThemeConfig } from '@/theme';
import miscService from '@/service/misc';
import antdEnUS from 'antd/locale/en_US';
import antdZhCN from 'antd/locale/zh_CN';
import { useTheme } from '@/hooks';
import { ThemeType, LangType, PrimaryColorType } from '@/constants/';
import { getLang, setLang } from '@/utils/localStorage';
import { clearOlderLocalStorage } from '@/utils';
import registerMessage from './init/registerMessage';
import registerNotification from './init/registerNotification';
import MyNotification from '@/components/MyNotification';
import indexedDB from '@/indexedDB';
import useCopyFocusData from '@/hooks/useFocusData';
import styles from './index.less';

const initConfig = () => {
  registerMessage();
  registerNotification();
  clearOlderLocalStorage();
};

initConfig();

window._Lang = getLang();

export const colorSchemeListeners: {
  [key: string]: (theme: { backgroundColor: ThemeType; primaryColor: PrimaryColorType }) => void;
} = {};

export function addColorSchemeListener(
  callback: (theme: { backgroundColor: ThemeType; primaryColor: PrimaryColorType }) => void,
) {
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
  const [initEnd, setInitEnd] = useState(false);
  const [appTheme, setAppTheme] = useTheme();
  const [startSchedule, setStartSchedule] = useState(0); // 0 初始状态 1 服务启动中 2 启动成功
  const [serviceFail, setServiceFail] = useState(false);
  useCopyFocusData();

  useLayoutEffect(() => {
    collectInitApp();
  }, []);

  // 初始化app
  function collectInitApp() {
    registerElectronApi();
    monitorOsTheme();
    initLang();
    initIndexedDB();
    setInitEnd(true);
  }

  // 注册Electron关闭时，关闭服务
  function registerElectronApi() {
    window.electronApi?.registerAppMenu({
      version: __APP_VERSION__,
    });
    // 把关闭java服务的的方法传给electron
    window.electronApi?.setBaseURL?.(window._BaseURL);
    // console.log(window.electronApi)
  }

  // 初始化indexedDB
  function initIndexedDB() {
    indexedDB.createDB('chat2db', 1).then((db) => {
      window._indexedDB = {
        chat2db: db,
      };
    });
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
          {startSchedule < 2 && (
            <div className={styles.loadingBox}>
              <Spin spinning={!serviceFail} size="large" />
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
          {startSchedule === 2 && <Outlet />}
        </div>
      )}
   
    </div>
  );
}
