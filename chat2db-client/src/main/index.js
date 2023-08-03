const { app, BrowserWindow, Menu, shell, net, ipcMain, dialog } = require('electron');
const { exec } = require('child_process');
const path = require('path');
const os = require('os');
const fs = require('fs');
const registerAppMenu = require('./menu');
const i18n = require('./i18n');
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
  registerAppMenu();

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

app.on('before-quit', (event) => {
  // const isWindows = os.platform() === 'win32';
  // let ports = [10821, 10822, 10824]; // 日常端口、测试包端口、线上包端口
  // for (let port of ports) {
  //   let command = '';
  //   if (isWindows) {
  //     command = `netstat -ano | findstr:${port}`;
  //   } else {
  //     command = `lsof -i :${port} | awk '{print $2}'`;
  //   }

  //   exec(command, (err, stdout) => {
  //     if (err) {
  //       console.error(`exec error: ${err}`);
  //       return;
  //     }

  //     let pidArr = [];
  //     if (isWindows) {
  //       const lines = stdout.trim().split('\n');
  //       pidArr = lines.map((line) => line.trim().split(/\s+/)[4]).filter((pid) => !isNaN(pid));
  //     } else {
  //       pidArr = stdout.trim().split('\n');
  //     }

  //     if (pidArr.length) {
  //       try {
  //         (pidArr || []).forEach((pid) => {
  //           !!pid && !isNaN(pid) && process.kill(pid);
  //         });
  //       } catch (error) {
  //         console.error(`Error killing process: ${error}`);
  //       }
  //     }
  //   });
  // }
  try {
    const request = net.request({
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
      url: 'http://127.0.0.1:10824/api/system/stop',
    });
    request.end();
  } catch (error) {}
});

ipcMain.handle('get-product-name', (event) => {
  const exePath = app.getPath('exe');
  const { name } = path.parse(exePath);
  return name;
});
