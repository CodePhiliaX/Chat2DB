import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { notification, Space, Button } from 'antd';
import outSideService from '@/service/outside'

interface IProps {
  className?: string;
}

export default memo<IProps>(function TestVersion(props) {
  const { className } = props;
  const [notificationApi, notificationDom] = notification.useNotification();

  useEffect(() => {
    getVersions();
  }, [])

  const close = () => { };

  function getVersions() {
    try {
      const time = +(localStorage.getItem('update-hint-time') || 0);
      const nowTime = new Date().getTime();
      if (time < nowTime) {
        outSideService.checkVersion().then(res => {
          localStorage.setItem('app-gateway-params', JSON.stringify(res))
          openNotification(res);
          const time = new Date().getTime() + 2 * 60 * 60 * 1000;
          localStorage.setItem('update-hint-time', time.toString())
        })
      }
    }
    catch {

    }
  }

  function updateHint() {
    notificationApi.destroy();
    const time = new Date().getTime() + 24 * 60 * 60 * 1000;
    localStorage.setItem('update-hint-time', time.toString());
  }


  function go(responseText: any) {
    window.open(responseText.downloadLink)
    notificationApi.destroy();
  }

  const openNotification = (responseText: any) => {
    console.log(responseText)
    try {
      if (responseText.version !== '1.0.11') {
        const key = `open${Date.now()}`;

        const btn = (
          <Space>
            <Button type="link" size="small" onClick={updateHint}>
              稍后提醒我
            </Button>
            <Button type="primary" size="small" onClick={() => { go(responseText) }}>
              前往更新
            </Button>
          </Space>
        );

        notificationApi.open({
          message: '更新提醒',
          description: `监测到最新版本 v${responseText.version}`,
          btn,
          key,
          onClose: close,
        });
      }
    }
    catch {

    }
  };
  return <>
    {notificationDom}
  </>

})
