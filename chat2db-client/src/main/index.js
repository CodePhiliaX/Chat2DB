const { app, BrowserWindow, Menu, shell, net, ipcMain, dialog } = require('electron');
const path = require('path');
const os = require('os');
const fs = require('fs');
const menuBar = require('./menu');
const { loadMainResource } = require('./utils');
let mainWindow = null;

function createWindow() {
  mainWindow = new BrowserWindow({
    minWidth: 1080,
    minHeight: 720,
    show: false,
    webPreferences: {
      webSercurity: false,
      nodeIntegration: true,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
    },
  });
  mainWindow.maximize();
  mainWindow.show();

  // 加载应用-----
  loadMainResource(mainWindow);

  // 关闭window时触发下列事件.
  mainWindow.on('closed', function (event) {
    event.preventDefault();
    mainWindow = null;
  });

  // 监听打开新窗口事件 用默认浏览器打开
  mainWindow.webContents.on('new-window', function (event, url) {
    event.preventDefault();
    shell.openExternal(url);
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// const menu = Menu.buildFromTemplate(menuBar);
// Menu.setApplicationMenu(menu);

app.on('ready', () => {
  createWindow();

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('before-quit', (event) => {
  const request = net.request({
    headers: {
      'Content-Type': 'application/json',
    },
    method: 'POST',
    url: 'http://127.0.0.1:10824/api/system/stop',
  });
  request.write(JSON.stringify({}));
  request.on('response', (response) => {
    response.on('data', (res) => {
      let data = JSON.parse(res.toString());
    });
    response.on('end', () => {});
  });
  request.end();
});

ipcMain.handle('get-product-name', (event) => {
  const exePath = app.getPath('exe');
  const { name } = path.parse(exePath);
  return name;
});

module.exports = {
  mainWindow,
};
