import React, { PureComponent } from 'react';
import classnames from 'classnames';
import styles from './index.less';
import Iconfont from '../Iconfont';

export type Theme = 'default' | 'primary';

export default class Button extends PureComponent<{
  theme?: Theme;
  disabled?: boolean;
  loading?: boolean;
} & React.DetailedHTMLProps<React.HTMLAttributes<HTMLButtonElement>, HTMLButtonElement>> {
  render() {
    const { className, children, theme, loading, ...rest } = this.props;
    if (loading) {
      rest.disabled = true
    }
    const _theme = theme ? (typeof theme === 'string' ? [theme] : theme) : [];
    const _className = classnames(className, styles.button, _theme.map(t => styles[t]), {
      [styles.disabled]: rest.disabled
    });
    return <button className={_className} {...rest}>
      {loading && <div className={styles.loadingIcon}>
        <Iconfont code='&#xe6cd;' />
      </div>}
      {children}
    </button>
  }
}
