import common from './common';
import connection from './connection';
import menu from './menu';
import setting from './setting';
import workspace from './workspace';

export default {
  lang: 'en',
  ...common,
  ...setting,
  ...connection,
  ...workspace,
  ...menu,
};
