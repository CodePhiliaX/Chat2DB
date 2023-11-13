// 注册Electron关闭时，关闭服务
const registerElectronApi = () => {
  window.electronApi?.registerAppMenu({
    version: __APP_VERSION__,
  });
  window.electronApi?.setBaseURL?.(window._BaseURL);
};

export default registerElectronApi;
