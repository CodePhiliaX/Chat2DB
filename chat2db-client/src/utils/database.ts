import { DatabaseTypeCode } from '@/constants/common';
import { IWorkspaceModelType } from '@/models/workspace';
import { Option } from '@/typings/common';

export function handleDatabaseAndSchema(databaseAndSchema: IWorkspaceModelType['state']['databaseAndSchema']) {
  let newCascaderOptions: Option[] = [];
  if (databaseAndSchema.databases) {
    newCascaderOptions = (databaseAndSchema?.databases || []).map((t) => {
      let schemasList: Option[] = [];
      if (t.schemas) {
        schemasList = t.schemas.map((t) => {
          return {
            value: t.name,
            label: t.name,
            type: 'schema',
            isLeaf: true,
          };
        });
      }
      return {
        value: t.name,
        label: t.name,
        type: 'database',
        children: schemasList,
        isLeaf: schemasList.length === 0,
      };
    });
  } else if (databaseAndSchema?.schemas) {
    newCascaderOptions = (databaseAndSchema?.schemas || []).map((t) => {
      return {
        value: t.name,
        label: t.name,
        type: 'schema',
        isLeaf: true,
      };
    });
  }
  return newCascaderOptions;
}

/**
 * 兼容处理数据库名称
 * @param databaseName
 * @param databaseType
 * @returns
 */
export function compatibleDataBaseName(databaseName: string, databaseType: DatabaseTypeCode) {
  
  //""  oracele  sqlite postgrsql  h2 dm
  // ` MYSQL clickhouse MariaDB
  // [ sqlserver
  if (
    [
      DatabaseTypeCode.ORACLE,
      DatabaseTypeCode.SQLITE,
      DatabaseTypeCode.POSTGRESQL,
      DatabaseTypeCode.H2,
      DatabaseTypeCode.DB2,
      DatabaseTypeCode.KINGBASE,
      DatabaseTypeCode.DM,
    ].includes(databaseType)
  ) {
    return `"${databaseName}"`;
  } else if ([DatabaseTypeCode.SQLSERVER].includes(databaseType)) {
    return `[${databaseName}]`;
  } else if ([DatabaseTypeCode.MYSQL, DatabaseTypeCode.CLICKHOUSE, DatabaseTypeCode.MARIADB].includes(databaseType)) {
    return `\`${databaseName}\``;
  } else {
    return `${databaseName}`;
  }
}
