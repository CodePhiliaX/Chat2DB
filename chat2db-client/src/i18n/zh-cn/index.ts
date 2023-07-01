import { LangType } from '@/constants'
import menu from './menu';
import common from './common';
import connection from './connection';
import setting from './setting';
import workspace from './workspace';
import dashboard from './dashboard';
import chat from './chat';

export default {
  lang: LangType.ZH_CN,
  ...connection,
  ...common,
  ...setting,
  ...workspace,
  ...menu,
  ...connection,
  ...dashboard,
  ...chat
};
