import { IChartItem, IDashboardItem, IPageResponse } from '@/typings';
import createRequest from './base';

const get = createRequest<{ key: string }, void>('/client/remaininguses/');
