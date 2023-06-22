const { contextBridge, ipcRenderer } = require('electron');
const { spawn } = require('child_process');
const { JAVA_APP_NAME, JAVA_PATH } = require('./constants');
const path = require('path');

contextBridge.exposeInMainWorld('myAPI', {
  startServerForSpawn: async () => {
    const path1 = path.join(__dirname, `app/${JAVA_APP_NAME}`);

    const productName = await ipcRenderer.invoke('get-product-name');

    const isTest = productName.match(/test$/i) !== null;

    console.log('productName:', productName, isTest);

    const child = spawn(path.join(__dirname, JAVA_PATH), [
      '-jar',
      '-Xmx512M',
      `-Dspring.profiles.active=${isTest ? 'test' : 'release'}`,
      '-Dserver.address=127.0.0.1',
      path1,
    ]);

    child.stdout.on('data', (buffer) => {
      console.log(buffer.toString('utf8'));
      const data = buffer.toString('utf8');
      if (data.toString().indexOf('Started Application') !== -1) {
        console.log('load success');
      }
    });
    child.stderr.on('data', (data) => {
      console.error(`stderr: ${data}`);
      // alert('启动服务异常');
    });
    child.on('close', (code) => {
      console.log(`child process exited with code ${code}`);
    });
  },
});
