import { LangType } from '@/constants/common'
import menu from './menu';
import common from './common';
import connection from './connection';
import setting from './setting';
import workspace from './workspace';

export default {
  lang: LangType.ZH_CN,
  ...connection,
  ...common,
  ...setting,
  ...workspace,
  ...menu,
  ...connection,
};
