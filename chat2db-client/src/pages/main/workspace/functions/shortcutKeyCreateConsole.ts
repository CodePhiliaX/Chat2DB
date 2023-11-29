import createConsole from './createConsole';
import { useWorkspaceStore } from '../store';
import { WorkspaceTabType } from '@/constants';

export const handelCreateConsole = () => { 
  const params = useWorkspaceStore.getState().currentConnectionDetails;
  createConsole({
    dataSourceId: params?.id,
    dataSourceName: params?.alias,
    type: params?.type,
    operationType: WorkspaceTabType.CONSOLE
  });
}

const shortcutKeyCreateConsole = () => {
  // 注册快捷键监听cmd+shift+l或ctrl+shift+l新建一个console
  const handleKeyDown = (e: KeyboardEvent) => {
    if ((e.metaKey && e.shiftKey && e.key === 'L') || (e.ctrlKey && e.shiftKey && e.key === 'L')) {
      handelCreateConsole()
    }
  };
  window.addEventListener('keydown', handleKeyDown);
  return () => {
    window.removeEventListener('keydown', handleKeyDown);
  };
}

export default shortcutKeyCreateConsole;
