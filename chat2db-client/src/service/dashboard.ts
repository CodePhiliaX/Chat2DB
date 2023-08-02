import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import createRequest from './base';

/** 获取报表列表 */
const getDashboardList = createRequest<{}, IPageResponse<IDashboardItem>>('/api/dashboard/list', { method: 'get' });
const getDashboardById = createRequest<{ id: number }, IDashboardItem>('/api/dashboard/:id', { method: 'get' });
/** 创建报表 */
const createDashboard = createRequest<{ name: string; description: string; schema?: string; chartId?: number[] }, void>(
  '/api/dashboard/create',
  { method: 'post' },
);
/** 更新报表 */
const updateDashboard = createRequest<IDashboardItem, void>('/api/dashboard/update', { method: 'post' });
/** 删除报表 */
const deleteDashboard = createRequest<{ id: number }, string>('/api/dashboard/:id', { method: 'delete' });

/** 根据id 查询图表详情 */
const getChartById = createRequest<{ id: number }, IChartItem>('/api/chart/:id', { method: 'get' });
/** 创建图表 */
const createChart = createRequest<IChartItem, number>('/api/chart/create', { method: 'post' });
/** 更新图表 */
const updateChart = createRequest<IChartItem, void>('/api/chart/update', { method: 'post' });
/** 删除图表 */
const deleteChart = createRequest<{ id: number }, string>('/api/chart/:id', { method: 'delete' });

export {
  getDashboardList,
  getDashboardById,
  createDashboard,
  updateDashboard,
  deleteDashboard,
  getChartById,
  createChart,
  updateChart,
  deleteChart,
};
