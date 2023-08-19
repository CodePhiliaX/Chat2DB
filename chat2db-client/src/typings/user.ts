export type IRole = 'admin' | 'normal';
export interface IUser {
  id?: number;
  userName: string;
  nickName: string;
  password?: string;
  password2?: string;
  email: string;
  role?: IRole;
}
