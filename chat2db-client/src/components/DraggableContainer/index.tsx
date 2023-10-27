import React, { memo, useRef, useEffect, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';

interface IProps {
  className?: string;
  children: any; //TODO: TS，约定接受一个数组，第一项child需要携带ref
  min?: number;
  layout?: 'row' | 'column';
  callback?: (data: any) => void;
  showLine?: boolean;
}

export default memo<IProps>((props: IProps) => {
  const { children, showLine = true, callback, min, className, layout = 'row' } = props;
  const volatileRef = children[0]?.ref;

  const DividerRef = useRef<HTMLDivElement | null>(null);
  const DividerLine = useRef<HTMLDivElement | null>(null);
  const [dragging, setDragging] = useState(false);

  const isRow = layout === 'row';

  useEffect(() => {
    if (!DividerRef.current) {
      return;
    }

    DividerRef.current.onmousedown = (e) => {
      if (!volatileRef?.current) return;
      e.preventDefault();
      setDragging(true);
      const clientStart = isRow ? e.clientX : e.clientY;
      const volatileBoxXY = isRow ? volatileRef.current.offsetWidth : volatileRef.current.offsetHeight;
      document.onmousemove = (_e) => {
        moveHandle(isRow ? _e.clientX : _e.clientY, volatileRef.current, clientStart, volatileBoxXY);
      };
      document.onmouseup = () => {
        setDragging(false);
        document.onmouseup = null;
        document.onmousemove = null;
      };
    };
  }, []);

  const moveHandle = (nowClientXY: any, leftDom: any, clientStart: any, volatileBoxXY: any) => {
    const computedXY = nowClientXY - clientStart;
    let finalXY = 0;

    finalXY = volatileBoxXY + computedXY;

    if (min && finalXY < min) {
      return;
    }
    if (isRow) {
      leftDom.style.width = finalXY + 'px';
    } else {
      leftDom.style.height = finalXY + 'px';
    }
    callback && callback(finalXY);
  };

  return (
    <div className={classnames(styles.box, { [styles.box_column]: !isRow }, className)}>
      {children[0]}
      {
        <div
          style={{ display: showLine ? 'block' : 'none' }}
          ref={DividerLine}
          className={classnames(styles.divider, { [styles.displayDivider]: !children[1] })}
        >
          <div ref={DividerRef} className={classnames(styles.dividerCenter, { [styles.dragging]: dragging })} />
        </div>
      }
      {children[1]}
    </div>
  );
});
