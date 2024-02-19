import { v4 as uuidv4 } from 'uuid';
import { useGlobalStore } from '@/store/global';

export interface ICommandLineRequest {
  url: string;
  method: string;
  data: {
    params: any;
  };
}

export interface ICommandLineParams extends ICommandLineRequest {
  id: string;
}

export interface ICommandLineRequestListItem {
  requestData: ICommandLineParams;
  responseData: any;
  resolve: (value: any) => void;
  reject: (reason?: any) => void;
}

export const commandLineRequest = (data: ICommandLineRequest) => {
  const id = uuidv4();
  const commandLineParams = {
    id: id,
    ...data,
  };
  return new Promise((resolve, reject) => {
    useGlobalStore.getState().javaServer?.stdin?.write(JSON.stringify(commandLineParams));
    
    const commandLineRequestListItem = {
      requestData: commandLineParams,
      responseData: null,
      resolve,
      reject,
    }
    useGlobalStore.getState().addCommandLineRequestListItem(commandLineRequestListItem);
  });
};
