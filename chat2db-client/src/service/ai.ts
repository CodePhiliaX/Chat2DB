import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import { IRemainingUse } from '@/typings/ai';
import createRequest from './base';

const getRemainingUse = createRequest<{ key: string }, IRemainingUse>('/api/client/remaininguses/:key', {
  errorLevel: false,
  outside: true,
});

export default { getRemainingUse };
