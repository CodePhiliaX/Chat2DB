import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import { ILoginAndQrCode, IRemainingUse } from '@/typings/ai';
import createRequest from './base';

const getRemainingUse = createRequest<{}, IRemainingUse>('/api/ai/config/remaininguses/', {
  errorLevel: false,
});

const getLoginQrCode = createRequest<{ token?: string }, ILoginAndQrCode>('/api/ai/config/getLoginQrCode');

const getLoginStatus = createRequest<{ token?: string }, ILoginAndQrCode>('/api/ai/config/getLoginStatus');

export default { getRemainingUse, getLoginQrCode, getLoginStatus };
