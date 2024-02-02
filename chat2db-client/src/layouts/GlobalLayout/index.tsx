import React, { FC } from 'react';
import { Outlet } from 'umi';
import AppTheme, { AppThemeProps } from './AppTheme';
import usePollRequestService, { ServiceStatus } from '@/hooks/usePollRequestService';
import service from '@/service/misc';
import OpenScreenAnimation from './OpenScreenAnimation';
interface GlobalLayoutProps extends AppThemeProps {}

const GlobalLayout: FC<GlobalLayoutProps> = () => {
  const { serviceStatus, restartPolling } = usePollRequestService({
    loopService: service.testService,
  });

  // return <AppTheme>{serviceStatus === ServiceStatus.PENDING ? <OpenScreenAnimation /> : <Outlet />}</AppTheme>;
  return <AppTheme>{<Outlet />}</AppTheme>;
};

export default GlobalLayout;
