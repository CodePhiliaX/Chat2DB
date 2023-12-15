import lodash from 'lodash';
import { CRUD } from '@/constants';
import { USER_FILLED_VALUE, IUpdateData } from '../components/TableBox/index';

export interface IProps {
  preCode: string;
  //
  tableData: { [key: string]: string | null }[];
  setTableData: (tableData: { [key: string]: string | null }[]) => void;
  //
  editingCell: [string, string, boolean] | null;
  setEditingCell: (editingCell: [string, string, boolean] | null) => void;
  //
  updateData: IUpdateData[];
  setUpdateData: (updateData: IUpdateData[]) => void;
  //
  curOperationRowNo: Array<string> | null;
  setCurOperationRowNo: (curOperationRowNo: Array<string> | null) => void;
  //
  columns;
  oldDataList;
  queryResultData;
  tableBoxRef;
  oldTableData;
  colNoCode;
}

const useCurdTableData = (props: IProps) => {
  const {
    tableData,
    setTableData,
    preCode,
    editingCell,
    columns,
    curOperationRowNo,
    oldDataList,
    updateData,
    setUpdateData,
    queryResultData,
    setCurOperationRowNo,
    setEditingCell,
    tableBoxRef,
    oldTableData,
    colNoCode,
  } = props;

  // 编辑数据
  const updateTableData = (type: 'setCell' | 'setRow', _data: string | null | Array<string | null>) => {
    const newTableData = lodash.cloneDeep(tableData);
    let oldRowDataList: Array<string | null> = [];
    let newRowDataList: Array<string | null> = [];
    let curRowNo: string | null = null;
    if (type === 'setCell' && (typeof _data === 'string' || _data === null)) {
      const [colId, rowId] = editingCell!;
      curRowNo = rowId;
      newTableData.forEach((item) => {
        if (item[colNoCode] === rowId) {
          item[colId] = _data;
          newRowDataList = Object.keys(item).map((i) => item[i]);
        }
      });
    }

    if (type === 'setRow' && Array.isArray(_data) && curOperationRowNo) {
      curRowNo = curOperationRowNo[0];
      _data.unshift(curOperationRowNo[0]);
      newTableData.forEach((t) => {
        if (t[colNoCode] === curOperationRowNo[0]) {
          const dataLength = Object.keys(t).length;
          Object.keys(t).forEach((item, index) => {
            if (index > dataLength) return;
            t[item] = _data[index] || null;
          });
          return;
        }
      });
      newRowDataList = _data;
    }

    setTableData(newTableData);

    oldDataList.forEach((item) => {
      if (item[0] === curRowNo) {
        oldRowDataList = item;
      }
    });

    const index = updateData.findIndex((item) => item.rowId === curRowNo);
    // 如果newRowDataList和oldRowDataList的数据一样，代表用户虽然编辑过，但是又改回去了，则不需要更新
    if (oldRowDataList?.join(',') === newRowDataList?.join(',')) {
      if (index !== -1) {
        setUpdateData(updateData.filter((item) => item.rowId !== curRowNo && item.type !== CRUD.UPDATE));
      }
      return;
    }

    if (index === -1) {
      setUpdateData([
        ...updateData,
        {
          type: CRUD.UPDATE,
          oldDataList: oldRowDataList,
          dataList: newRowDataList,
          rowId: curRowNo!,
        },
      ]);
      return;
    }

    const newRowUpdateData = {
      ...updateData[index],
      dataList: newRowDataList,
    };

    // 如果是删除过的，则需要把type改为update
    if (newRowUpdateData.type === CRUD.DELETE) {
      newRowUpdateData.type = CRUD.UPDATE;
    }

    updateData[index] = newRowUpdateData;
    setUpdateData([...updateData]);
  };

  // 处理创建数据
  const handleCreateData = (_newData?: { [key in string]: any }[]) => {
    // 正常的新增
    const newTableData = lodash.cloneDeep(tableData);
    let newData: { [key in string]: any }[] = [{}];
    if (_newData) {
      newData = _newData;
    } else {
      newData.forEach((newDataItem, index) => {
        columns.forEach((t, i) => {
          if (t.name === 'No.') {
            newDataItem[`${preCode}${i}${t.name}`] = (newTableData.length + index + 1).toString();
          } else {
            // 判断是否有默认值
            const hasDefaultValue =
              queryResultData.headerList.find((item) => item.name === t.name)?.defaultValue !== null;
            if (hasDefaultValue) {
              newDataItem[`${preCode}${i}${t.name}`] = USER_FILLED_VALUE.DEFAULT;
              return;
            }
            newDataItem[`${preCode}${i}${t.name}`] = null;
          }
        });
      });
    }

    setTableData(newTableData.concat(newData));

    const newUpdateData = newData.map((item, index) => {
      return {
        type: CRUD.CREATE,
        dataList: Object.keys(item).map((i) => item[i]),
        rowId: (newTableData.length + index + 1).toString(),
      };
    });

    setUpdateData([...updateData, ...newUpdateData]);

    setCurOperationRowNo([(newTableData.length + 1).toString()]);
    setEditingCell(null);

    // 新增一条数据，tableBox需要滚动到最下方
    setTimeout(() => {
      tableBoxRef.current?.scrollTo(0, tableBoxRef.current?.scrollHeight + 31);
    }, 0);
  };

  // 处理删除数据
  const handleDeleteData = () => {
    if (!curOperationRowNo && !editingCell) {
      return;
    }

    const rowIds = curOperationRowNo || [editingCell![1]];
    const needDeleteNewData:any = [];
    const needRecoverData:any = [];
    const needDeleteData:any = [];
    let _updateData = lodash.cloneDeep(updateData);
    let _tableData = lodash.cloneDeep(tableData);


    rowIds.forEach((rowId: string) => {
      let flag = false
      _updateData.forEach((item) => {
        if (item.rowId === rowId) {
          if (item.type === CRUD.CREATE) {
            flag = true
            needDeleteNewData.push(rowId);
          } else if(item.type === CRUD.UPDATE){
            flag = true
            needRecoverData.push(rowId);
          }
        }
      });
      if(!flag) {
        needDeleteData.push(rowId);
      }
    });

    // 删除新增的行
    needDeleteNewData.forEach((rowId) => {
      _updateData = _updateData.filter((item) => item.rowId !== rowId);
      _tableData = _tableData.filter((item) => item[colNoCode] !== rowId);
    });

    // 删除编辑的行，需要把这一行的数据恢复
    needRecoverData.forEach((rowId) => {
      const oldRowDataList = _updateData.find((item) => item.rowId === rowId)?.oldDataList;

      _tableData = _tableData.map((item) => {
        if (item[colNoCode] === rowId) {
          return oldTableData.find((i) => item[colNoCode] === i[colNoCode])!;
        }
        return item;
      })

      const index = _updateData.findIndex((item) => item.rowId === rowId);
      _updateData[index] = {
        ..._updateData[index],
        type: CRUD.DELETE,
        oldDataList: oldRowDataList,
      };
    });

    needDeleteData.forEach((rowId) => {
      const oldRowData = oldDataList.find((item) => item[0] === rowId);
      _updateData.push({
        type: CRUD.DELETE,
        oldDataList: Object.keys(oldRowData!).map((i) => oldRowData![i]),
        rowId,
      })
    });

    setUpdateData(_updateData);
    setTableData(_tableData);
    setEditingCell(null);
    setCurOperationRowNo(null);
  };

  return {
    updateTableData,
    handleCreateData,
    handleDeleteData,
  };
};

export default useCurdTableData;
