// ===================== Common ==================
export enum ManagementType {
  DATASOURCE = 'DATASOURCE',
  TEAM = 'TEAM',
  USER = 'USER',
}

export enum AffiliationType {
  'USER_TEAM' = 'USER_TEAM',
  'USER_DATASOURCE' = 'USER_DATASOURCE',
  'TEAM_USER' = 'TEAM_USER',
  'TEAM_DATASOURCE' = 'TEAM_DATASOURCE',
  'DATASOURCE_USER/TEAM' = 'DATASOURCE_USER/TEAM'
}

export enum SearchType {
  DATASOURCE = 'DATASOURCE',
  TEAM = 'TEAM',
  USER = 'USER',
  'USER/TEAM' = 'USER/TEAM'
}

export enum StatusType {
  INVALID = 'INVALID',
  VALID = 'VALID',
}

export enum RoleType {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

export enum MemberType {
  TEAM = 'TEAM',
  USER = 'USER'
}



// ===================== DataSource ==================

export interface IDataSourceVO {
  /**
   * 连接别名
   */
  alias?: string;
  /**
   * 环境
   */
  environment?: IEnvironmentVO;
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

export interface IEnvironmentVO {
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
  style?: string;
}


export interface IDataSourceAccessVO {
  /**
   * 授权对象
   */
  accessObject: IDataSourceAccessObjectVO;
  /**
   * 授权id,根据类型区分是用户还是团队
   */
  accessObjectId: number;
  /**
   * 授权类型
   */
  accessObjectType: RoleType;
  /**
   * 主键
   */
  id: number;
}


export interface IDataSourceAccessObjectVO {
  /**
   * The name of the code that belongs to the authorization type, such as user account, team
   * code
   */
  code?: string;
  /**
   * 授权id,根据类型区分是用户还是团队
   */
  id?: number;
  /**
   * Code that belongs to the authorization type, such as user name, team name
   */
  name?: string;
  /**
   * 授权类型
   */
  type?: RoleType;
}

// ===================== User ======================

export interface IUserVO {
  /**
 * 主键
 */
  id: number;

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


export interface IUserWithTeamVO {
  /**
   * 主键
   */
  id?: number;
  /**
   * 团队
   */
  team?: ITeamVO;
  /**
   * user id
   */
  userId?: number;
}

export interface IUserWithDataSourceVO {
  id?: number;
  /**
   * Data Source
   */
  dataSource?: IDataSourceVO;
  /**
   * user id
   */
  userId?: number;
}


// ===================== Team =====================

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

export interface ITeamWithUserVO {
  id: number;
  teamId: number;
  user: IUserVO;
}

export interface ITeamWithDataSourceVO {
  /**
   * Data Source
   */
  dataSource?: IDataSourceVO;
  /**
   * 主键
   */
  id: number;
  /**
   * team id
   */
  teamId?: number;
}

// ===================== USER/TEAM =====================
export interface ITeamAndUserVO {
  code?: string;
  id?: number;
  name?: string;
  type?: MemberType
}
