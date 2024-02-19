import { ICommandLineRequestListItem } from '@/service/commandLine';
import { ChildProcess } from 'child_process';

export interface RequestState {
  commandLineRequestList: {
    [key: string]: ICommandLineRequestListItem;
  };
  javaServer?:ChildProcess
}

export const initialRequestState: RequestState = {
  commandLineRequestList: {},
  javaServer: undefined
};
