const { DEV_WEB_URL } = require('./constants');
const path = require('path');
const url = require('url');
const { version } = require('os');
const fs = require('fs');

/**
 * 加载主进程前端资源
 * @param {*} mainWindow
 */
function loadMainResource(mainWindow) {
  if (process.env.NODE_ENV === 'development') {
    mainWindow.loadURL(DEV_WEB_URL);
    mainWindow.webContents.openDevTools();
    // 监听应用程序根路径下的所有文件，当文件发生修改时，自动刷新应用程序
    require('electron-reload')(path.join(__dirname, '..'));
  } else {
    mainWindow.loadURL(
      url.format({
        pathname: path.join(__dirname, '../..', `./${readVersion()}`, `./dist/index.html`),
        protocol: 'file:',
        slashes: true,
      }),
    );
  }
}

/**
 * 应该读取哪个版本的资源
 * @param {*} 
 */
function readVersion() {
  if (process.env.NODE_ENV !== 'development') {
    var readDir = fs.readdirSync(path.join(__dirname, '../..', './versions'));
    console.log(readDir);
  }
  if (readDir.length) {
    return readDir[readDir.length - 1]
  }
  return ''
}

module.exports = {
  loadMainResource,
  readVersion
};
