import { DatabaseTypeCode } from '@/constants';
import mysql from '@/constants/IntelliSense/mysql';
import oracle from '@/constants/IntelliSense/oracle';
import postgresql from '@/constants/IntelliSense/pgsql';
import redis from '@/constants/IntelliSense/redis';
import sqlserver from '@/constants/IntelliSense/sqlserver';
import { ILegacyDialectCompletion, ISqlDialectCompletion } from './types';

const genericSqlCompletion: ISqlDialectCompletion = {
  type: 'GENERIC',
  keywords: [
    'SELECT',
    'FROM',
    'WHERE',
    'JOIN',
    'LEFT JOIN',
    'RIGHT JOIN',
    'INNER JOIN',
    'GROUP BY',
    'ORDER BY',
    'HAVING',
    'LIMIT',
    'AND',
    'OR',
    'NOT',
    'IN',
    'EXISTS',
    'BETWEEN',
    'LIKE',
    'AS',
    'DISTINCT',
    'NULL',
    'IS NULL',
    'IS NOT NULL',
    'ASC',
    'DESC',
    'UNION',
    'UNION ALL',
    'CASE',
    'WHEN',
    'THEN',
    'ELSE',
    'END',
    'WITH',
  ],
  functions: [
    'COUNT',
    'SUM',
    'AVG',
    'MAX',
    'MIN',
    'CAST',
    'CONVERT',
    'TRIM',
    'SUBSTRING',
    'LENGTH',
    'UPPER',
    'LOWER',
    'CONCAT',
    'REPLACE',
    'COALESCE',
    'NULLIF',
    'ABS',
    'CEIL',
    'FLOOR',
    'ROUND',
    'NOW',
    'CURRENT_DATE',
    'CURRENT_TIME',
    'CURRENT_TIMESTAMP',
  ],
  dataTypes: [],
  variables: [],
  operators: [],
};

const legacyDialectCompletions = [mysql, postgresql, oracle, sqlserver, redis] as ILegacyDialectCompletion[];

const mysqlCompatibleTypes = new Set<string>([
  DatabaseTypeCode.MYSQL,
  DatabaseTypeCode.MARIADB,
  DatabaseTypeCode.H2,
  DatabaseTypeCode.OCEANBASE,
  'TIDB',
]);

const postgresqlCompatibleTypes = new Set<string>([DatabaseTypeCode.POSTGRESQL, DatabaseTypeCode.KINGBASE, 'GAUSS']);

const sqlserverCompatibleTypes = new Set<string>([DatabaseTypeCode.SQLSERVER, 'SYBASE']);

const oracleCompatibleTypes = new Set<string>([DatabaseTypeCode.ORACLE, DatabaseTypeCode.DM, 'DAMENG']);

const normalizeWords = (words?: string[]) => {
  const normalizedMap = new Map<string, string>();

  (words || []).forEach((word) => {
    const trimmedWord = word?.trim();
    if (!trimmedWord) {
      return;
    }

    const normalizedKey = trimmedWord.toUpperCase();
    if (!normalizedMap.has(normalizedKey)) {
      normalizedMap.set(normalizedKey, trimmedWord);
    }
  });

  return Array.from(normalizedMap.values());
};

const normalizeLegacyCompletion = (completion: ILegacyDialectCompletion): ISqlDialectCompletion => {
  return {
    type: completion.type,
    keywords: normalizeWords(completion.keywords),
    functions: normalizeWords(completion.functions),
    dataTypes: [],
    variables: [],
    operators: [],
  };
};

const findLegacyCompletion = (databaseType?: string) => {
  const normalizedType = databaseType?.toUpperCase();

  if (mysqlCompatibleTypes.has(normalizedType || '')) {
    return mysql;
  }

  if (postgresqlCompatibleTypes.has(normalizedType || '')) {
    return postgresql;
  }

  if (sqlserverCompatibleTypes.has(normalizedType || '')) {
    return sqlserver;
  }

  if (oracleCompatibleTypes.has(normalizedType || '')) {
    return oracle;
  }

  return legacyDialectCompletions.find((completion) => completion.type === normalizedType);
};

export const getDialectCompletion = (databaseType?: DatabaseTypeCode | string): ISqlDialectCompletion => {
  const legacyCompletion = findLegacyCompletion(databaseType);

  if (legacyCompletion) {
    return normalizeLegacyCompletion(legacyCompletion);
  }

  return genericSqlCompletion;
};
