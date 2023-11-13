import { setWorkspaceTabList, useConsoleStore, setActiveConsoleId } from '@/store/console';
import { ConsoleStatus, ConsoleOpenedStatus, WorkspaceTabType, DatabaseTypeCode } from '@/constants'
import historyService from '@/service/history';

interface ICreateConsoleParams { 
  name?: string;
  ddl?: string;
  dataSourceId?: number;
  databaseName?: string;
  schemaName?: string;
  type?: DatabaseTypeCode;
  operationType?: string;
}

function useCreateConsole() {
  const { workspaceTabList } = useConsoleStore(state => {
    return {
      workspaceTabList: state.workspaceTabList,
    }
  });

  const createConsole = (params: ICreateConsoleParams) => {
    const newConsole = {
      ...params,
      name: params.name || 'create console',
      ddl: params.ddl || '',
      status: ConsoleStatus.DRAFT,
      tabOpened: ConsoleOpenedStatus.IS_OPEN,
      operationType: WorkspaceTabType.CONSOLE,
    };
    historyService.createConsole(newConsole).then((res) => {
      const newList = [
        ...(workspaceTabList||[]),
        {
          id: res,
          title: newConsole.name,
          type: newConsole.operationType,
          uniqueData: newConsole,
        },
      ];
      setWorkspaceTabList(newList);
      setActiveConsoleId(res);
    });
  }
  
  return {
    createConsole
  }
}

export default useCreateConsole;
