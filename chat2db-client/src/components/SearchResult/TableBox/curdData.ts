import lodash from 'lodash';
import { CRUD } from '@/constants';
import { USER_FILLED_VALUE, IUpdateData } from './index';

export interface IProps {
  preCode: string;
  // 
  tableData: { [key: string]: string | null }[];
  setTableData: (tableData:  { [key: string]: string | null }[]) => void;
  // 
  editingCell: [string, string, boolean] | null;
  setEditingCell: (editingCell: [string, string, boolean] | null) => void;
  // 
  updateData: IUpdateData[];
  setUpdateData: (updateData: IUpdateData[]) => void;
  // 
  curOperationRowNo: string | null;
  setCurOperationRowNo: (curOperationRowNo: string | null)=>void;
  // 
  columns;
  oldDataList;
  queryResultData;
  tableBoxRef;
  oldTableData
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
    oldTableData
  } = props;

  // 编辑数据
  const updateTableData = (type: 'setCell' | 'setRow', _data: string | null | Array<string | null>) => {
    const newTableData = lodash.cloneDeep(tableData);
    let oldRowDataList: Array<string | null> = [];
    let newRowDataList: Array<string | null> = [];
    let curRowNo: string | null = '0';
    if (type === 'setCell' && (typeof _data === 'string' || _data === null)) {
      const [colId, rowId] = editingCell!;
      curRowNo = rowId;
      newTableData.forEach((item) => {
        if (item[`${preCode}0No.`] === rowId) {
          item[colId] = _data;
          newRowDataList = Object.keys(item).map((i) => item[i]);
        }
      });
    }

    if (type === 'setRow' && Array.isArray(_data)) {
      curRowNo = curOperationRowNo;
      _data.unshift(curOperationRowNo);
      newTableData.forEach((t) => {
        if (t[`${preCode}0No.`] === curOperationRowNo) {
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
  const handleCreateData = (_newData?: any) => {
    // 正常的新增
    const newTableData = lodash.cloneDeep(tableData);
    let newData = {};
    if (_newData) {
      newData = _newData;
    } else {
      columns.forEach((t, i) => {
        if (t.name === 'No.') {
          newData[`${preCode}${i}${t.name}`] = (newTableData.length + 1).toString();
        } else {
          // 判断是否有默认值
          const hasDefaultValue =
            queryResultData.headerList.find((item) => item.name === t.name)?.defaultValue !== null;
          if (hasDefaultValue) {
            newData[`${preCode}${i}${t.name}`] = USER_FILLED_VALUE.DEFAULT;
            return;
          }
          newData[`${preCode}${i}${t.name}`] = null;
        }
      });
    }
    newTableData.push(newData);
    setTableData(newTableData);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.CREATE,
        dataList: Object.keys(newData).map((item) => newData[item]),
        rowId: newTableData.length.toString(),
      },
    ]);
    setCurOperationRowNo(newTableData.length.toString());
    setEditingCell(null);

    // 新增一条数据，tableBox需要滚动到最下方
    setTimeout(() => {
      tableBoxRef.current?.scrollTo(0, tableBoxRef.current?.scrollHeight + 31);
    }, 0);
  };

  // 处理删除数据
  const handleDeleteData = () => {
    const rowId = curOperationRowNo || editingCell?.[1];
    if (rowId === null) {
      return;
    }
    // 如果是新增的行，则直接删除
    const index = updateData.findIndex((item) => item.rowId === rowId && item.type === CRUD.CREATE);
    if (index !== -1) {
      updateData.splice(index, 1);
      setUpdateData([...updateData]);
      setTableData(tableData.filter((item) => item[`${preCode}0No.`] !== rowId));
      setCurOperationRowNo(null);
      return;
    }

    // 正常的删除数据
    const deleteIndex = updateData.findIndex((t) => t.rowId === rowId);
    if (deleteIndex !== -1) {
      updateData.splice(deleteIndex, 1);
    }

    // 如果删除的这个数据时编辑过的，要把这个数据恢复
    setTableData(
      tableData.map((item) =>
        item[`${preCode}0No.`] === rowId ? oldTableData.find((i) => i[`${preCode}0No.`] === rowId)! : item,
      ),
    );
    const newDataOldList = oldDataList.find((item) => item[0] === rowId);
    setUpdateData([
      ...updateData,
      {
        type: CRUD.DELETE,
        oldDataList: newDataOldList,
        rowId: rowId!,
      },
    ]);
    setEditingCell(null);
    setCurOperationRowNo(null);
  };

  return {
    updateTableData,
    handleCreateData,
    handleDeleteData
  };
};

export default useCurdTableData;
