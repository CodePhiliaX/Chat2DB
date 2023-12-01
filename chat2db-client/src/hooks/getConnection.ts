import connectionService from '@/service/connection';
import { setConnectionEnvList, getConnectionList } from '@/pages/main/store/connection';
import { useWorkspaceStore } from '@/pages/main/workspace/store';

const getConnectionEnvList = () => {
  connectionService.getEnvList().then((res) => {
    setConnectionEnvList(res);
  });
};
import { setCurrentConnectionDetails } from '@/pages/main/workspace/store/common';

const getConnection = () => {
  const currentConnectionDetails = useWorkspaceStore.getState().currentConnectionDetails;


  getConnectionList().then((res) => {
    // 如果连接列表为空，则设置当前连接为空
    if (res.length === 0) {
      setCurrentConnectionDetails(null);
      return;
    }

    // 如果当前连接不存在，则设置当前连接为第一个连接
    if (!currentConnectionDetails?.id) {
      setCurrentConnectionDetails(res[0]);
      return;
    }

    // 如果存在但是不在列表中，则设置当前连接为第一个连接
    const currentConnection = res.find((item) => item.id === currentConnectionDetails?.id);
    if (!currentConnection) {
      setCurrentConnectionDetails(res[0]);
    }
  });

  getConnectionEnvList();
};

export default getConnection;
