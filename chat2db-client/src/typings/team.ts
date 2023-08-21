// ===================== Universal ==================
export enum ManagementType {
  DATASOURCE = 'DATASOURCE',
  TEAM = 'TEAM',
  USER = 'USER',
}

export enum StatusType {
  INVALID = 'INVALID',
  VALID = 'VALID',
}

export enum RoleType {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

// ===================== DataSource ==================

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
  status?: StatusType;
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
  roleCode: RoleType;
  /**
   * 用户状态
   */
  status: StatusType;
  /**
   * 用户名
   */
  userName: string;
}

// ===================== Team =====================

export interface ITeamPageQueryVO {
  /**
   * 团队编码
   */
  code: string;
  /**
   * 主键
   */
  id: number;
  /**
   * 团队名称
   */
  name: string;
  /**
   * 团队状态
   */
  status: StatusType;
}

export interface ITeamVO {
  id?: number;
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
   * 团队状态
   */
  status: StatusType;
}

export interface TeamUserPageQueryVO {
  id: number;
  teamId: number;
  user: IUserVO;
}
