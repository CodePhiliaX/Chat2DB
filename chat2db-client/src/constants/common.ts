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
  OCEANBASE = "OCEANBASE",
  PRESTO = "PRESTO",
  HIVE = "HIVE",
  KINGBASE = "KINGBASE",
}

export enum ConsoleOpenedStatus {
  IS_OPEN = 'y',
  NOT_OPEN = 'n',
}

export enum ConsoleStatus {
  DRAFT = 'DRAFT',
  RELEASE = 'RELEASE',
}

export enum OSType {
  WIN = 'Win',
  MAC = 'Mac',
  RESTS = 'rests',
}

export enum OperationType {
  CONSOLE = 'console',
  FUNCTION = 'function',
  PROCEDURE = 'procedure',
  VIEW = 'view',
  TRIGGER = 'trigger',
}

export const operationTypeConfig: {
  [key in OperationType]: {
    icon: string
  };
} = {
  [OperationType.CONSOLE]: {
    icon: '\ue619'
  },
  [OperationType.VIEW]: {
    icon: '\ue70c'
  },
  [OperationType.FUNCTION]: {
    icon: '\ue76a'
  },
  [OperationType.PROCEDURE]: {
    icon: '\ue73c'
  },

  [OperationType.TRIGGER]: {
    icon: '\ue64a'
  }
}