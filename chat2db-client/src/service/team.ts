import createRequest from './base';
import { IPageParams, IPageResponse } from '@/typings';
import { IDataSourcePageQueryVO, ITeamPageQueryVO, ITeamVO, IUserPageQueryVO, IUserVO } from '@/typings/team';

/**
 * 获取共享链接列表
 */
const getDataSourceList = createRequest<IPageParams, IPageResponse<IDataSourcePageQueryVO>>(
  '/api/admin/data_source/page',
  {
    method: 'get',
  },
);

/** 用户管理列表查询 */
const getUserManagementList = createRequest<IPageParams, IPageResponse<IUserPageQueryVO>>('/api/admin/user/page', {
  method: 'get',
});

/** 创建用户 */
const createUser = createRequest<IUserVO, { data: number }>('/api/admin/user/create', {
  method: 'post',
});

/** 团队-团队管理列表查询 */
const getTeamManagementList = createRequest<IPageParams, IPageResponse<ITeamPageQueryVO>>('/api/admin/team/page', {
  method: 'get',
});

/** 团队-创建团队 */
const createTeam = createRequest<ITeamVO, { data: number }>('/api/admin/team/create', {
  method: 'post',
});

export { getDataSourceList, getUserManagementList, createUser, getTeamManagementList, createTeam };
