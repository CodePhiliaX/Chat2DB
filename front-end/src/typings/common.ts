import { DatabaseTypeCode } from '@/constants/database';
export interface IPageResponse<T> {
  data: T[];
  pageNo: number;
  pageSize: number;
  total: number;
  hasNextPage?: boolean;
}