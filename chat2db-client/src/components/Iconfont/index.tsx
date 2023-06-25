import React, { PureComponent } from 'react';
import classnames from 'classnames';
// import desktopStyle from './desktop.less';
import styles from './index.less';

// 只有本地开发时使用cdn，发布线上时要下载iconfont到 /assets/font
if (__ENV === 'local') {
  let container = `
  @font-face {
    font-family: 'iconfont';  /* Project id 3633546 */
    src: url('//at.alicdn.com/t/c/font_3633546_0ip5jtwa6g6.woff2?t=1687700061581') format('woff2'),
         url('//at.alicdn.com/t/c/font_3633546_0ip5jtwa6g6.woff?t=1687700061581') format('woff'),
         url('//at.alicdn.com/t/c/font_3633546_0ip5jtwa6g6.ttf?t=1687700061581') format('truetype');
  }
  `
  let style = document.createElement("style");
  style.type = "text/css";
  document.head.appendChild(style);
  style.appendChild(document.createTextNode(container));
}


export default class Iconfont extends PureComponent<{
  code: string;
} & React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>> {
  render() {
    return <i {...this.props} className={classnames(this.props.className, styles.iconfont)}>{this.props.code}</i>
  }
}
