import { useEffect } from 'react';

import { useConsoleStore, getSavedConsoleList } from '@/store/console';

const useGetConsoleList = () => {
  const { consoleList } = useConsoleStore((state) => {
    return {
      consoleList: state.consoleList,
    };
  });

  useEffect(() => {
    getSavedConsoleList();
  }, []);

  return {
    consoleList,
  }
};

export default useGetConsoleList;
