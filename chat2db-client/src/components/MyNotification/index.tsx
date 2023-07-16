import React, { useCallback, useEffect, useState } from 'react'
import { Button, ConfigProvider, Modal, notification, Space } from 'antd';
import styles from './index.less'
import i18n from '@/i18n';
import { IconType } from 'antd/es/notification/interface';
import Iconfont from '../Iconfont';

interface IProps {
  type?: IconType;
  message?: React.ReactNode;
  /** 错误代码 */
  errorCode: string;
  /** 错误信息 */
  errorMessage: string;
  /** 错误详情 */
  errorDetail: string;
  /** 问题wiki路径 */
  solutionLink: string;
}

function MyNotification() {
  const [notificationApi, notificationDom] = notification.useNotification({
    maxCount: 2
  });
  const [open, setOpen] = useState(false);
  const [props, setProps] = useState<IProps>()

  window._notificationApi = useCallback((props: IProps) => {
    const { errorCode, errorMessage, errorDetail, solutionLink } = props;
    setProps(props);
    const btn = (
      <Space>
        <Button type="link" size="small" onClick={() => {
          setOpen(true);
        }}>
          {i18n('common.notification.detial')}
        </Button>
        <Button type="link" size="small" target='_blank' href={solutionLink}>
          {i18n('common.notification.solution')}
        </Button>
      </Space>
    );

    const renderDescription = () => {
      return <div className={styles.description}>
        {props.errorCode}{props.errorCode}{props.errorCode}{props.errorCode}
      </div>
    }

    const renderMessage = () => {
      return <div className={styles.message}>
        <Iconfont code='&#xe60c;' />
        Error
      </div>
    }

    notificationApi.open({
      className: styles.notification,
      message: renderMessage(),
      description: renderDescription(),
      placement: 'bottomRight',
      btn,
      duration: null,
    })
  }, [])


  return <>
    {notificationDom}
    <Modal
      className={styles.modal}
      open={open}
      title={props?.errorCode}
      width='70vw'
      footer={[]}
      onCancel={() => {
        setOpen(false)
      }}
    >
      {props?.errorDetail}
    </Modal>
  </>
}

export default MyNotification;