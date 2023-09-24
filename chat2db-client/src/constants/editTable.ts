// 索引类型
export enum IndexesType {
  NormPRIMARY_KEYal = 'PRIMARY_KEY',
  NORMAL = 'NORMAL',
  UNIQUE = 'UNIQUE',
  FULLTEXT = 'FULLTEXT',
  SPATIAL = 'SPATIAL',
}

export enum EditColumnOperationType { 
  // 新增
  Add = 'ADD',
  // 修改
  Modify = 'MODIFY',
  // 删除
  Delete = 'DELETE',
}
