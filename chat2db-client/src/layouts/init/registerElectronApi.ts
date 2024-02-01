// 注册Electron关闭时，关闭服务
import { useGlobalStore } from '@/store/global'
const registerElectronApi = () => {
  window.electronApi?.registerAppMenu({
    version: __APP_VERSION__,
  });
  window.electronApi?.setBaseURL?.(window._BaseURL);
  window.electronApi?.setForceQuitCode?.(useGlobalStore.getState().holdingService);
};

export default registerElectronApi;
