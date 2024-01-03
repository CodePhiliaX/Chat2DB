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

export interface IUserVO {
  admin: boolean;
  id : number;
  nickName: string; 
  roleCode: string;
  token: string;
}
