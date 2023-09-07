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

export enum TabType {
  CONSOLE = 'console',
  FUNCTION = 'function',
  PROCEDURE = 'procedure',
  VIEW = 'view',
  TRIGGER = 'trigger',
  EditTable = 'editTable',
  OpenTable = 'openTable',
}

export const tabTypeConfig: {
  [key in TabType]: {
    icon: string
  };
} = {
  [TabType.CONSOLE]: {
    icon: '\uec83'
  },
  [TabType.VIEW]: {
    icon: '\ue70c'
  },
  [TabType.FUNCTION]: {
    icon: '\ue76a'
  },
  [TabType.PROCEDURE]: {
    icon: '\ue73c'
  },
  [TabType.TRIGGER]: {
    icon: '\ue64a'
  },
  [TabType.EditTable]: {
    icon: '\ue6b6'
  },
  [TabType.OpenTable]: {
    icon: '\ue618'
  }
}

