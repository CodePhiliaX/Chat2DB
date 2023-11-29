import { createConsole} from '../store/console'
import { useWorkspaceStore } from '../store';

export const handelCreateConsole = () => { 
  const params = useWorkspaceStore.getState().currentConnectionDetails;
  if (params) {
    createConsole({
      dataSourceId: params.id,
      dataSourceName: params.alias,
      databaseType: params.type,
    });
  }
}

const shortcutKeyCreateConsole = () => {
  // 注册快捷键监听cmd+shift+l或ctrl+shift+l新建一个console
  const handleKeyDown = (e: KeyboardEvent) => {
    if ((e.metaKey && e.shiftKey && e.code === 'KeyL') || (e.ctrlKey && e.shiftKey && e.key === 'KeyL')) {
      handelCreateConsole()
    }
  };
  window.addEventListener('keydown', handleKeyDown);
  return () => {
    window.removeEventListener('keydown', handleKeyDown);
  };
}

export default shortcutKeyCreateConsole;
