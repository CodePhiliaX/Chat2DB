import { formatDate } from './src/utils/date';
import { defineConfig } from 'umi';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const UMI_PublicPath = process.env.UMI_PublicPath || '/static/front/';

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
  publicPath: UMI_PublicPath,
  chainWebpack
});
