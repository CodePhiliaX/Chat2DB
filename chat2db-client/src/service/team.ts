import createRequest from './base';
import { IPageParams, IPageResponse } from '@/typings';
import {
  IDataSourcePageQueryVO,
  ITeamPageQueryVO,
  ITeamVO,
  IUserPageQueryVO,
  IUserVO,
  TeamUserPageQueryVO,
} from '@/typings/team';

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
const updateTeam = createRequest<ITeamVO, { data: number }>('/api/admin/team/update', {
  method: 'post',
});
const deleteTeam = createRequest<{ id: number }, {}>('/api/admin/team/:id', {
  method: 'delete',
});

/** 团队-团队管理中获取包含用户列表 */
const getUserListFromTeam = createRequest<IPageParams & { teamId: number }, IPageResponse<TeamUserPageQueryVO>>(
  '/api/admin/team/user/page',
  {
    method: 'get',
  },
);
const deleteUserFromTeam = createRequest<{ id: number }, {}>('/api/admin/team/user/:id', {
  method: 'delete',
});

// ======================= 通用列表 =====================
/** 通用-获取user列表 */
const getCommonUserList = createRequest<{ searchKey: string }, IUserPageQueryVO[]>('/api/admin/common/user/list', {
  method: 'get',
});

export {
  getDataSourceList,
  getUserManagementList,
  createUser,
  getTeamManagementList,
  createTeam,
  updateTeam,
  deleteTeam,
  getUserListFromTeam,
  deleteUserFromTeam,
  getCommonUserList,
};
