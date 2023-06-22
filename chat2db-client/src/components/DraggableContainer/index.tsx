import React, { memo, useRef, useEffect, useState, useMemo } from 'react';
import styles from './index.less';
import classnames from 'classnames';

interface IProps {
  className?: string;
  children: React.ReactNode[];
  min?: number;
  layout?: 'row' | 'column';
  callback?: Function;
  showLine?: boolean;
}

export default memo<IProps>(function DraggableContainer({
  children,
  showLine = true,
  callback,
  min,
  className,
  layout = 'row',
}) {
  const volatileRef = children[0]?.ref;

  const DividerRef = useRef<HTMLDivElement | null>(null);
  const DividerLine = useRef<HTMLDivElement | null>(null);
  const [dragging, setDragging] = useState(false);

  const isRow = layout === 'row';

  useEffect(() => {
    if (!DividerRef.current) {
      return;
    }
    // DividerRef.current.onmouseover = (e) => {
    //   setDragging(true);
    // };
    // DividerRef.current.onmouseout = (e) => {
    //   setDragging(false);
    // };

    DividerRef.current.onmousedown = (e) => {
      if (!volatileRef?.current) return;
      console.log(volatileRef?.curren);

      e.preventDefault();
      setDragging(true);
      const clientStart = isRow ? e.clientX : e.clientY;
      const volatileBoxXY = isRow
        ? volatileRef.current.offsetWidth
        : volatileRef.current.offsetHeight;
      document.onmousemove = (e) => {
        moveHandle(
          isRow ? e.clientX : e.clientY,
          volatileRef.current,
          clientStart,
          volatileBoxXY,
        );
      };
      document.onmouseup = (e) => {
        setDragging(false);
        document.onmouseup = null;
        document.onmousemove = null;
      };
    };
  }, []);

  const moveHandle = (
    nowClientXY: any,
    leftDom: any,
    clientStart: any,
    volatileBoxXY: any,
  ) => {
    let computedXY = nowClientXY - clientStart;
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
          className={classnames(
            styles.divider,
            { [styles.displayDivider]: !children[1] },
          )}
        >
          <div
            ref={DividerRef}
            className={classnames(
              styles.dividerCenter,
              { [styles.dragging]: dragging },
            )}
          />
        </div>
      }
      {children[1]}
    </div>
  );
});
