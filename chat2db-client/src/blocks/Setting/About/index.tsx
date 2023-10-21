import React, { useEffect, useMemo } from 'react';
import styles from './index.less';
// import i18n from '@/i18n';
import BrandLogo from '@/components/BrandLogo';
import { APP_NAME, WEBSITE_DOC } from '@/constants/appConfig';
// import { formatDate, getUserTimezoneTimestamp } from '@/utils/date';
import { Button, Radio, Space } from 'antd';
import configService from '@/service/config';
import { DownloadOutlined } from '@ant-design/icons';
import { IUpdateDetectionData } from '../index';
import { IUpdateDetectionRef, UpdatedStatusEnum } from '../UpdateDetection';

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

  const updateButton = useMemo(() => {
    if (!updateDetectionData?.needUpdate) {
      return false;
    }
    switch (updateDetectionData?.updatedStatusEnum) {
      case UpdatedStatusEnum.NOT_UPDATED:
        return (
          <Button
            onClick={() => {
              updateDetectionRef?.current?.openDownload();
            }}
            icon={<DownloadOutlined />}
            type="primary"
          >
            开始下载
          </Button>
        );
      case UpdatedStatusEnum.UPDATING:
        return (
          <Button type="primary" loading>
            下载中
          </Button>
        );
      // 超时后端如何处理 TODO:
      case UpdatedStatusEnum.TIMEOUT:
        return (
          <Button
            onClick={() => {
              updateDetectionRef?.current?.openDownload();
            }}
            icon={<DownloadOutlined />}
            type="primary"
            loading
          >
            超时重新下载
          </Button>
        );
      case UpdatedStatusEnum.UPDATED:
        return (
          <Button icon={<DownloadOutlined />} type="primary">
            立即重启
          </Button>
        );
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
              <span>发现新版本{updateDetectionData?.version}</span>
            ) : (
              <span>已是最新版本</span>
            )}
          </div>
          {updateDetectionData?.desktop && (
            <div className={styles.updateButton}>
              {updateButton}
              <Button onClick={jumpDoc}>查看更新日志</Button>
            </div>
          )}
        </div>
      </div>
      <div className={styles.updateRule}>
        <div className={styles.updateRuleTitle}>软件更新</div>
        <Radio.Group className={styles.updateRuleGroup} onChange={onChangeUpdateRul} value={updateRule}>
          <Space direction="vertical">
            <Radio className={styles.updateRuleRadio} value="auto">
              新版自动下载并安装更新
            </Radio>
            <Radio className={styles.updateRuleRadio} value="manual">
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
