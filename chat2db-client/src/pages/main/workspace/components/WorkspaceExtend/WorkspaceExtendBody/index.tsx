import { useMemo } from 'react';
import {extendConfig} from '../config';
import {useWorkspaceStore} from '@/pages/main/workspace/store';


export default () => {
  const {currentWorkspaceExtend} = useWorkspaceStore((state) => {
    return {
      currentWorkspaceExtend: state.currentWorkspaceExtend,
    }
  });
  const component = useMemo(() => {
    return extendConfig.find((item) => item.code === currentWorkspaceExtend)?.components
  }, [currentWorkspaceExtend]);

  return component
};
