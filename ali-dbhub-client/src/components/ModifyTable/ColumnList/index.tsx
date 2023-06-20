import React, { memo, useCallback, useContext, useDebugValue, useEffect, useLayoutEffect, useRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Button, DatePicker, Input, Form, Modal, message, Checkbox, Select } from 'antd';
import { createTableRows } from '@/components/TableColumns'
import Iconfont from '@/components/Iconfont';
import DraggingRow from '@/components/DraggingRow';
import { useOnlyOnceTask } from '@/utils/hooks';
import { mysqlDataType } from '@/data/dataType';
import { IOptions } from '@/types';
import { scrollPage } from '@/utils';
import { Context } from '@/components/ModifyTable/ModifyTable'
import Table from '@/components/Table'
import { IEditTableConsole } from '@/types'

interface IProps {
  className?: string;
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

export default memo<IProps>(function ColumnList({ className }) {
  const scrollBoxRef = useRef<any>();
  const dataSourceRef = useRef<IRow[]>([createDefaultColumn()]);
  const mysqlDataTypeOptionsRef = useRef<IOptions[]>();
  const [, setRefresh] = useState(0);
  const [dragIndex, setDragIndex] = useState<number>();
  const [currentDragMovePx, setCurrentDragMovePx] = useState<string>();
  const [dragedIndex, setDragedIndex] = useState<number>();
  const data: IEditTableConsole | null = useContext(Context)

  useEffect(() => {
    if (data?.tableData?.children?.[0].children) {
      dataSourceRef.current = data.tableData.children[0].children.map((t, i) => {
        return {
          index: new Date().getTime() + i,
          state: IRowState.OLD,
          columnName: t.name,
          type: t.dataType!,
          length: '',
          unNull: false,
          comment: '',
          isEdit: false
        }
      })
      setRefresh(new Date().getTime())
    }
  }, [])


  useEffect(() => {
    const newList = mysqlDataType.map(item => {
      return {
        value: item.name,
        label: item.name,
        type: item.type
      }
    })
    mysqlDataTypeOptionsRef.current = newList
    setRefresh(new Date().getTime())
  }, [])

  function createDefaultColumn(): IRow {
    return {
      index: new Date().getTime(),
      state: IRowState.NEW,
      columnName: '',
      type: '',
      length: '',
      unNull: false,
      comment: '',
      isEdit: true
    }
  }

  function renderDrag(t: IRow, i: number) {
    // if (t.state === 'new') {
    // }
    return <div className={styles.cellMoveBox} onMouseDown={(e) => { onMouseDown(e, t, i) }}>{
      <Iconfont code="&#xe611;" />
    }</div>
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
        setDragedIndex(target)
        return target
      } else {
        setDragedIndex(dragIndex)
        return dragIndex!
      }
    }

    function mouseup(e: MouseEvent) {
      const target = check(e);
      if (target || target === 0) {
        const newList = [...dataSourceRef.current!];
        const item = newList.splice(i, 1)[0];
        newList.splice(target, 0, item);
        dataSourceRef.current = newList
      }
      setCurrentDragMovePx('0px')
      setDragIndex(undefined)
      setDragedIndex(undefined)
      mask.removeEventListener('mouseup', mouseup)
      mask.removeEventListener('mousemove', check)
    }

    mask.addEventListener('mousemove', check);
    mask.addEventListener('mouseup', mouseup);
  }

  function onChangeIsNull() {

  }

  function changeCurrentEdit(data: IRow, list?: IRow[]) {
    const newList = list ? list : [...dataSourceRef.current!]
    newList.map(item => {
      if (item.index === data.index) {
        item.isEdit = true
      } else {
        item.isEdit = false
      }
    })
    return newList
  }

  function enterEdit(t: IRow) {
    dataSourceRef.current = changeCurrentEdit(t)
    setRefresh(new Date().getTime())
  }

