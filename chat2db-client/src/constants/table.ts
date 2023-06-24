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

export enum StatusType {
  SUCCESS = 'success',
  FAIL = 'fail',
}