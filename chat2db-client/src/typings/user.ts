export enum IRole {
  'ADMIN' = 'ADMIN',
  'USER' = 'USER',
  'DESKTOP' = 'DESKTOP',
}
export interface IUser {
  id?: number;
  userName: string;
  nickName: string;
  password?: string;
  password2?: string;
  email: string;
  role?: IRole;
}

export interface ILoginUser {
  /**
   * Is it an administrator
   */
  admin?: boolean;
  /**
   * 用户id
   */
  id?: number;
  /**
   * 昵称
   */
  nickName?: string;
  roleCode: IRole;
}