  function onChangeDataSource<Key extends keyof IRow>(type: Key, value: IRow[Key], rowData: IRow) {
    const newList = [...dataSourceRef.current!]
    newList.map((t: IRow, i) => {
      if (t.index === rowData.index) {
        t[type] = value
      }
    })
    dataSourceRef.current = newList
    setRefresh(new Date().getTime())
  }

  function deleteRow(data: IRow) {
    const newList = [...dataSourceRef.current!]
    newList.map((t: IRow, i) => {
      if (t.index === data.index) {
        newList.splice(i, 1)
      }
    })
    dataSourceRef.current = newList
    setRefresh(new Date().getTime())
  }

  function renderSelete(rowData: IRow) {

    const onChange = (value: string) => {
      onChangeDataSource('type', value, rowData)
    };

    const onSearch = (value: string) => {
      if (!value) return
      const newList = [...mysqlDataTypeOptionsRef.current!]
      if (newList[newList.length - 1].type === 'custom') {
        newList[newList.length - 1] = {
          label: value,
          value: value,
          type: 'custom'
        }
      } else {
        newList.push({
          label: value,
          value: value,
          type: 'custom'
        })
      }
      mysqlDataTypeOptionsRef.current = newList
      setRefresh(new Date().getTime())
    };

    function onFocus() {
      let flag = false
      mysqlDataTypeOptionsRef.current?.map(item => {
        if (rowData.type === item.value) {
          flag = true
        }
      })
      if (!flag) {
        const newList = [...mysqlDataTypeOptionsRef.current!]
        newList[newList.length - 1] = {
          label: rowData.type,
          value: rowData.type,
          type: 'custom'
        }
        mysqlDataTypeOptionsRef.current = newList
        setRefresh(new Date().getTime())
      }
    }

    return <Select
      className={styles.select}
      showSearch
      placeholder="请选择"
      onFocus={onFocus}
      onChange={onChange}
      onSearch={onSearch}
      value={rowData.type}
      options={mysqlDataTypeOptionsRef.current}
    />
  }

