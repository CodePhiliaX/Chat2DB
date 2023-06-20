import { defineConfig } from 'umi';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
// const MonacoWebpackPlugin = require('monaco-editor-esm-webpack-plugin');
const assetDir = "static";

const chainWebpack = (config: any, { webpack }: any) => {
  config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
    {
      languages: ['mysql', 'pgsql', 'sql'],
    },
  ]);
  config.plugin('define').use(require('webpack').DefinePlugin, [{
    __BUILD_TIME__: JSON.stringify(formatDate(new Date(),'yyyyMMddhhmmss')),
    __APP_VERSION__: JSON.stringify(process.env.APP_VERSION || '0.0.0'),
  }]);
};

export default defineConfig({
  title: 'Chat2DB',
  history: {
    type: 'hash',
  },
  base: '/',
  publicPath: '/',
  hash: false,
  routes: [
    {
      path: '/',
      component: '@/components/AppContainer',
      routes: [
        { path: '/login', exact: true, component: '@/pages/login' },
        // { path: '/verify', exact: true, component: '@/pages/verify' },
        { path: '/error', component: '@/pages/error' },
        { path: '/demo', exact: true, component: '@/pages/demo' },
        {
          path: '/',
          component: '@/layouts/BaseLayout',
          routes: [
            {
              exact: true,
              path: '/',
              component: '@/pages/database',
            },
            {
              exact: true,
              path: '/database',
              component: '@/pages/database',
            },
            {
              path: '/sql-history',
              exact: true,
              component: '@/pages/sql-history',
            },
            {
              path: '/manage',
              exact: true,
              component: '@/pages/manage',
            },
            {
              path: '/chat',
              exact: true,
              component: '@/pages/chat-ai'
            },
            {
              redirect: '/error',
            }
          ]
        }
      ],
    },
  ],
  mfsu: {},
  fastRefresh: {},
  dynamicImport: {
    loading: '@/components/Loading/LazyLoading'
  },
  nodeModulesTransform: {
    type: 'none',
  },
  chainWebpack,
  devServer: {
    port: 8001,
    host: '127.0.0.1',
  },
  define: {
    'process.env.UMI_ENV': process.env.UMI_ENV,
    'process.env.APP_PORT': process.env.APP_PORT,
    'process.env.APP_VERSION': process.env.APP_VERSION,
  }
});

function formatDate(date:any, fmt = 'yyyy-MM-dd') {
  if (!date) {
    return '';
  }
  if (typeof date == 'number' || typeof date == 'string') {
    date = new Date(date);
  }
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }
  var o:any = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    S: date.getMilliseconds(),
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  for (var k  in o)
    if (new RegExp('(' + k + ')').test(fmt))
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
  return fmt;
}
