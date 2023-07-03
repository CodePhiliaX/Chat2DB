const i18n = require('./i18n');
const { shell, app, dialog, BrowserWindow, Menu } = require('electron');
const os = require('os');
const path = require('path');
const registerAppMenu = () => {
  const menuBar = [
    {
      label: 'Chat2DB',
      submenu: [
        {
          label: '关于Chat2DB',
          click() {
            dialog.showMessageBox({
              title: '关于Chat2DB',
              message: `关于Chat2DB v${app.getVersion()}`,
              detail:
                // An intelligent database client and smart BI reporting tool with integrated AI capabilities.
                '一个集成AI能力的智能数据库客户端和智能BI报表工具。',
              icon: './logo/icon.png',
            });
          },
        },
        // {
        //   label: '检查更新',
        // },
        { type: 'separator' },
        {
          label: '退出',
          accelerator: process.platform === 'darwin' ? 'Cmd+Q' : 'Ctrl+ALT+F4',
          click() {
            // 退出程序
            app.quit();
          },
        },
      ],
    },
    {
      // label: i18n('menu.edit'),
      label: '编辑',
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
      // label: i18n('menu.edit'),
      label: '视图',
      submenu: [
        {
          label: '刷新',
          accelerator: process.platform === 'darwin' ? 'Cmd+R' : 'Ctrl+R',
          click() {
            const focusedWindow = BrowserWindow.getFocusedWindow();
            if (focusedWindow) {
              focusedWindow.reload();
            }
          },
        },
        { type: 'separator' },
        { label: '全屏', role: 'togglefullscreen' },
      ],
    },

    {
      label: '帮助',
      submenu: [
        {
          label: '打开日志',
          accelerator: process.platform === 'darwin' ? 'Cmd+Shift+L' : 'Ctrl+Shift+L',
          click() {
            const fileName = '.chat2db/logs/application.log';
            const url = path.join(os.homedir(), fileName);
            shell.openPath(url).then((str) => console.log('err:', str));
          },
        },
        {
          label: '打开控制台',
          accelerator: process.platform === 'darwin' ? 'Cmd+Shift+I' : 'Ctrl+Shift+I',
          click() {
            const focusedWindow = BrowserWindow.getFocusedWindow();
            focusedWindow && focusedWindow.toggleDevTools();
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
  // console.log('registerAppMenu', registerAppMenu);
  Menu.setApplicationMenu(Menu.buildFromTemplate(menuBar));
};

module.exports = registerAppMenu;
