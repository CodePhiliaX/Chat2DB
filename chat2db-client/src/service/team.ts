import createRequest from './base';
import { IConnectionDetails, IPageParams, IPageResponse } from '@/typings';
import { IDataSourceAccessObjectVO, IDataSourceVO, ITeamAndUserVO, ITeamVO, ITeamWithDataSourceVO, ITeamWithUserVO, IUserVO, IUserWithDataSourceVO, IUserWithTeamVO, RoleType } from '@/typings/team';

// =============================== DataSource ============================
/**
 * 链接-获取共享链接列表
 */
const getDataSourceList = createRequest<IPageParams, IPageResponse<IDataSourceVO>>('/api/admin/data_source/page', {
  method: 'get',
});

/**
 * 链接-创建链接
 */
const createDataSource = createRequest<IConnectionDetails | {}, number>('/api/admin/data_source/create', {
  method: 'post',
});
/**
 * 链接-更新链接
 */
const updateDataSource = createRequest<IConnectionDetails, number>('/api/admin/data_source/update', {
  method: 'post',
});
/**
 * 链接-删除链接
 */
const deleteDataSource = createRequest<{ id: number }, boolean>('/api/admin/data_source/:id', {
  method: 'delete',
});
/**
 * 链接-获取链接包含的团队/用户列表
 */
const getUserAndTeamListFromDataSource = createRequest<
  IPageParams & { dataSourceId: number },
  IPageResponse<IDataSourceAccessObjectVO>
>('/api/admin/data_source/access/page', {
  method: 'get',
});

/**
 * 链接-添加团队/人员权限到共享链接
 */
const updateUserAndTeamListFromDataSource = createRequest<
  { dataSourceId: number; accessObjectList: Array<{ id: number; type: RoleType }> }, number
>('/api/admin/data_source/access/batch_create', {
  method: 'post',
});

/**
 * 链接-删除团队/人员权限到共享链接
 */
const deleteUserOrTeamFromDataSource = createRequest<{ id: number }, boolean>('/api/admin/data_source/access/:id', {
  method: 'delete',
});


// ====================== User ======================

/** 用户-用户管理列表查询 */
const getUserManagementList = createRequest<IPageParams, IPageResponse<IUserVO>>('/api/admin/user/page', {
  method: 'get',
});

/** 创建用户 */
const createUser = createRequest<IUserVO, number>('/api/admin/user/create', {
  method: 'post',
});
/** 更新用户 */
const updateUser = createRequest<IUserVO, number>('/api/admin/user/update', {
  method: 'post',
});
/** 删除用户 */
const deleteUser = createRequest<{ id: number }, boolean>('/api/admin/user/:id', {
  method: 'delete',
});

/** 用户-用户管理中获取所属团队列表 */
const getTeamListFromUser = createRequest<IPageParams & { userId: number }, IPageResponse<IUserWithTeamVO>>(
  '/api/admin/user/team/page',
  {
    method: 'get',
  },
);
/** 用户-用户管理中更新所属团队 */
const updateTeamListFromUser = createRequest<{ userId: number; teamIdList: number[] }, number>(
  '/api/admin/user/team/batch_create',
  {
    method: 'post',
  }
);

/** 用户-用户管理中删除所属团队 */
const deleteTeamListFromUser = createRequest<{ id: number }, boolean>('/api/admin/user/team/:id', {
  method: 'delete',
});

/** 用户-用户管理中添加链接 */
const getDataSourceListFromUser = createRequest<
  IPageParams & { userId: number },
  IPageResponse<IUserWithDataSourceVO>
>('/api/admin/user/data_source/page', {
  method: 'get',
});

/** 用户-用户管理中更新链接 */
const updateDataSourceListFromUser = createRequest<
  { userId: number; dataSourceIdList: number[] }, number>(
    '/api/admin/user/data_source/batch_create', {
    method: 'post',
  });

/** 用户-用户管理中删除链接 */
const deleteDataSourceFromUser = createRequest<{ id: number }, boolean>('/api/admin/user/data_source/:id', {
  method: 'delete',
});


