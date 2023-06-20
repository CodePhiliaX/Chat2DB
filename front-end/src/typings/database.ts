import { DatabaseTypeCode } from '@/constants/database';

export interface IDatabase {
  name: string;
  code: DatabaseTypeCode;
  img: string;
  icon: string;
}
