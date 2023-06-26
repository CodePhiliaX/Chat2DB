export enum DatabaseTypeCode {
  MYSQL = 'MYSQL',
  ORACLE = 'ORACLE',
  DB2 = 'DB2',
  MONGODB = 'MONGODB',
  REDIS = 'REDIS',
  H2 = 'H2',
  POSTGRESQL = 'POSTGRESQL',
  SQLSERVER = 'SQLSERVER',
  SQLITE = 'SQLITE',
  MARIADB = 'MARIADB',
  CLICKHOUSE = 'CLICKHOUSE',
  DM = "DM",
}

export enum ConsoleOpenedStatus {
  IS_OPEN = 'y',
  NOT_OPEN = 'n',
}

export enum ConsoleStatus {
  DRAFT = 'DRAFT',
  RELEASE = 'RELEASE',
}

/** console顶部注释 */
export const consoleTopComment = `-- Chat2DB自然语言转SQL等AI功能 >> https://github.com/alibaba/Chat2DB/blob/main/CHAT2DB_AI_SQL.md\n\n`;