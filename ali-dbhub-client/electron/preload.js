const { contextBridge, ipcRenderer } = require('electron');
const { spawn, exec } = require('child_process');
const path = require('path');
const { app } = require('electron');

const appName = 'ali-dbhub-server-start.jar';
contextBridge.exposeInMainWorld('myAPI', {
  startServerForSpawn: async () => {
    const path1 = path.join(__dirname, `app/${appName}`);

    const productName = await ipcRenderer.invoke('get-product-name');
    const isTest = productName.match(/test$/i) !== null;

    console.log('productName:', productName, isTest);

    const ls = spawn(path.join(__dirname, 'jre/bin/java'), [
      '-jar',
      '-Xmx512M',
      `-Dspring.profiles.active=${isTest?'test':'release'}`,
      '-Dserver.address=127.0.0.1',
      path1,
    ]);
    ls.stdout.on('data', (buffer) => {
      console.log(buffer.toString('utf8'));
      const data = buffer.toString('utf8');
      if (data.toString().indexOf('Started Application') !== -1) {
        console.log('load success');
        // TODO:
        // window.location.href = 'http://localhost:10824';
      }
    });
    ls.stderr.on('data', (data) => {
      console.error(`stderr: ${data}`);
      // alert('启动服务异常');
    });
    ls.on('close', (code) => {
      console.log(`child process exited with code ${code}`);
    });
  },
  startServerForbat: () => {
    const bat = spawn(path.join(__dirname, 'my.bat'));
    bat.stdout.on('data', (data) => {
      console.log(data.toString());
      if (data.toString().indexOf('Started') !== -1) {
        window.location.href = 'http://localhost:8080';
      }
    });
    bat.stderr.on('data', (data) => {
      console.error(data.toString());
    });
    bat.on('exit', (code) => {
      console.log(`Child exited with code ${code}`);
    });
  },
});
