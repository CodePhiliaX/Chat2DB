import React, { useEffect, useMemo } from 'react';
import styles from './index.less';
import i18n from '@/i18n';
import BrandLogo from '@/components/BrandLogo';
import { APP_NAME, WEBSITE_DOC } from '@/constants/appConfig';
import { Button, Radio, Space } from 'antd';
import configService from '@/service/config';
import { DownloadOutlined } from '@ant-design/icons';
import { IUpdateDetectionData } from '../index';
import { IUpdateDetectionRef, UpdatedStatusEnum } from '../UpdateDetection';
import Iconfont from '@/components/Iconfont';
interface IProps {
  updateDetectionData: IUpdateDetectionData | null;
  updateDetectionRef: React.MutableRefObject<IUpdateDetectionRef> | null;
}

// 关于我们
export default function AboutUs(props: IProps) {
  const { updateDetectionData, updateDetectionRef } = props;
  const [updateRule, setUpdateRule] = React.useState<'manual' | 'auto'>(updateDetectionData?.type || 'manual');

  const onChangeUpdateRul = (e) => {
    configService.setAppUpdateType(e.target.value).then(() => {
      setUpdateRule(e.target.value);
    });
  };

  useEffect(()=>{
    setUpdateRule(updateDetectionData?.type || 'manual');
  },[updateDetectionData?.type])

  const jumpDoc = () => {
    window.open(WEBSITE_DOC, '_blank');
  };

  const restartApp = () => {
    window.electronApi?.quitApp();
  }

  const updateButton = useMemo(() => {
    if (!updateDetectionData?.needUpdate) {
      return false;
    }
    switch (updateDetectionData?.updatedStatusEnum) {
      case UpdatedStatusEnum.NOT_UPDATED:
        return (
          <Button
            onClick={() => {
              updateDetectionRef?.current?.openDownload(updateDetectionData);
            }}
            icon={<DownloadOutlined />}
            type="primary"
          >
            {i18n('setting.button.startDownloading')}
          </Button>
        );
      case UpdatedStatusEnum.UPDATING:
        return (
          <Button type="primary" loading>
            {i18n('setting.button.beDownloading')}
          </Button>
        );
      case UpdatedStatusEnum.TIMEOUT:
        return (
          <Button
            onClick={() => {
              updateDetectionRef?.current?.openDownload(updateDetectionData);
            }}
            icon={<DownloadOutlined />}
            type="primary"
          >
            {i18n('setting.button.redownload')}
          </Button>
        );
      case UpdatedStatusEnum.UPDATED:
        return (
          <Button icon={<Iconfont code="&#xe662;" />} type="primary" onClick={restartApp}>
            {i18n('setting.button.restart')}
          </Button>
        );
      // case UpdatedStatusEnum.UPDATED:
      //   return (
      //     <Button icon={<RedoOutlined />} type="primary">
      //       {i18n('setting.button.restart')}
      //     </Button>
      //   );
      default:
        return false;
    }
  }, [updateDetectionData]);

  return (
    <div className={styles.aboutUs}>
      <div className={styles.versionsInfo}>
        <BrandLogo size={98} className={styles.brandLogo} />
        <div>
          <div className={styles.currentVersion}>
            <span className={styles.appName}>{APP_NAME}</span>
            <span>{__APP_VERSION__}</span>
          </div>
          <div className={styles.newVersion}>
            {updateDetectionData?.needUpdate ? (
              UpdatedStatusEnum.UPDATED === updateDetectionData?.updatedStatusEnum ?
              <span>{i18n('setting.text.newEditionIsReady')}</span>
              :
              <span>{i18n('setting.text.discoverNewVersion', updateDetectionData?.version)}</span>
            ) : (
              <span>{i18n('setting.text.isLatestVersion')}</span>
            )}
          </div>
          {updateDetectionData?.desktop && (
            <div className={styles.updateButton}>
              {updateButton}
              <Button onClick={jumpDoc}>{i18n('setting.button.changeLog')}</Button>
            </div>
          )}
        </div>
      </div>
      <div className={styles.updateRule}>
        <div className={styles.updateRuleTitle}>{i18n('setting.title.updateRule')}</div>
        <Radio.Group className={styles.updateRuleGroup} onChange={onChangeUpdateRul} value={updateRule}>
          <Space direction="vertical">
            <Radio className={styles.updateRuleRadio} value="auto">
              {i18n('setting.text.autoUpdate')}
            </Radio>
            <Radio className={styles.updateRuleRadio} value="manual">
              {i18n('setting.text.manualUpdate')}
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
