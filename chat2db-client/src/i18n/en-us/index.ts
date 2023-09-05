import common from './common';
import connection from './connection';
import menu from './menu';
import setting from './setting';
import workspace from './workspace';
import dashboard from './dashboard';
import chat from './chat';
import team from './team'
import login from './login';

export default {
  lang: 'en',
  ...common,
  ...setting,
  ...connection,
  ...workspace,
  ...menu,
  ...dashboard,
  ...chat,
  ...team,
  ...login
};
