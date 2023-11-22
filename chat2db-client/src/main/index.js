const { app, BrowserWindow, Menu, shell, net, ipcMain, dialog } = require('electron');
const { exec } = require('child_process');
const path = require('path');
const os = require('os');
const fs = require('fs');
const registerAppMenu = require('./menu');
const registerAnalysis = require('./analysis');
const i18n = require('./i18n');
const { loadMainResource } = require('./utils');

let mainWindow = null;

let baseUrl = null;

function createWindow() {
  mainWindow = new BrowserWindow({
    minWidth: 1080,
    minHeight: 720,
    show: false,
    webPreferences: {
      webSecurity: false,
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
  // mainWindow.webContents.on('new-window', function (event, url) {
  //   event.preventDefault();
  //   shell.openExternal(url);
  // });
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });
}

// const menu = Menu.buildFromTemplate(menuBar);
// Menu.setApplicationMenu(menu);

app.commandLine.appendSwitch('--disable-gpu-sandbox');

app.on('ready', () => {
  createWindow();
  registerAppMenu(mainWindow);
  registerAnalysis();

  app.on('activate', function () {
    if (mainWindow === null) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('before-quit', () => {
  if(baseUrl){
    try {
      const request = net.request({
        headers: {
          'Content-Type': 'application/json',
        },
        method: 'POST',
        url: `${baseUrl}/api/system/stop`,
      });
      request.end();
    } catch (error) {}
  }
});

ipcMain.handle('get-product-name', () => {
  const exePath = app.getPath('exe');
  const { name } = path.parse(exePath);
  return name;
});

// 重启app
ipcMain.on('quit-app', () => {
  app.relaunch();
  app.quit();
});

ipcMain.on('register-app-menu', (event, orgs) => {
  registerAppMenu(mainWindow, orgs);
});

ipcMain.on('set-base-url',(event,_baseUrl)=>{
  baseUrl = _baseUrl;
})


