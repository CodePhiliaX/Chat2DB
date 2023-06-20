import { formatDate } from './src/utils/date';
import { defineConfig } from 'umi';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');

const chainWebpack = (config: any, { webpack }: any) => {
  config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
    {
      languages: ['mysql', 'pgsql', 'sql'],
    },
  ]);

  config.plugin('define').use(require('webpack').DefinePlugin, [
    {
      __BUILD_TIME__: JSON.stringify(formatDate(new Date(), 'yyyyMMddhhmmss')),
      __APP_VERSION__: JSON.stringify(process.env.APP_VERSION || '0.0.0'),
    },
  ]);
};

export default defineConfig({
  title: 'Chat2DB',
  base: '/',
  publicPath: '/',
  routes: [
    { path: '/demo', component: '@/pages/demo' },
    { path: '/connections', component: 'main' },
    { path: '/workspace', component: 'main' },
    { path: '/dashboard', component: 'main' },
    { path: '/test', component: '@/pages/test' },
    { path: '/', component: 'main' },
  ],
  npmClient: 'yarn',
  dva: {},
  plugins: ['@umijs/plugins/dist/dva'],
  chainWebpack,
  proxy: {
    '/api': {
      target: 'http://127.0.0.1:10821',
      changeOrigin: true,
    },
  },
});
