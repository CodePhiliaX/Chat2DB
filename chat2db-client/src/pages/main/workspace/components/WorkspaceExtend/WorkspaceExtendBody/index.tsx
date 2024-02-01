import React, { useMemo } from 'react';
import {extendConfig} from '../config';
import {useWorkspaceStore} from '@/store/workspace';


export default () => {
  const currentWorkspaceExtend = useWorkspaceStore((state) => state.currentWorkspaceExtend);
  const Component = useMemo(() => {
    return extendConfig.find((item) => item.code === currentWorkspaceExtend)?.components 
  }, [currentWorkspaceExtend]);

  return Component ? <Component /> : false
};
