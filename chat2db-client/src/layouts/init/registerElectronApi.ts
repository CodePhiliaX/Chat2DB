// 注册Electron关闭时，关闭服务
import { useSettingStore } from '@/store/setting'
const registerElectronApi = () => {
  window.electronApi?.registerAppMenu({
    version: __APP_VERSION__,
  });
  window.electronApi?.setBaseURL?.(window._BaseURL);
  window.electronApi?.setForceQuitCode?.(useSettingStore.getState().holdingService);
};

export default registerElectronApi;
