import {useState, useEffect} from 'react';
import sqlService from '@/service/sql';
import {IBoundInfo} from '../index'

interface IProps {
  boundInfo: IBoundInfo;
}

export const useModuleData = (props: IProps) => {
  const { boundInfo } = props;
  const [selectedTables, setSelectedTables] = useState<string[]>([]);
  const [tableNameList, setTableNameList] = useState<string[]>([]);

  useEffect(() => {
    const { dataSourceId, databaseName, schemaName } = boundInfo;

    if( !databaseName && !schemaName){
      setTableNameList([]);
      setSelectedTables([]);
      return
    }

    sqlService
      .getAllTableList({
        dataSourceId,
        databaseName,
        schemaName,
      })
      .then((data) => {
        const tableNameListTemp = data.map((t) => t.name);
        setTableNameList(tableNameListTemp);

        if (selectedTables.length === 0) {
          setSelectedTables(tableNameListTemp.slice(0, 1));
        }
      });
  }, []);

  return {
    selectedTables,
    setSelectedTables,
    tableNameList,
    setTableNameList
  }
}
