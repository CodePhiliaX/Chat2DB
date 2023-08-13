import React, { PureComponent } from 'react';
import classnames from 'classnames';
// import desktopStyle from './desktop.less';
import styles from './index.less';

// 只有本地开发时使用cdn，发布线上时要下载iconfont到 /assets/font
if (__ENV__ === 'local') {
  let container = `
  /* 在线链接服务仅供平台体验和调试使用，平台不承诺服务的稳定性，企业客户需下载字体包自行发布使用并做好备份。 */
  @font-face {
    font-family: 'iconfont';  /* Project id 3633546 */
    src: url('//at.alicdn.com/t/c/font_3633546_72e0owin532.woff2?t=1691843170071') format('woff2'),
         url('//at.alicdn.com/t/c/font_3633546_72e0owin532.woff?t=1691843170071') format('woff'),
         url('//at.alicdn.com/t/c/font_3633546_72e0owin532.ttf?t=1691843170071') format('truetype');
  }
  `;
  let style = document.createElement('style');
  style.type = 'text/css';
  document.head.appendChild(style);
  style.appendChild(document.createTextNode(container));
}

export default class Iconfont extends PureComponent<
  {
    code: string;
  } & React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>
> {
  render() {
    return (
      <i {...this.props} className={classnames(this.props.className, styles.iconfont)}>
        {this.props.code}
      </i>
    );
  }
}
