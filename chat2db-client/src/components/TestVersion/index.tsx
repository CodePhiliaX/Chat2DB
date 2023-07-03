import React, { memo, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { notification, Space, Button } from 'antd';
import outSideService from '@/service/outside';
import i18n from '@/i18n';
import { isVersionHigher } from '@/utils';

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
          const time = new Date().getTime() + 2 * 60 * 60 * 1000;
          localStorage.setItem('update-hint-time', time.toString())
          openNotification(res);
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
    try {
      const needToBeUpdated = isVersionHigher(responseText.version, '2.0.1');
      if (needToBeUpdated) {
        const key = `open${Date.now()}`;
        const btn = (
          <Space>
            <Button type="link" size="small" onClick={updateHint}>
              {i18n('common.text.remindMeLater')}
            </Button>
            <Button type="primary" size="small" onClick={() => { go(responseText) }}>
              {i18n('common.text.goToUpdate')}
            </Button>
          </Space>
        );
        notificationApi.open({
          message: i18n('common.text.updateReminder'),
          description: `${i18n('common.text.detectionLatestVersion')} v${responseText.version}`,
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
