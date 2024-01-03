import { queryCurUser } from '@/store/user'

/** 初始化登陆的信息 */
const initLoginInfo = () => {
  queryCurUser();
};

export default initLoginInfo;
