import { defineConfig } from 'umi';
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const chainWebpack = (config: any, { webpack }: any) => {
  config.plugin('monaco-editor').use(MonacoWebpackPlugin, [
    {
      languages: ['mysql', 'pgsql', 'sql'],
    },
  ]);

  config.plugin("umi-webpack-bundle-analyzer").use(new BundleAnalyzerPlugin({
    analyzerMode: 'static',
    reportFilename: 'bundle-report.html',
    defaultSizes: 'gzip',
    openAnalyzer: true,
  }));

};

export default defineConfig({
  publicPath: './',
  chainWebpack,
  dynamicImport: false,
});
