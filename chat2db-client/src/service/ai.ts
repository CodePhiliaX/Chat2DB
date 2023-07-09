import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import { ILoginAndQrCode, IRemainingUse } from '@/typings/ai';
import createRequest from './base';

const getRemainingUse = createRequest<{ key: string }, IRemainingUse>('/api/client/remaininguses/:key', {
  errorLevel: false,
});

const getLoginQrCode = createRequest<{ token?: string }, ILoginAndQrCode>('/api/ai/config/getLoginQrCode', {
  isFullPath: true,
});

export default { getRemainingUse, getLoginQrCode };
