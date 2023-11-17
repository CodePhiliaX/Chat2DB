import { DatabaseTypeCode, TableDataType } from '@/constants';

export interface IDatabase {
  name: string;
  code: DatabaseTypeCode;
  img: string;
  icon: string;
}

export interface ITableHeaderItem {
  dataType: TableDataType;
  name: string;
  autoIncrement: boolean | null; // 是否自增
  columnSize: number | null; // 字段长度
  comment: string | null; // 字段注释
  decimalDigits: number | null; // 小数位
  defaultValue: string | null; // 默认值
  nullable: boolean | null; // 是否为空
  primaryKey: boolean | null; // 是否为主键
}

export interface IManageResultData {
  dataList: string[][];
  headerList: ITableHeaderItem[];
  description: string;
  message: string;
  sql: string;
  originalSql: string;
  success: boolean;
  uuid?: string;
  duration: number;
  fuzzyTotal: string;
  hasNextPage: boolean;
  sqlType: 'SELECT' | 'UNKNOWN';
  updateCount?: number; // 如果是修改的话。后端会返回修改的条数
  canEdit?: boolean; // 返回的数据是否可以编辑
  tableName?: string; // 如果可以编辑的话。后端会返回表名称。修改需要给后端传递表名
}

/** 查询结果 配置属性 */
export interface IResultConfig {
  pageNo: number;
  pageSize: number;
  total: number | string;
  hasNextPage: boolean;
}

/** 不同数据库支持的列字段类型 以及字符集 排列规则列表*/
export interface IDatabaseSupportField {
  columnTypes: IColumnTypes[];
  charsets: ICharset[];
  collations: ICollation[];
  indexTypes: IIndexTypes[];
  defaultValues: IDefaultValue[];
}

/** 字段所对应的 字符集*/
export interface ICharset {
  charsetName: string; // 字符集名称
  defaultCollationName: string; // 字符集默认的排序规则
}

/** 排列规则*/
export interface ICollation {
  collationName: string;
}

/** 索引的类型*/
export interface IIndexTypes {
  typeName: string;
}

/** 不同数据库支持的列字段类型  以及支持调整的选项*/
export interface IColumnTypes {
  typeName: string;
  supportAutoIncrement: boolean; // 是否支持自增
  supportCharset: boolean; // 是否支持字符集
  supportCollation: boolean; // 是否支持排序规则
  supportComments: boolean; // 是否支持注释
  supportDefaultValue: boolean; // 是否支持默认值
  supportExtent: boolean; // 是否支持扩展
  supportLength: boolean; // 是否支持长度
  supportNullable: boolean; // 是否支持为空
  supportScale: boolean; // 是否支持小数位
  supportValue: boolean; // 是否支持值
  supportUnit: boolean; // 是否支持单位
}

/** 不同数据库支持不同的默认值 */
export interface IDefaultValue {
  defaultValue: string; // 默认值
}
