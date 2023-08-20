// ===================== DataSource ==================
/**
 * Pagination query
 *
 * DataSourcePageQueryVO
 */
export interface IDataSourcePageQueryVO {
  /**
   * 连接别名
   */
  alias?: string;
  /**
   * 环境
   */
  environment?: ISimpleEnvironmentVO;
  /**
   * 环境id
   */
  environmentId?: number;
  /**
   * 主键id
   */
  id?: number;
  /**
   * 连接地址
   */
  url?: string;
}

/**
 * 环境
 *
 * SimpleEnvironmentVO
 */
export interface ISimpleEnvironmentVO {
  /**
   * 主键
   */
  id?: number;
  /**
   * 环境名称
   */
  name?: string;
  /**
   * 环境缩写
   */
  shortName?: string;
  /**
   * 样式类型
   */
  style?: StyleType;
}
/**
 * 样式类型
 */
export enum StyleType {
  Release = 'RELEASE',
  Test = 'TEST',
}

// ===================== User ======================
export enum RoleStatusType {
  ADMIN = 'ADMIN',
  USER = 'USER',
}
export enum UserStatusType {
  INVALID = 'INVALID',
  VALID = 'VALID',
}
export type IRoleStatus = keyof typeof RoleStatusType;
export type IUserStatus = keyof typeof UserStatusType;
/**
 * Pagination query
 *
 * UserPageQueryVO
 */
export interface IUserPageQueryVO {
  /**
   * 主键
   */
  id: number;
  /**
   * 昵称
   */
  nickName: string;
  /**
   * 用户状态
   */
  status?: IUserStatus;
  /**
   * 用户名
   */
  userName: string;
}

/**
 * UserCreateRequest
 */
export interface IUserVO {
  /**
   * 邮箱
   */
  email: string;
  /**
   * 昵称
   */
  nickName: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 角色编码
   */
  roleCode: IRoleStatus;
  /**
   * 用户状态
   */
  status: IUserStatus;
  /**
   * 用户名
   */
  userName: string;
}

// ===================== Team =====================
export enum TeamStatusType {
  INVALID = 'INVALID',
  VALID = 'VALID',
}
export interface ITeamPageQueryVO {
  /**
   * 团队编码
   */
  code?: string;
  /**
   * 主键
   */
  id?: number;
  /**
   * 团队名称
   */
  name?: string;
  /**
   * 团队状态
   */
  status?: TeamStatusType;
}

export interface ITeamVO {
  /**
   * 团队编码
   */
  code: string;
  /**
   * 团队描述
   */
  description?: string;
  /**
   * 团队名称
   */
  name: string;
  /**
   * 角色编码
   */
  roleCode: string;
  /**
   * 团队状态
   */
  status: TeamStatusType;
}
