import { IndexesType, EditColumnOperationType } from '@/constants';

// 编辑表时表的基础数据
export interface IBaseInfo {
  name: string;
  comment?: string;
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
  primaryKey: string | null; // 是否主键
  typeName: string | null; // 类型名
  columnSize: number | null; // 列的长度
  bufferLength: number | null; // 缓冲区长度
  decimalDigits: string | null; // 小数位数
  numPrecRadix: number| null; // 数字精度
  sqlDataType: string| null; // sql数据类型
  sqlDatetimeSub: string| null; // sql日期时间子类型
  charOctetLength:string|  null; // 字符串最大长度
  ordinalPosition: number| null; // 位置
  nullable: 0 | 1 | null; //是否为空
  generatedColumn: string | null; // 是否生成列
}

export interface IIndexIncludeColumnItem extends IColumnItemNew {

}

// 编辑表时索引的数据结构
export interface IIndexItem {
  key?: string;
  name: string | null;
  columns: string | null;
  comment?: string | null;
  type: IndexesType | null;
  columnList: IIndexIncludeColumnItem[];
  editStatus: EditColumnOperationType | null; // 操作类型

}

// 编辑表时整体的数据结构
export interface IEditTableInfo extends IBaseInfo {
  columnList: IColumnItemNew[];
  indexList: IIndexItem[];
}
