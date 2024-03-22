import { defineConfig } from 'umi';
import { extractYarnConfig } from './src/utils/webpack';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const yarn_config = extractYarnConfig(process.argv);
const publicPath = yarn_config.public_path || './static/front/';

const chainWebpack = (config: any, { webpack }: any) => {
  config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
    {
      languages: ['mysql', 'pgsql', 'sql'],
    },
  ]);
};

export default defineConfig({
  publicPath: publicPath,
  chainWebpack,
  define: {
    'process.env.UMI_ENV': process.env.UMI_ENV,
  },
  headScripts: [
    `window.dataLayer = window.dataLayer || [];
    function gtag() {
      window.dataLayer.push(arguments);
    }
    gtag('js', new Date());
    gtag('config', 'G-V8M4E5SF61', {
      platform: 'WEB',
      version: '${yarn_config['app_version']}'
    });`,
  ],
});
