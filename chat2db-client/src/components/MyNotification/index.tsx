import { Button, Modal, notification, } from 'antd';
import React from 'react'
import styles from './index.less'
import i18n from '@/i18n';
// import { staticNotification } from '@/layouts'

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

function MyNotification(props: IProps) {
  const { errorCode, errorMessage, errorDetail, solutionLink } = props;

  const type = props.type || 'warning';
  const title = `${errorCode}:${errorMessage}`;
  const message = props.message || <div className={styles.message}>{errorCode}:{errorMessage}</div>

  const description = <div className={styles.description}>
    <Button style={{ 'marginRight': '8px' }} type='link' onClick={() => {
      Modal.info({
        bodyStyle: {
          width: '320px'
        },
        title,
        content: errorDetail
      })
    }}>{i18n('common.notification.detial')}</Button>
    <Button type='link' target='_blank' href={solutionLink}>{i18n('common.notification.solution')}</Button>
  </div >


  return notification.open({
    ...props,
    className: styles.notification,
    type,
    message,
    description,
  })


}

export default MyNotification;