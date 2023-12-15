import connectionService from '@/service/connection';
import { setConnectionEnvList } from '@/pages/main/store/connection';

const getConnectionEnvList = () => {
  connectionService.getEnvList().then((res) => {
    setConnectionEnvList(res);
  });
};

export default getConnectionEnvList;
