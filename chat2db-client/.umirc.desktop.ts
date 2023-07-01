import { formatDate } from './src/utils/date';
import { defineConfig } from 'umi';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
// const UMI_PublicPath = process.env.UMI_PublicPath || './';

const chainWebpack = (config: any, { webpack }: any) => {
  config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
    {
      languages: ['mysql', 'pgsql', 'sql'],
    },
  ]);
};

export default defineConfig({
  publicPath: './',
  chainWebpack,
  headScripts: ['if (window.myAPI) { window.myAPI.startServerForSpawn() }'],
  define: {
    'process.env.UMI_ENV': process.env.UMI_ENV,
  },
});
