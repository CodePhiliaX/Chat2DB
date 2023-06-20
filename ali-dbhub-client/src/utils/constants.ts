import mysqlLogo from '@/assets/mysql-logo.png';
import redisLogo from '@/assets/redis-logo.png';
import h2Logo from '@/assets/h2-logo.png';
import moreDBLogo from '@/assets/moreDB-logo.png';
import { IDatabase } from '@/types';

export enum ThemeType {
  default = 'default',
  dark = 'dark',
}

export enum StatusType {
  SUCCESS = 'success',
  FAIL = 'fail',
}

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
  DM="DM",
}

export const databaseType: {
  [keys: string]: IDatabase;
} = {
  [DatabaseTypeCode.MYSQL]: {
    name: 'MySQL',
    img: mysqlLogo,
    code: DatabaseTypeCode.MYSQL,
    port: 3306,
    icon: '\uec6d',
  },
  [DatabaseTypeCode.H2]: {
    name: 'H2',
    img: h2Logo,
    code: DatabaseTypeCode.H2,
    port: 9092,
    icon: '\ue61c',
  },
  [DatabaseTypeCode.ORACLE]: {
    name: 'Oracle',
    img: moreDBLogo,
    code: DatabaseTypeCode.ORACLE,
    port: 1521,
    icon: '\uec48',
  },
  [DatabaseTypeCode.POSTGRESQL]: {
    name: 'PostgreSql',
    img: moreDBLogo,
    code: DatabaseTypeCode.POSTGRESQL,
    port: 5432,
    icon: '\uec5d',
  },
  [DatabaseTypeCode.SQLSERVER]: {
    name: 'SQLServer',
    img: moreDBLogo,
    code: DatabaseTypeCode.SQLSERVER,
    port: 1521,
    icon: '\ue664',
  },
  [DatabaseTypeCode.SQLITE]: {
    name: 'SQLite',
    img: moreDBLogo,
    code: DatabaseTypeCode.SQLITE,
    port: 5432,
    icon: '\ue65a',
  },
  [DatabaseTypeCode.MARIADB]: {
    name: 'Mariadb',
    img: moreDBLogo,
    code: DatabaseTypeCode.MARIADB,
    port: 3306,
    icon: '\ue6f5',
  },
  [DatabaseTypeCode.CLICKHOUSE]: {
    name: 'ClickHouse',
    img: moreDBLogo,
    code: DatabaseTypeCode.CLICKHOUSE,
    port: 8123,
    icon: '\ue8f4',
  },
  [DatabaseTypeCode.DM]: {
    name: 'DM',
    img: moreDBLogo,
    code: DatabaseTypeCode.DM,
    port: 5236,
    icon: '\ue655',
  },
};

export const databaseTypeList = Object.keys(databaseType).map((keys) => {
  return databaseType[keys];
});

export enum TreeNodeType {
  DATASOURCES = 'dataSources',
  DATASOURCE = 'dataSource',
  DATABASE = 'database',
  SCHEMAS = 'schemas',
  TABLES = 'tables',
  TABLE = 'table',
  COLUMNS = 'columns',
  COLUMN = 'column',
  KEYS = 'keys',
  KEY = 'key',
  INDEXES = 'indexes',
  INDEX = 'index',
}

export enum WindowTabStatus {
  DRAFT = 'DRAFT',
  RELEASE = 'RELEASE',
}

export enum ConsoleStatus {
  DRAFT = 'DRAFT',
  RELEASE = 'RELEASE',
}

export enum TableDataType {
  BOOLEAN = 'BOOLEAN',
  NUMERIC = 'NUMERIC',
  STRING = 'STRING',
  DATETIME = 'DATETIME',
  // 暂时不适配
  BINARY = 'BINARY',
  CONTENT = 'CONTENT',
  STRUCT = 'STRUCT',
  DOCUMENT = 'DOCUMENT',
  ARRAY = 'ARRAY',
  OBJECT = 'OBJECT',
  REFERENCE = 'REFERENCE',
  ROWID = 'ROWID',
  ANY = 'ANY',
  UNKNOWN = 'UNKNOWN',
}

// /**
//  * 布尔值
//  */
//  BOOLEAN("布尔值"),

//  /**
//   * 数字
//   */
//  NUMERIC("数字"),
 
//  /**
//   * 字符串
//   */
//  STRING("字符串"),
 
//  /**
//   * 日期
//   */
//  DATETIME("日期"),
 
//  /**
//   * 二进制
//   */
//  BINARY("空数据"),
 
//  /**
//   * 内容
//   */
//  CONTENT("内容"),
 
//  /**
//   * 结构
//   */
//  STRUCT("结构"),
 
//  /**
//   * 文档
//   */
//  DOCUMENT("文档"),
 
//  /**
//   * 数组
//   */
//  ARRAY("数组"),
 
 
//  /**
//   * 对象
//   */
//  OBJECT("对象"),
 
 
//  /**
//   * 引用
//   */
//  REFERENCE("引用"),
 
 
//  /**
//   * 行号
//   */
//  ROWID("行号"),
 
 
//  /**
//   * 任何
//   */
//  ANY("任何"),
 
//  UNKNOWN("未知"),

export enum EnvType {
  DAILY = 'DAILY',
  PRODUCT = 'PRODUCT',
}

export enum OSType {
  WIN = 'Win',
  MAC = 'Mac',
  RESTS = 'rests',
}
export enum ConsoleType {
  SQLQ = 'SQLQ',
  EDITTABLE = 'editTable',
}

export enum TabOpened {
  IS_OPEN = 'y',
  NOT_OPEN = 'n',
}

/** console顶部注释 */
export const consoleTopComment = `-- Chat2DB自然语言转SQL等AI功能 >> https://github.com/alibaba/Chat2DB/blob/main/CHAT2DB_AI_SQL.md\n\n`;
