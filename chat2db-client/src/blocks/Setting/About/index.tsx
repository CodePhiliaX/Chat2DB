import BrandLogo from '@/components/BrandLogo';
import { APP_NAME, GITHUB_URL, WEBSITE_DOC } from '@/constants/appConfig';
import i18n from '@/i18n';
import React from 'react';
import styles from './index.less';
// import { formatDate, getUserTimezoneTimestamp } from '@/utils/date';
import { Button, Radio, Space } from 'antd';

// 关于我们
export default function AboutUs() {
  const [updateRule, setUpdateRule] = React.useState(1); // 1:自动下载并安装更新 2:仅在新版本发布时提醒我
  const onChangeUpdateRul = () => {
    setUpdateRule(updateRule === 1 ? 2 : 1);
  };

  const jumpDoc = () => {
    window.open(WEBSITE_DOC, '_blank');
  };

  return (
    <div className={styles.aboutUs}>
      <div className={styles.versionsInfo}>
        <BrandLogo size={98} className={styles.brandLogo} />
        <div>
          <div className={styles.currentVersion}>
            <span className={styles.appName}>{APP_NAME}</span>
            <span>{__APP_VERSION__}</span>
          </div>
          <div className={styles.newVersion}>发现新版本10.0.0</div>
          <div className={styles.updateButton}>
            <Button type="primary">立即更新</Button>
            <Button onClick={jumpDoc}>查看更新日志</Button>
          </div>
        </div>
      </div>
      <div className={styles.updateRule}>
        <div className={styles.updateRuleTitle}>软件更新</div>
        <Radio.Group className={styles.updateRuleGroup} onChange={onChangeUpdateRul} value={updateRule}>
          <Space direction="vertical">
            <Radio className={styles.updateRuleRadio} value={1}>
              新版自动下载并安装更新
            </Radio>
            <Radio className={styles.updateRuleRadio} value={2}>
              仅在新版本发布时提醒我
            </Radio>
          </Space>
        </Radio.Group>
      </div>
      {/* <div className={styles.brief}>
        <div className={styles.appName}>{APP_NAME}</div>
        <div className={styles.env}>
          {i18n('setting.text.currentEnv')}:{__ENV__}
        </div>
        <div className={styles.version}>
          {i18n('setting.text.currentVersion')}:v{__APP_VERSION__} build
          {formatDate(getUserTimezoneTimestamp(__BUILD_TIME__), 'yyyyMMddhhmmss')}
        </div>
        <a target="blank" href={GITHUB_URL} className={styles.log}>
          {i18n('setting.text.viewingUpdateLogs')}
        </a>
      </div> */}
    </div>
  );
}
