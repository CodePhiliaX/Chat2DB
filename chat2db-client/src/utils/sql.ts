import { DatabaseTypeCode } from '@/constants';
import sqlServer from '@/service/sql';
import { format } from 'sql-formatter';

/**
 * 格式化sql
 */
export function formatSql(sql: string, dbType: DatabaseTypeCode) {
  const arr = [
    'bigquery',
    'db2',
    'hive',
    'mariadb',
    'mysql',
    'n1ql',
    'plsql',
    'postgresql',
    'redshift',
    'spark',
    'sqlite',
    'sql',
    'trino',
    'transactsql',
    'singlestoredb',
    'snowflake',
  ];
  const language = arr.includes(dbType.toLowerCase()) ? dbType.toLowerCase() : 'sql';
  return new Promise((r: (sql: string) => void) => {
    let formatRes = '';
    // debugger;
    try {
      formatRes = format(sql || '', { language });
      // formatRes = '';
    } catch (e) {
      console.log('Frontend format sql error', e);
    }
    // // 如果格式化失败，直接返回原始sql
    if (!formatRes) {
      sqlServer
        .sqlFormat({
          sql,
          dbType,
        })
        .then((res) => {
          formatRes = res;
          r(formatRes);
        });
    } else {
      r(formatRes);
    }
  });
}
