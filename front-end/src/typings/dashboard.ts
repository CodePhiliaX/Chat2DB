// export type IChartType = 'Pie' | 'Column' | 'Line';
export enum IChartType {
  'Pie' = 'Pie',
  'Column' = 'Column',
  'Line' = 'Line',
}
// export interface IDashboardItem {
//   name: string;
//   data: Array<IChartItem[]>;
// }

export interface IDashboardItem {
  id: number;
  name?: string;
  description?: string;
  /** 保存图表布局 二维数据 number[][]  */
  schema?: string;
  chartIds?: number[];
  gmtModified?: number;
  gmtCreate?: number;
}

// export interface IChartDataItem {
//   /** sql内容 */
//   sqlContext: string;
//   /** sql返回数据 */
//   sqlData: any;
//   /** 图表类型  */
//   chartType: IChartType;
//   /** 图表参数 */
//   chartParam: any;
// }

export interface IChartItem {
  id?: number;
  /** 图表名称 */
  name?: string;
  /** 图表描述 */
  description?: string;
  /** 图表参数 */
  schema?: string;
  /** 图表类型  */
  chartType?: IChartType;
  /** 数据源连接ID */
  dataSourceId?: number;
  /** 数据库类型 */
  type?: string;
  /** db名称 */
  databaseName?: string;
  /** ddl内容 */
  ddl?: string;
  /** 是否链接 */
  connectable?: boolean;
  /** sql返回数据 */
  sqlData?: any;
}
