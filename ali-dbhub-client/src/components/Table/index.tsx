import React, { memo, useEffect, useState, useRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { useOnlyOnceTask } from '@/utils/hooks';
import Iconfont from '@/components/Iconfont';
import { createTableRows, IColumn } from '@/components/TableColumns';
import { IOptions } from '@/types';

interface IProps {
  className?: string;
  columns: IColumn[];
  dataSource: T[];
  changeData?: Function
}

export enum IRowState {
  NEW = 'new',
  OLD = 'old'
}

interface IRow {
  index: number;
  state: IRowState;
  columnName: string;
  type: string;
  length: string;
  unNull: boolean;
  comment: string;
  isEdit: boolean;
}

const CategoryLineHeight = 46;

export default memo<IProps>(function Table({ className, columns, dataSource, changeData }) {
  const [newData, setNewData] = useState(dataSource)
  const scrollBoxRef = useRef<any>();
  // const dataSourceRef = useRef<IRow[]>([createDefaultColumn()]);
  const mysqlDataTypeOptionsRef = useRef<IOptions[]>();
  const [, setRefresh] = useState(0);
  const [dragIndex, setDragIndex] = useState<number>();
  const [currentDragMovePx, setCurrentDragMovePx] = useState<string>();
  const [draggedIndex, setDraggedIndex] = useState<number>();

  useEffect(() => {
    setNewData(dataSource)
  }, [dataSource])

  function renderDrag(t: IRow, i: number) {
    // if (t.state === 'new') {
    // }
    return <div className={styles.cellMoveBox} onMouseDown={(e) => { onMouseDown(e, t, i) }}>{
      <Iconfont code="&#xe611;" />
    }</div>
  }

  function moveStyle(index: number) {
    if (index === dragIndex) {
      return { transform: `translateY(${currentDragMovePx}px)` }
    } else if ((index > dragIndex!) && (draggedIndex! >= index)) {
      return { transform: `translateY(-${CategoryLineHeight}px)`, }
    } else if ((index < dragIndex!) && (draggedIndex! <= index)) {
      return { transform: `translateY(${CategoryLineHeight}px)` }
    } else {
      return { transform: `translateY(0px)` }
    }
  }

  function onMouseDown(e: React.MouseEvent, t: IRow, i: number) {
    setDragIndex(i);
    const start = e.clientY;
    const mask = scrollBoxRef.current!;

    function check(e: MouseEvent): number {
      const { clientY } = e;
      const extent = start - clientY;
      setCurrentDragMovePx(`${-(extent)}`);
      const absoluteValue = extent > 0 ? extent : -extent
      if (absoluteValue / CategoryLineHeight > 0.5) {
        const target = extent > 0
          ?
          i - Math.round(absoluteValue / CategoryLineHeight)
          :
          i + Math.round(absoluteValue / CategoryLineHeight)
        setDraggedIndex(target)
        return target
      } else {
        setDraggedIndex(dragIndex)
        return dragIndex!
      }
    }

    function mouseup(e: MouseEvent) {
      const target = check(e);
      if (target || target === 0) {
        const newList = [...dataSource!];
        const item = newList.splice(i, 1)[0];
        newList.splice(target, 0, item);
        setNewData(newList)
        console.log(newList)
        changeData && changeData(newData)
      }
      setCurrentDragMovePx('0px')
      setDragIndex(undefined)
      setDraggedIndex(undefined)
      mask.removeEventListener('mouseup', mouseup)
      mask.removeEventListener('mousemove', check)
    }

    mask.addEventListener('mousemove', check);
    mask.addEventListener('mouseup', mouseup);
  }

  const { Header: TableHeader, Row: TableRow } = useOnlyOnceTask(() => {
    const newColumns = columns.map(item => {
      if (item.drag) {
        return {
          ...item,
          renderCell: (t: IRow, i) => renderDrag(t, i)
        }
      } else {
        return item
      }
    })
    return createTableRows<IRow>(newColumns)
  });

  return <div className={classnames(className, styles.table)}>
    <TableHeader className={styles.tableHeader}></TableHeader>
    <div className={styles.scrollBox} ref={scrollBoxRef}>
      <div className={classnames(styles.tableMain, { [styles.dragging]: dragIndex !== undefined })}>
        {
          newData?.map((t: any, index: number) => {
            return <TableRow
              className={classnames(styles.tableRow, { [styles.draggingRow]: index === dragIndex })}
              style={moveStyle(index)}
              index={index}
              key={t.index}
              data={t}
            ></TableRow>
          })
        }
      </div>
    </div>
  </div>
})
