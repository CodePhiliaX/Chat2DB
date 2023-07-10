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
const url = 'https://oss-chat2db.alibaba.com/static/wechat.webp';
export default memo<IProps>(function Popularize(props) {
  const { className } = props;

  const renderTip = () => {
    if (props.tip) {
      return <p>{props.tip}</p>;
    }
    let dom;
    if (props.source === 'setting') {
      dom = <p>{i18n('common.text.wechatPopularizeAi2')}</p>;
    } else {
      dom = <p>{i18n('common.text.wechatPopularizeAi')}</p>;
    }
    return dom;
  };
  return (
    <div className={classnames(styles.box, className)}>
      {/* <div className={styles.title}>获取更多次数</div> */}
      <img className={styles.wechatImg} src={props.source ? url : props.imageUrl} />
      <div className={styles.text}>{renderTip()}</div>
    </div>
  );
});
