import React, { memo, useRef, useCallback, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Spin } from 'antd';


interface IProps {
  className?: string;
  children?: React.ReactChild | false; // 滚动的内容
  onReachBottom: () => Promise<unknown>; // 触底的数据请求
  threshold: number; // 触底阈值
  scrollerElement: React.MutableRefObject<any>; // overflow：scroll 的盒子
  finished: boolean; // 是否结束
}

export default memo<IProps>(({ className, children, scrollerElement, threshold, onReachBottom, finished }) => {
  const scroller = scrollerElement.current;
  const scrollerRef = useRef(scroller);
  const pendingRef = useRef(false);
  const finishedRef = useRef(false);
  const onBoxMounted = useRef(null);
  const onReachBottomRef = useRef(onReachBottom);

  useEffect(() => {
    scrollerRef.current = scrollerElement.current;
    replenishData(onBoxMounted.current!, scrollerElement.current);
  }, []);

  useEffect(() => {
    finishedRef.current = finished;
  }, [finished]);

  useEffect(() => {
    onReachBottomRef.current = onReachBottom;
  }, [onReachBottom]);

  const onScroll = useCallback(() => {
    if (finishedRef.current || pendingRef.current) {
      return;
    }
    const _scroller = scrollerRef.current;
    if (_scroller) {
      if (_scroller.scrollTop >= _scroller.scrollHeight - _scroller.clientHeight - threshold) {
        pendingRef.current = true;
        onReachBottomRef.current().then(() => {
          pendingRef.current = false;
        });
      }
    }
  }, []);

  useEffect(() => {
    if (scrollerRef.current) {
      scrollerRef.current.addEventListener('scroll', onScroll);
      return () => {
        scrollerRef.current.removeEventListener('scroll', onScroll);
      };
    }
  }, [onScroll]);

  // 填充数据
  const replenishData = (a: HTMLElement, b: HTMLElement) => {
    if (a.clientHeight <= b.clientHeight && !finishedRef.current) {
      onReachBottomRef.current()
      // .then(() => {
      //   setTimeout(() => {
      //     replenishData(a, b);
      //   }, 0);
      // });
    }
  };

  return (
    <div ref={onBoxMounted} className={classnames(className, styles.box)}>
      {children}
      <>
        {!finished && (
          <div className={styles.tips}>
            <Spin className={styles.loading} />
          </div>
        )}
      </>
    </div>
  );
});
