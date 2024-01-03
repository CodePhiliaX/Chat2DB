import { StoreApi } from 'zustand';
import { UseBoundStoreWithEqualityFn, createWithEqualityFn } from 'zustand/traditional';
import { devtools } from 'zustand/middleware';
import { shallow } from 'zustand/shallow';
import { IUserVO } from '@/typings/user';
import { getUser } from '@/service/user';

export interface IUserStore {
  curUser: IUserVO | null;
}

const initUserStore: IUserStore = {
  curUser: null,
};

/**
 * 用户 store
 */
export const useUserStore: UseBoundStoreWithEqualityFn<StoreApi<IUserStore>> = createWithEqualityFn(
  devtools(() => initUserStore),
  shallow,
);

/**
 *
 * @param curUser 设置当前用户
 */

export const setCurUser = (curUser: IUserVO) => {
  useUserStore.setState({ curUser });
};

/**
 * 获取当前用户
 */

export const queryCurUser = async () => {
  const curUser = await getUser();
  useUserStore.setState({ curUser });
  // 向cookie中写入当前用户id
  const date = new Date('2030-12-30 12:30:00').toUTCString();
  document.cookie = `CHAT2DB.USER_ID=${curUser?.id};Expires=${date}`;
};
