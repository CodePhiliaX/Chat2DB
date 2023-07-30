import React, { memo, useRef, useState, useEffect } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import DraggableContainer from '@/components/DraggableContainer';
import Console, { IAppendValue } from '@/components/Console';
import SearchResult from '@/components/SearchResult';
import { DatabaseTypeCode, ConsoleStatus } from '@/constants';
import { IManageResultData, IResultConfig } from '@/typings';
import { IWorkspaceModelState } from '@/models/workspace';
import historyServer, { IGetSavedListParams, ISaveBasicInfo } from '@/service/history';
import { IAIState } from '@/models/ai';
import sqlServer, { IExecuteSqlParams, IExportParams } from '@/service/sql';
import { v4 as uuidV4 } from 'uuid';
import sql from '@/service/sql';
import { isNumber } from 'lodash';
import { ExportSizeEnum, ExportTypeEnum } from '@/typings/resultTable';
import { downloadFile } from '@/utils/common';
interface IProps {
  className?: string;
  isActive: boolean;
  workspaceModel: IWorkspaceModelState;
  aiModel: IAIState;
  dispatch: any;
  data: {
    databaseName: string;
    dataSourceId: number;
    type: DatabaseTypeCode;
    schemaName: string;
    consoleId: number;
    consoleName: string;
    initDDL: string;
  };
}

const defaultResultConfig: IResultConfig = {
  pageNo: 1,
  pageSize: 200,
  total: 0,
  hasNextPage: true,
};

const WorkspaceRightItem = memo<IProps>(function (props) {
  const { data, workspaceModel, aiModel, isActive, dispatch } = props;
  const draggableRef = useRef<any>();
  const [appendValue, setAppendValue] = useState<IAppendValue>();
  const [resultData, setResultData] = useState<IManageResultData[]>([]);
  const [resultConfig, setResultConfig] = useState<IResultConfig[]>([]);

  const { doubleClickTreeNodeData, curTableList, curWorkspaceParams } = workspaceModel;
  const [tableLoading, setTableLoading] = useState(false);

  useEffect(() => {
    if (!doubleClickTreeNodeData) {
      return;
    }
    const { extraParams } = doubleClickTreeNodeData;
    const { tableName } = extraParams || {};
    const ddl = `SELECT * FROM ${tableName};\n`;
    if (isActive) {
      setAppendValue({ text: ddl });
    }
    dispatch({
      type: 'workspace/setDoubleClickTreeNodeData',
      payload: '',
    });
  }, [doubleClickTreeNodeData]);

  /**
   * 执行SQL
   * @param sql
   */
  const handleExecuteSQL = async (sql: string) => {
    setTableLoading(true);

    let executeSQLParams: IExecuteSqlParams = {
      sql,
      ...defaultResultConfig,
      ...data,
    };

    // 获取当前SQL的查询结果
    let sqlResult = await sqlServer.executeSql(executeSQLParams);
    sqlResult = sqlResult.map((res) => ({
      ...res,
      uuid: uuidV4(),
    }));

    // 获取当前SQL的总条数
    // let reqDMLCountPromiseArr: Array<Promise<any>> = [];
    // (sqlResult || []).forEach((res) => {
    //   const { originalSql } = res;
    //   let p = sqlServer.getDMLCount({ ...executeSQLParams, sql: originalSql });
    //   reqDMLCountPromiseArr.push(p);
    // });
    // let reqDMLCountArr = await Promise.all(reqDMLCountPromiseArr);

    setResultConfig(
      sqlResult.map((res) => ({ ...defaultResultConfig, total: res.fuzzyTotal, hasNextPage: res.hasNextPage })),
    );
    setResultData(sqlResult);
    setTableLoading(false);

    let createHistoryParams: ISaveBasicInfo = {
      ...data,
      ddl: sql,
      name: `${new Date()}-${sql}`,
    };
    historyServer.createHistory(createHistoryParams);
  };

  /**
   * 因为 pageNo、pageSize等信息导致的
   * 单条SQL执行
   */
  const handleExecuteSQLbyConfigChanged = async (sql: string, config: IResultConfig, index: number) => {
    setTableLoading(true);
    const param = { ...data, ...config, sql };
    let sqlResult = await sqlServer.executeSql(param);
    resultData[index] = { ...resultData[index], ...sqlResult[0] };
    setResultData([...resultData]);

    resultConfig[index] = {
      ...config,
      total: isNumber(resultConfig[index].total) ? resultConfig[index].total : sqlResult[0].fuzzyTotal,
      hasNextPage: sqlResult[0].hasNextPage,
    };
    setResultConfig([...resultConfig]);
    setTableLoading(false);
  };

  const handleSearchTotal = async (index: number) => {
    const { originalSql } = resultData[index];
    let total = await sqlServer.getDMLCount({ ...data, sql: originalSql });
    resultConfig[index] = {
      ...resultConfig[index],
      total,
    };
    setResultConfig([...resultConfig]);
    return total;
  };

  const handleExportSQLResult = async (
    sql: string,
    originalSql: string,
    exportType: ExportTypeEnum,
    exportSize: ExportSizeEnum,
  ) => {
    const params: IExportParams = { ...data, sql, originalSql, exportType, exportSize };
    downloadFile(window._BaseURL + '/api/rdb/dml/export', params);
  };

  const handleResultTabEdit = (type: 'add' | 'remove', uuid?: string | number) => {
    if (type === 'remove') {
      const tabIndex = resultData.findIndex((d) => d.uuid === uuid);
      resultData.splice(tabIndex, 1);
      resultConfig.splice(tabIndex, 1);
      setResultData([...resultData]);
      setResultConfig([...resultConfig]);
    }
  };

  return (
    <div className={classnames(styles.box)}>
      <DraggableContainer layout="column" className={styles.boxRightCenter}>
        <div ref={draggableRef} className={styles.boxRightConsole}>
          <Console
            source="workspace"
            defaultValue={data.initDDL}
            isActive={isActive}
            appendValue={appendValue}
            executeParams={{ ...data }}
            hasAiChat={true}
            hasAi2Lang={true}
            onExecuteSQL={handleExecuteSQL}
            onConsoleSave={() => {
              dispatch({
                type: 'workspace/fetchGetSavedConsole',
                payload: {
                  status: ConsoleStatus.RELEASE,
                  orderByDesc: true,
                  ...curWorkspaceParams,
                },
                callback: (res: any) => {
                  dispatch({
                    type: 'workspace/setConsoleList',
                    payload: res.data,
                  });
                },
              });
            }}
            tables={curTableList || []}
            remainingUse={aiModel.remainingUse}
          />
        </div>
        <div className={styles.boxRightResult}>
          {
            <SearchResult
              onTabEdit={handleResultTabEdit}
              onExecute={handleExecuteSQLbyConfigChanged}
              onSearchTotal={handleSearchTotal}
              onExport={handleExportSQLResult}
              manageResultDataList={resultData}
              resultConfig={resultConfig}
              isLoading={tableLoading}
            />
          }
        </div>
      </DraggableContainer>
    </div>
  );
});

export default WorkspaceRightItem;
