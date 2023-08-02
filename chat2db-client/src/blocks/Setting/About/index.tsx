import BrandLogo from '@/components/BrandLogo';
import { APP_NAME, GITHUB_URL } from '@/constants/appConfig';
import i18n from '@/i18n';
import React from 'react';
import styles from './index.less';
import { formatDate, getUserTimezoneTimestamp } from '@/utils/date';

// 关于我们
export default function AboutUs() {
  return (
    <div className={styles.aboutUs}>
      <BrandLogo size={60} className={styles.brandLogo} />
      <div className={styles.brief}>
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
      </div>
    </div>
  );
}
