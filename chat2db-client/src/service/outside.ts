import createRequest from './base';
import { IVersionResponse } from '@/typings';

const checkVersion = createRequest<void, IVersionResponse>('/api/client/version/check/v2', {
  errorLevel: false,
  outside: true,
});

const dynamicUrl = createRequest<string, void>('', {
  dynamicUrl: true,
});

export default {
  dynamicUrl,
  checkVersion,
};
