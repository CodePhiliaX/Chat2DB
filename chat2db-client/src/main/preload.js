const { contextBridge, ipcRenderer } = require('electron');
const { spawn } = require('child_process');
const { JAVA_APP_NAME, JAVA_PATH } = require('./constants');
const path = require('path');
const { readVersion } = require('./utils');

contextBridge.exposeInMainWorld('myAPI', {
  startServerForSpawn: async () => {
    const javaPath = path.join(__dirname, '../..', `./versions/${readVersion()}`, `./static/${JAVA_APP_NAME}`);
    const libPath = path.join(__dirname, '../..', `./versions/${readVersion()}`, './static/lib');

    const productName = await ipcRenderer.invoke('get-product-name');

    const isTest = productName.match(/test$/i) !== null;

    console.log('productName:', productName, isTest);

    const child = spawn(path.join(__dirname, '../..', `./static/${JAVA_PATH}`), [
      '-jar',
      '-Xmx1024M',
      `-Dspring.profiles.active=${isTest ? 'test' : 'release'}`,
      '-Dserver.address=127.0.0.1',
      `-Dproject.path=${javaPath}`,
      `-Dloader.path=${libPath}`,
      javaPath,
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
    });
    child.on('close', (code) => {
      console.log(`child process exited with code ${code}`);
    });
  },
});
