import React, { memo, useEffect, forwardRef, ForwardedRef, useImperativeHandle } from 'react';
import configService from '@/service/config';
import { notification, Button, Space } from 'antd';
// import i18n from '@/i18n';
import { compareVersion } from '@/utils';
import { IUpdateDetectionData } from '../index';

export enum UpdatedStatusEnum {
  // 未更新
  NOT_UPDATED = 'NOT_UPDATED',
  // 更新中
  UPDATING = 'UPDATING',
  // 更新完成
  UPDATED = 'UPDATED',
  // 更新失败
  UPDATE_FAILED = 'UPDATE_FAILED',
  // 超时
  TIMEOUT = 'TIMEOUT',
}

interface IProps {
  openSettingModal: (number) => void;
  updateDetectionData: IUpdateDetectionData | null;
  setUpdateDetectionData: (data: IUpdateDetectionData) => void;
}

export interface IUpdateDetectionRef {
  openDownload: () => void;
}

// 轮训间隔时间
const INTERVAL_TIME = 10000;
// 最大轮训次数
const MAX_TIMES = 200;

const UpdateDetection = memo(
  forwardRef((props: IProps, ref: ForwardedRef<IUpdateDetectionRef>) => {
    const { openSettingModal, updateDetectionData, setUpdateDetectionData } = props;
    const [notificationApi, notificationDom] = notification.useNotification();
    const timesRef = React.useRef(0);

    useEffect(() => {
      checkUpdate();
    }, []);

    const close = () => {};

    // 检测是否有可更新的版本
    function checkUpdate() {
      configService
        .getLatestVersion({
          currentVersion: __APP_VERSION__,
        })
        .then((res) => {
          // 如果是服务端，那么就不用更新
          if (res.desktop === false) {
            return;
          }

          // 如果是最新版本，那么就不用更新
          if (compareVersion(res.version, __APP_VERSION__) !== 1) {
            return;
          }

          // 如果用户点过知道那么，就不用提示更新
          if (localStorage.getItem('i-see-latest-version') === res.version) {
            return;
          }

          const _updateDetectionData = {
            ...res,
            needUpdate: compareVersion(res.version, __APP_VERSION__) === 1,
            updatedStatusEnum: UpdatedStatusEnum.NOT_UPDATED,
          }
          
          setUpdateDetectionData(_updateDetectionData);
          // 如果是自动更新那么就轮询调后端接口，判断是否更新完成
          if (res.type === 'auto') {
            timesRef.current = 0
            isUpdateSuccess();
            setUpdateDetectionData({
              ..._updateDetectionData,
              updatedStatusEnum: UpdatedStatusEnum.UPDATING,

            })
          } else {
            // 如果是手动更新，那么就提示下载
            if (res.version) {
              openNotificationManual();
            }
          }
        });
    }

    function isUpdateSuccess() {
      if (timesRef.current > MAX_TIMES) {
        setUpdateDetectionData({
          ...updateDetectionData!,
          updatedStatusEnum: UpdatedStatusEnum.TIMEOUT,
        });
        return;
      }
      timesRef.current = timesRef.current + 1

      if (!updateDetectionData?.version) {
        return;
      }
      configService
        .isUpdateSuccess({
          version: updateDetectionData.version,
        })
        .then((res) => {
          if (res) {
            setUpdateDetectionData({
              ...updateDetectionData!,
              updatedStatusEnum: UpdatedStatusEnum.UPDATED,
            });
            openNotificationAuto();
          } else {
            setTimeout(() => {
              isUpdateSuccess();
            }, INTERVAL_TIME);
          }
        });
    }

    function go() {
      // window.open(responseText.downloadLink);
      notificationApi.destroy();
      alert('重启APP');
    }

    const handleISee = () => {
      // 存入localStorage
      localStorage.setItem('i-see-latest-version', updateDetectionData?.version || '');
      notificationApi.destroy();
    };

    const openNotificationAuto = () => {
      const key = `open${Date.now()}`;
      const btn = (
        <Space>
          <Button type="link" size="small" onClick={handleISee}>
            我知道了
          </Button>
          <Button
            type="primary"
            size="small"
            onClick={() => {
              go();
            }}
          >
            立即重启
          </Button>
        </Space>
      );
      notificationApi.open({
        duration: null,
        message: '新版本以下载完成',
        description: '重启软件将会安装新版本',
        btn,
        key,
        onClose: close,
      });
    };

    const openNotificationManual = () => {
      const key = `open${Date.now()}`;
      const btn = (
        <Space>
          <Button type="link" size="small" onClick={handleISee}>
            我知道了
          </Button>
          <Button
            type="primary"
            size="small"
            onClick={() => {
              openSettingModal(3);
              notificationApi.destroy();
            }}
          >
            前往更新
          </Button>
        </Space>
      );
      notificationApi.open({
        duration: null,
        message: '发现新的版本',
        description: updateDetectionData?.version || 'latestVersion?.version',
        btn,
        key,
        onClose: close,
      });
    };

    // 首次更新完成提示
    const openNotificationUpdated = () => {
      const key = `open${Date.now()}`;
      notificationApi.open({
        duration: 6,
        message: `已更新到最新版本${updateDetectionData?.version}`,
        btn: null,
        key,
        onClose: close,
      });
    };

    const openDownload = () => {
      if (!updateDetectionData) {
        return;
      }
      configService.updateDesktopVersion({ ...updateDetectionData }).then(() => {
        timesRef.current = 0
        isUpdateSuccess();
        setUpdateDetectionData({
          ...updateDetectionData,
          updatedStatusEnum: UpdatedStatusEnum.UPDATING,
        });
      });
    };

    useImperativeHandle(ref, () => ({
      openDownload,
    }))

    return <>{notificationDom}</>;
  }),
);

export default UpdateDetection;
