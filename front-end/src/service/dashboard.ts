import { IPageResponse } from '@/typings/common';
import { IChartItem, IDashboardItem } from '@/typings/dashboard';
import createRequest from './base';

/** 获取报表列表 */
const getDashboardList = createRequest<{}, IPageResponse<IDashboardItem>>('/api/dashboard/list', { method: 'get' });
/** 创建报表 */
const createDashboard = createRequest<{ name: string; description: string; schema?: string; chartId?: number[] }, void>(
  '/api/dashboard/create',
  { method: 'post' },
);
/** 更新报表 */
const updateDashboard = createRequest<
  { id: number; name: string; description: string; schema?: string; chartId?: number[] },
  void
>('/api/dashboard/update', { method: 'post' });
/** 删除报表 */
const deleteDashboard = createRequest<{ id: string }, string>('/api/dashboard/:id', { method: 'delete' });

/** 根据id 查询图表详情 */
const getChartById = createRequest<{ id: string }, IChartItem>('/api/chart/:id', { method: 'get' });
/** 创建图表 */
const createChart = createRequest<IChartItem, void>('/api/chart/create', { method: 'post' });
/** 更新图表 */
const updateChart = createRequest<IChartItem, void>('/api/chart/update', { method: 'post' });
/** 删除图表 */
const deleteChart = createRequest<{ id: string }, string>('/api/chart/:id', { method: 'delete' });

export {
  getDashboardList,
  createDashboard,
  updateDashboard,
  deleteDashboard,
  getChartById,
  createChart,
  updateChart,
  deleteChart,
};
