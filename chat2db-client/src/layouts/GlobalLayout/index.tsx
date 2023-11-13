import React, { useEffect } from 'react';
import usePollRequestService, { ServiceStatus } from '@/hooks/usePollRequestService';
import i18n, { isEn } from '@/i18n';
import { Button, ConfigProvider, Spin, Tooltip } from 'antd';
import antdEnUS from 'antd/locale/en_US';
import antdZhCN from 'antd/locale/zh_CN';
import service from '@/service/misc';
import styles from './index.less';
import MyNotification from '@/components/MyNotification';
import { useTheme } from '@/hooks/useTheme';
import { getAntdThemeConfig } from '@/theme';
import useCopyFocusData from '@/hooks/useFocusData';
import { Outlet } from 'umi';
import init from '../init/init';
import { GithubOutlined, SyncOutlined, WechatOutlined } from '@ant-design/icons';

const GlobalLayout = () => {
  const [appTheme] = useTheme();
  const { serviceStatus, restartPolling } = usePollRequestService({
    loopService: service.testService,
  });
  useCopyFocusData();

  useEffect(() => {
    init();
  }, []);

  // 等待状态页面
  if (serviceStatus === ServiceStatus.PENDING) {
    return <Spin className={styles.loadingBox} size="large" />;
  }

  // 错误状态页面
  if (serviceStatus === ServiceStatus.FAILURE) {
    return (
      <div className={styles.loadingBox}>
        <Button type="primary" onClick={restartPolling} style={{ marginBottom: 20 }}>
          <SyncOutlined />
          {i18n('common.text.tryToRestart')}
        </Button>
        <div className={styles.contact}>
          {i18n('common.text.contactUs')}：
          <GithubOutlined className={styles.icon} onClick={() => window.open('https://github.com/chat2db/Chat2DB')} />
          <Tooltip
            placement="bottom"
            title={<img style={{ width: 200, height: 200 }} src="https://sqlgpt.cn/_static/img/chat2db_wechat.png" />}
          >
            <WechatOutlined className={styles.icon} />
          </Tooltip>
        </div>
      </div>
    );
  }

  return (
    <ConfigProvider locale={isEn ? antdEnUS : antdZhCN} theme={getAntdThemeConfig(appTheme)}>
      <div className={styles.app}>
        <Outlet />
      </div>

      <MyNotification />
    </ConfigProvider>
  );
};

export default GlobalLayout;
