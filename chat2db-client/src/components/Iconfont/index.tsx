import React, { PureComponent } from 'react';
import classnames from 'classnames';
// import desktopStyle from './desktop.less';
import styles from './index.less';

// let container = ''
// container = `
//   @font-face {
//     font-family: 'iconfont'; /* Project id 3633546 */
//     src: url('../../assets/font/iconfont.woff2') format('woff2'),
//       url('../../assets/font/iconfont.woff') format('woff'),
//       url('../../assets/font/iconfont.ttf') format('truetype');
//   }
// `

// if (process.env.UMI_ENV === 'desktop') {
// } else {
//   container = `
//     @font-face {
//       font-family: 'iconfont';  /* Project id 3633546 */
//       src: url('//at.alicdn.com/t/c/font_3633546_bobs6jadiya.woff2?t=1687102726036') format('woff2'),
//           url('//at.alicdn.com/t/c/font_3633546_bobs6jadiya.woff?t=1687102726036') format('woff'),
//           url('//at.alicdn.com/t/c/font_3633546_bobs6jadiya.ttf?t=1687102726036') format('truetype');
//     }
//   `
// }
// let style = document.createElement("style");
// style.type = "text/css";
// document.head.appendChild(style);
// style.appendChild(document.createTextNode(container));

export default class Iconfont extends PureComponent<{
  code: string;
} & React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>> {
  render() {
    return <i {...this.props} className={classnames(this.props.className, styles.iconfont)}>{this.props.code}</i>
  }
}
