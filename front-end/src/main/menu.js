const i18n = require('./i18n');
const menuBar = [
  {
    label: 'Chat2DB',
    submenu: [
      {
        label: '关于Chat2DB',
      },
      { type: 'separator' },
      {
        label: '刷新',
        accelerator: process.platform === 'darwin' ? 'Cmd+R' : 'Ctrl+R',
      },
      {
        label: '检查更新',
      },
      { type: 'separator' },
      {
        label: '退出',
      },
    ],
  },
  {
    label: i18n('menu.edit'),
    submenu: [
      { label: '撤销', role: 'undo' },
      { label: '重做', role: 'redo' },
      { type: 'separator' },
      { label: '剪切', role: 'cut' },
      { label: '复制', role: 'copy' },
      { label: '粘贴', role: 'paste' },
      { label: '全选', role: 'selectAll' },
    ],
  },
  {
    label: '帮助',
    submenu: [
      {
        label: '打开日志',
        accelerator:
          process.platform === 'darwin' ? 'Cmd+Shift+L' : 'Ctrl+Shift+L',
        click() {
          const fileName = '.chat2db/logs/application.log';
          const url = path.join(os.homedir(), fileName);
          shell.openPath(url).then((str) => console.log('err:', str));
        },
      },
      {
        label: '打开控制台',
        accelerator:
          process.platform === 'darwin' ? 'Cmd+Shift+I' : 'Ctrl+Shift+I',
        click() {
          mainWindow && mainWindow.toggleDevTools();
        },
      },
      {
        label: '访问官网',
        click() {
          const url = 'https://chat2db.opensource.alibaba.com/';
          shell.openExternal(url);
        },
      },
      // {
      //   label: '关于',
      //   role: 'about', // about （关于），此值只针对 Mac  OS X 系统
      //   // 点击事件 role 属性能识别时 点击事件无效
      //   click: () => {
      //     var aboutWin = new BrowserWindow({
      //       width: 300,
      //       height: 200,
      //       parent: win,
      //       modal: true,
      //     });
      //     aboutWin.loadFile('about.html');
      //   },
      // },
    ],
  },
];

module.exports = menuBar;
