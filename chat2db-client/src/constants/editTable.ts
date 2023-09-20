// 索引类型
export enum IndexesType {
  // 普通索引
  Normal = 'normal',
  // 唯一索引
  Unique = 'unique',
  // 全文索引
  Fulltext = 'fulltext',
  // 空间索引
  Spatial = 'spatial',
}

export enum EditColumnOperationType { 
  // 新增
  Add = 'add',
  // 修改
  Modify = 'modify',
  // 删除
  Delete = 'delete',
}
