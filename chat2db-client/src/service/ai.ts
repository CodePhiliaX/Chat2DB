import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import { IRemainingUse } from '@/typings/ai';
import createRequest from './base';

const getRemainingUse = createRequest<{ key: string }, IRemainingUse>('/client/remaininguses/:key');

export default { getRemainingUse };
