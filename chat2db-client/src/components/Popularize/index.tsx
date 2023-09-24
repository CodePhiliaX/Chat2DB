import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import i18n from '@/i18n';

interface IProps {
  className?: string;
  source?: 'setting';
  imageUrl?: string;
  tip?: string;
}
const url =
  'http://oss.sqlgpt.cn/static/chat2db-wechat.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_256/quality,Q_80/format,webp';
export default memo<IProps>(function Popularize(props) {
  const { className } = props;

  const renderTip = () => {
    if (props.tip) {
      return <p>{props.tip}</p>;
    }
    let dom;
    if (props.source === 'setting') {
      dom = <p>{'关注公众号获取AI Key'}</p>;
    } else {
      dom = <p>{i18n('common.text.wechatPopularizeAi')}</p>;
    }
    return dom;
  };

  const renderImage = () => {
    if (!props.source && !props.imageUrl) {
      return null;
    }
    return <img className={styles.wechatImg} src={props.source ? url : props.imageUrl} />;
  };
  
  return (
    <div className={classnames(styles.box, className)}>
      {/* <div className={styles.title}>获取更多次数</div> */}
      {renderImage()}
      <div className={styles.text}>{renderTip()}</div>
    </div>
  );
});
