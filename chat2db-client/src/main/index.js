const { app, BrowserWindow, Menu, shell, net, ipcMain, dialog } = require('electron');
const { exec } = require('child_process');
const path = require('path');
const os = require('os');
const fs = require('fs');
const registerAppMenu = require('./menu');
const registerAnalysis = require('./analysis');
const i18n = require('./i18n');
const store = require('./store');
const { loadMainResource } = require('./utils');

let mainWindow = null;

let baseUrl = null;
let _forceQuitCode = false;

/**
 * Initial window options
 */

function createWindow() {
  const { width, height, x, y } = store.get('windowBounds', { width: 1440, height: 800 });

  const options = {
    x,
    y,
    height,
    width,
    minWidth: 1080,
    minHeight: 720,
    show: false,
    frame: false, // 无边框
    titleBarStyle: 'hidden',
    webPreferences: {
      webSecurity: false,
      spellcheck: false, // 禁用拼写检查器
      nodeIntegration: true,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
    },
  };

  mainWindow = new BrowserWindow(options);
  mainWindow.show();

  // 加载应用-----
  loadMainResource(mainWindow);

  // 关闭window时触发下列事件.
  mainWindow.on('closed', function (event) {
    event.preventDefault();
    app.hide();
  });

  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });

  mainWindow.on('resize', () => {
    store.set('windowBounds', mainWindow.getBounds());
  });

  mainWindow.on('move', () => {
    store.set('windowBounds', mainWindow.getBounds());
  });
}

// const menu = Menu.buildFromTemplate(menuBar);
// Menu.setApplicationMenu(menu);

app.commandLine.appendSwitch('--disable-gpu-sandbox');

app.on('ready', () => {
  createWindow();
  registerAppMenu(mainWindow);
  registerAnalysis();

  app.on('activate', () => {
    app.show();
  });
});

app.on('window-all-closed', (event) => {
  event.preventDefault();
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('before-quit', () => {
  if (baseUrl) {
    try {
      const request = net.request({
        headers: {
          'Content-Type': 'application/json',
        },
        method: 'POST',
        url: `${baseUrl}/api/system/stop?forceQuit=${_forceQuitCode}`,
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

// 放大或还原窗口
ipcMain.on('set-maximize', () => {
  if (mainWindow.isMaximized()) {
    mainWindow.unmaximize();
  } else {
    mainWindow.maximize();
  }
});

ipcMain.on('register-app-menu', (event, orgs) => {
  registerAppMenu(mainWindow, orgs);
});

ipcMain.on('set-base-url', (event, _baseUrl) => {
  baseUrl = _baseUrl;
});

ipcMain.on('set-force-quit-code', (event, _forceQuitCode) => {
  forceQuitCode = _forceQuitCode;
});
