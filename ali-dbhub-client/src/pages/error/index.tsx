import React, { memo } from 'react';
import styles from './index.less';
import { history } from 'umi';
import ghostImg from '@/assets/ghost.png'

export default function ErrorPage() {
  const goHone = () => {
    history.push('/')
  }
  return <div className={styles.page}>
    <div className={styles.box}>
      <div className={styles.left}>
        <div className={styles.top}>
          <div>ERROR 404</div>
          <div>找不到页面：'/home/xxx'</div>
        </div>
        <div className={styles.bottom} onClick={goHone}>
          <div className={styles.button}>
            返回首页
          </div>
        </div>
      </div>
      <div className={styles.right}>
        <img className={styles.ghost} src={ghostImg} alt="" />
        <div className={styles.shadow}></div>
      </div>
    </div>
  </div>
}