import { EditColumnOperationType, NullableType } from '@/constants';

// 编辑表时表的基础数据
export interface IBaseInfo {
  name: string;
  comment?: string | null;
  charset: string | null; // 字符集
  engine: string | null; // 引擎
  incrementValue: string | null; // 自增值
}

export interface IColumnItemNew {
  editStatus: EditColumnOperationType | null; // 操作类型

  key?: string;
  oldName: string | null; // 老的列名
  name: string | null; // 列名

  databaseName: string | null; // 数据库名
  schemaName: string | null; // 模式名
  tableName: string | null; // 表名

  columnType: string | null; // 列的类型 比如 varchar(100) ,double(10,6)
  dataType: number | null; // 数据类型
  defaultValue: string | null; // 默认值
  autoIncrement: string | null; // 是否自增
  comment: string | null; // 注释
  primaryKey: boolean | null; // 是否主键
  primaryKeyOrder: number | null; // 主键顺序
  typeName: string | null; // 类型名
  columnSize: number | null; // 列的长度
  bufferLength: number | null; // 缓冲区长度
  decimalDigits: string | null; // 小数位数
  numPrecRadix: number| null; // 数字精度
  sqlDataType: string| null; // sql数据类型
  sqlDatetimeSub: string| null; // sql日期时间子类型
  charOctetLength:string|  null; // 字符串最大长度
  ordinalPosition: number| null; // 位置
  nullable: NullableType | null; //是否为空
  generatedColumn: string | null; // 是否生成列

  charSetName: string | null; // 字符集名
  collationName: string | null; // 排序规则名
  value: string | null; // 值
}

// 
export interface IIndexIncludeColumnItem {
  key?: string; // 前端添加的唯一标识
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
}


// 编辑表时索引的数据结构
export interface IIndexItem {
  key?: string;
  name: string | null;
  comment?: string | null;
  type: any | null;
  columnList: IIndexIncludeColumnItem[];
  editStatus: EditColumnOperationType | null; // 操作类型

}

// 编辑表时整体的数据结构
export interface IEditTableInfo extends IBaseInfo {
  columnList: IColumnItemNew[];
  indexList: IIndexItem[];
}
