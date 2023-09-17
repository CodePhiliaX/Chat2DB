import { IndexesType } from '@/constants';

// 编辑表时表的基础数据
export interface IBaseInfo {
  name: string;
  comment?: string;
}

// 编辑表时列的数据结构
export interface IColumnItem {
  key?: string; // 列的key 前端自己给的
  name: string | null; // 列名
  columnType: string | null; // 列的类型 比如 varchar(100) ,double(10,6)
  columnSize: number | null; // 列的长度
  nullable: number | null; // 是否为空
  primaryKey: boolean | null; // 是否主键
  defaultValue: string | null; // 默认值
  dataType: string | null; // 数据类型
  autoIncrement: boolean | null; // 是否自增
  numericPrecision: number | null; // 数字精度
  numericScale: number | null; // 数字比例
  characterMaximumLength: number | null; // 字符串最大长度
  comment: string | null; // 注释
}

export interface IIndexIncludeColumnItem { 
  key?: string; // 列的key 前端自己给的
  ascOrDesc: string | null; // 升序还是降序
  cardinality: number | null; // 基数
  collation: string | null; // 排序规则
  columnName: string | null; // 列名
  comment: string | null; // 注释
  databaseName: string | null; // 数据库名
  filterCondition: string | null; // 过滤条件
  indexName: string | null; // 索引名
  indexQualifier: string | null; // 索引限定符
  nonUnique: boolean | null; // 是否唯一
  ordinalPosition: number | null; // 位置
  schemaName: string | null; // 模式名
  tableName: string | null; // 表名
  type: string | null; // 类型
  pages: number | null; // 页数
  prefixLength: number | null; // 
}

// 编辑表时索引的数据结构
export interface IIndexItem {
  key?: string;
  name: string | null;
  columns: string | null;
  comment?: string | null;
  type: IndexesType | null;
  columnList: IIndexIncludeColumnItem[];
}

// 编辑表时整体的数据结构
export interface IEditTableInfo extends IBaseInfo {  
  columnList: IColumnItem[];
  indexList: IIndexItem[];
}