// ======================== 团队 ======================

/** 团队-团队管理列表查询 */
const getTeamManagementList = createRequest<IPageParams, IPageResponse<ITeamVO>>('/api/admin/team/page', {
  method: 'get',
});

/** 团队-创建团队 */
const createTeam = createRequest<ITeamVO, number>('/api/admin/team/create', {
  method: 'post',
});
/** 团队-更新团队 */
const updateTeam = createRequest<ITeamVO, number>('/api/admin/team/update', {
  method: 'post',
});
/** 团队-删除团队 */
const deleteTeam = createRequest<{ id: number }, boolean>('/api/admin/team/:id', {
  method: 'delete',
});

/** 团队-团队管理中获取包含用户列表 */
const getUserListFromTeam = createRequest<IPageParams & { teamId: number }, IPageResponse<ITeamWithUserVO>>(
  '/api/admin/team/user/page',
  {
    method: 'get',
  },
);
/** 团队-团队管理中更新包含用户列表 */
const updateUserListFromTeam = createRequest<{ teamId: number; userIdList: number[] }, number>(
  '/api/admin/team/user/batch_create',
  {
    method: 'post',
  },
);
/** 团队-团队管理中删除包含用户列表 */
const deleteUserFromTeam = createRequest<{ id: number }, boolean>('/api/admin/team/user/:id', {
  method: 'delete',
});


/** 用户-用户管理中添加归属链接 */
const getDataSourceListFromTeam = createRequest<
  IPageParams & { userId: number },
  IPageResponse<ITeamWithDataSourceVO>
>('/api/admin/team/data_source/page', {
  method: 'get',
});

/** 用户-用户管理中更新归属链接 */
const updateDataSourceListFromTeam = createRequest<
  { userId: number; dataSourceIdList: number[] }, number
>('/api/admin/team/data_source/batch_create', {
  method: 'post',
});

/** 用户-用户管理中删除所属团队 */
const deleteDataSourceFromTeam = createRequest<{ id: number }, boolean>('/api/admin/team/data_source/:id', {
  method: 'delete',
});

// ======================= 通用列表 =====================
/** 通用-获取user列表 */
const getCommonUserList = createRequest<{ searchKey: string }, IUserVO[]>('/api/admin/common/user/list', {
  method: 'get',
});
/** 通用-获取team列表 */
const getCommonTeamList = createRequest<{ searchKey: string }, ITeamVO[]>('/api/admin/common/team/list', {
  method: 'get',
});
/** 通用-获取DataSource列表 */
const getCommonDataSourceList = createRequest<{ searchKey: string }, IDataSourceVO[]>(
  '/api/admin/common/data_source/list',
  {
    method: 'get',
  },
);
/** 通用-获取user和team列表 */
const getCommonUserAndTeamList = createRequest<{ searchKey: string }, ITeamAndUserVO[]>(
  '/api/admin/common/team_user/list',
  {
    method: 'get',
  },
);

export {
  // dataSource
  getDataSourceList,
  createDataSource,
  updateDataSource,
  deleteDataSource,
  getUserAndTeamListFromDataSource,
  updateUserAndTeamListFromDataSource,
  deleteUserOrTeamFromDataSource,
  // user
  getUserManagementList,
  createUser,
  updateUser,
  deleteUser,
  getTeamListFromUser,
  updateTeamListFromUser,
  deleteTeamListFromUser,
  getDataSourceListFromUser,
  updateDataSourceListFromUser,
  deleteDataSourceFromUser,
  // team
  getTeamManagementList,
  createTeam,
  updateTeam,
  deleteTeam,
  getUserListFromTeam,
  updateUserListFromTeam,
  deleteUserFromTeam,
  getDataSourceListFromTeam,
  updateDataSourceListFromTeam,
  deleteDataSourceFromTeam,
  // common
  getCommonUserList,
  getCommonTeamList,
  getCommonDataSourceList,
  getCommonUserAndTeamList,
};