  const { Header: TableHeader, Row: TableRow } = useOnlyOnceTask(() => {
    return createTableRows<IRow>([
      {
        name: '拖动', baseWidth: 50, renderCell: (t: IRow, i) => renderDrag(t, i)
      },
      {
        name: '序号', baseWidth: 50, renderCell: (t, i) => <div>{i + 1}</div>
      },
      {
        name: '状态', baseWidth: 80, renderCell: t => <div>{t.state}</div>
      },
      {
        name: '列名', flex: 1, renderCell: t => {
          return t.isEdit ?
            <Input onChange={(value) => { onChangeDataSource('columnName', value.target.value, t) }} value={t.columnName}></Input>
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.columnName}</div>
        }
      },
      {
        name: '类型', flex: 1, renderCell: t => {
          return t.isEdit ?
            renderSelete(t)
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.type}</div>
        }
      },
      {
        name: '长度', baseWidth: 100, renderCell: t => {
          return t.isEdit ?
            <Input onChange={(value) => { onChangeDataSource('length', value.target.value, t) }} value={t.length}></Input>
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.length}</div>
        }
      },
      {
        name: '可空', baseWidth: 80, renderCell: t => <div><Checkbox value={t.unNull} onChange={(value) => { onChangeDataSource('unNull', value.target.value, t) }}></Checkbox></div>
      },
      // {
      //   name: '注释', flex: 1, renderCell: t => {
      //     return t.isEdit ?
      //       <Input onChange={(value) => { onChangeDataSource('comment', value.target.value, t) }} value={t.comment}></Input>
      //       :
      //       <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.comment}</div>
      //   }
      // },
      {
        name: '操作', baseWidth: 60, renderCell: t => <div>
          <div onClick={deleteRow.bind(null, t)} className={styles.deleteButton}>删除</div>
        </div>
      },
    ])
  });

  function moveStyle(index: number) {
    if (index === dragIndex) {
      return { transform: `translateY(${currentDragMovePx}px)` }
    } else if ((index > dragIndex!) && (dragedIndex! >= index)) {
      return { transform: `translateY(-${CategoryLineHeight}px)`, }
    } else if ((index < dragIndex!) && (dragedIndex! <= index)) {
      return { transform: `translateY(${CategoryLineHeight}px)` }
    } else {
      return { transform: `translateY(0px)` }
    }
  }

  function addData() {
    const newList = [...dataSourceRef.current!]
    let newRow = createDefaultColumn()
    newList.push(newRow)
    dataSourceRef.current = changeCurrentEdit(newRow, newList)
    setRefresh(new Date().getTime())
    setTimeout(() => {
      scrollPage(999999999, scrollBoxRef.current) //TODO:
    }, 0);
  }

  return <div className={classnames(className, styles.box)}>
    <TableHeader className={styles.tableHeader}></TableHeader>
    <div className={styles.scrollBox} ref={scrollBoxRef}>
      <div className={classnames(styles.tableMain, { [styles.dragging]: dragIndex !== undefined })}>
        {
          dataSourceRef.current?.map((t, index) => {
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
      <Button className={styles.addDataButton} onClick={addData}>
        <Iconfont code="&#xe631;" />
        新增
      </Button>
    </div>
    {/* <Table columns={[
      {
        name: '拖动', baseWidth: 50, drag: true, renderCell: (t: IRow, i) => renderDrag(t, i)
      },
      {
        name: '序号', baseWidth: 50, renderCell: (t, i) => <div>{i + 1}</div>
      },
      {
        name: '状态', baseWidth: 80, renderCell: t => <div>{t.state}</div>
      },
      {
        name: '列名', flex: 1, renderCell: t => {
          return t.isEdit ?
            <Input onChange={(value) => { onChangeDataSource('columnName', value.target.value, t) }} value={t.columnName}></Input>
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.columnName}</div>
        }
      },
      {
        name: '类型', flex: 1, renderCell: t => {
          return t.isEdit ?
            renderSelete(t)
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.type}</div>
        }
      },
      {
        name: '长度', baseWidth: 100, renderCell: t => {
          return t.isEdit ?
            <Input onChange={(value) => { onChangeDataSource('length', value.target.value, t) }} value={t.length}></Input>
            :
            <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.length}</div>
        }
      },
      {
        name: '可空', baseWidth: 80, renderCell: t => <div><Checkbox value={t.unNull} onChange={(value) => { onChangeDataSource('unNull', value.target.value, t) }}></Checkbox></div>
      },
      // {
      //   name: '注释', flex: 1, renderCell: t => {
      //     return t.isEdit ?
      //       <Input onChange={(value) => { onChangeDataSource('comment', value.target.value, t) }} value={t.comment}></Input>
      //       :
      //       <div onClick={enterEdit.bind(null, t)} className={styles.cellContent}>{t.comment}</div>
      //   }
      // },
      {
        name: '操作', baseWidth: 60, renderCell: t => <div>
          <div onClick={deleteRow.bind(null, t)} className={styles.deleteButton}>删除</div>
        </div>
      },
    ]} data={dataSourceRef.current}></Table> */}
    <Expand></Expand>
  </div >
})

const basicInfo = {
  data: {}
}

export function Expand() {
  const [form] = Form.useForm();

  function onChangeForm() {
    basicInfo.data = {
      ...form.getFieldsValue()
    }
  }
  return <div className={styles.expandBox}>
    <div className={styles.title}>扩展属性</div>
    <Form
      form={form}
      initialValues={{ remember: true }}
      autoComplete="off"
      className={styles.form}
    >
      <Form.Item
        label="默认值"
        name="name"
      >
        <Input onChange={() => { onChangeForm() }} />
      </Form.Item>
      <Form.Item
        label="注释"
        name="comment"
      >
        <Input onChange={() => { onChangeForm() }} />
      </Form.Item>
      <Form.Item
        label="自动增长"
        name="comment"
      >
        <Checkbox onChange={() => { onChangeForm() }}></Checkbox>
      </Form.Item>
    </Form>
  </div>
}