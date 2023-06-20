import React, { memo, useEffect, useLayoutEffect, useRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { ConfigProvider } from 'antd';
import { history } from 'umi';
import { useLogin } from '@/utils/hooks';
import { getLastPosition, setCurrentPosition } from '@/utils';
import miscService from '@/service/misc';
import LoadingLiquid from '@/components/Loading/LoadingLiquid';
import i18n from '@/i18n';
import { ThemeType } from '@/utils/constants';
import Setting from '@/components/Setting';

interface IProps {
  className?: any;
}

/** 重启次数 */
const restartCount = 200;

declare global {
  interface Window {
    _ENV: string;
    _APP_PORT: string;
    _BUILD_TIME: string;
    _BaseURL: string;
  }
  const __APP_VERSION__: string;
  const __BUILD_TIME__: string;
}

window._ENV = process.env.UMI_ENV! || 'local';

export default memo<IProps>(function AppContainer({ className, children }) {
  const [startSchedule, setStartSchedule] = useState(0); // 0 初始状态 1 服务启动中 2 启动成功
  const [serviceFail, setServiceFail] = useState(false);
  // const scrollTimer = useRef<any>();

  function hashchange() {
    setCurrentPosition();
  }

  // window.addEventListener('scroll', function () {
  //   document.body.toggleAttribute('scroll', true)
  //   scrollTimer.current && clearTimeout(scrollTimer.current)
  //   scrollTimer.current = setTimeout(() => {
  //     document.body.toggleAttribute('scroll')
  //   }, 500)
  // })

  useLayoutEffect(() => {
    settings();
    window.addEventListener('hashchange', hashchange);
    return () => {
      window.removeEventListener('hashchange', hashchange);
    };
  }, []);

  useEffect(() => {
    detectionService();
  }, []);

  function detectionService() {
    setServiceFail(false);
    let flag = 0;
    const time = setInterval(() => {
      miscService.testService().then(() => {
        clearInterval(time);
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

  function settings() {
    let theme = localStorage.getItem('theme') || ThemeType.dark;
    if (theme === 'followOs') {
      theme = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'default'
    }
    document.documentElement.setAttribute('theme', theme);

    document.documentElement.setAttribute(
      'primary-color',
      localStorage.getItem('primary-color') || 'polar-blue',
    );

    if (!localStorage.getItem('lang')) {
      localStorage.setItem('lang', 'zh-cn');
    }

    //禁止右键
    // document.oncontextmenu = (e) => {
    //   e.preventDefault();
    // };
  }

  return (
    <ConfigProvider prefixCls="custom">
      {/* 待启动状态 */}
      {startSchedule === 0 && <div className={classnames(className, styles.app)}></div>}
      {/* 服务启动中 */}
      {startSchedule === 1 && <div className={styles.starting}>
        <div>
          {!serviceFail && <LoadingLiquid />}
          <div className={styles.hint}>
            {/* {serviceFail
              ? i18n('common.text.serviceFail')
              : i18n('common.text.serviceStarting')} */}
            <Setting text={'设置'} />
          </div>
          {serviceFail && (
            <>
              <div className={styles.restart}>
                联系我们-钉钉群：<a href="dingtalk://dingtalkclient/action/sendmsg?dingtalk_id=9135032392">9135032392</a>
              </div>
              <div className={styles.restart} onClick={detectionService}>
                尝试重新启动
              </div>
            </>
          )}
        </div>
      </div>}
      {/* 服务启动完成 */}
      {startSchedule === 2 && <div className={classnames(className, styles.app)}>{children}</div>}
    </ConfigProvider>
  );
});
