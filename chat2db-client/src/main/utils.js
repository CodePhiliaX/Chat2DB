const electronReload = require('electron-reload');
const { DEV_WEB_URL } = require('./constants');
const path = require('path');

/**
 * 加载主进程前端资源
 * @param {*} mainWindow
 */
function loadMainResource(mainWindow) {
  if (process.env.NODE_ENV === 'development') {
    mainWindow.loadURL(DEV_WEB_URL);
    mainWindow.webContents.openDevTools();

    // 监听应用程序根路径下的所有文件，当文件发生修改时，自动刷新应用程序
    electronReload(path.join(__dirname, '..'));
  } else {
    mainWindow.loadURL(
      url.format({
        pathname: path.join(__dirname, './dist/index.html'),
        protocol: 'file:',
        slashes: true,
      }),
    );
  }
}

module.exports = {
  loadMainResource,
};
