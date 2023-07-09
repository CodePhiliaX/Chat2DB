import React, { memo } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import './index.less'

interface IProps {
  className?: any;
}

export default memo(function LoadingLiquid(props: IProps) {
  const { className } = props;
  return <div className={styles.box}>
    <div className={styles.loading}>
      <span style={{ '--i': 1 } as any} ></span>
      <span style={{ '--i': 2 } as any} ></span>
      <span style={{ '--i': 3 } as any} ></span>
      <span style={{ '--i': 4 } as any} ></span>
      <span style={{ '--i': 5 } as any} ></span>
      <span style={{ '--i': 6 } as any} ></span>
      <span style={{ '--i': 7 } as any} ></span>
    </div>
    <svg>
      <filter id="gooey">
        <feGaussianBlur in="SourceGraphic" stdDeviation="10" />
        <feColorMatrix values="
            1 0 0 0 0 
            0 1 0 0 0
            0 0 1 0 0 
            0 0 0 20 -10
            " />
      </filter>
    </svg>
  </div>

});
