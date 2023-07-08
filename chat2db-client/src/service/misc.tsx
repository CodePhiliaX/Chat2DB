import createRequest from "./base";
const testService = createRequest<void, void>('/api/system', { errorLevel: false });
const systemStop = createRequest<void, void>('/api/system/stop', { errorLevel: false, method: 'post' });
const testApiSmooth = createRequest<void, void>('/api/system/get-version-a', { errorLevel: false, method: 'get' });

export default {
  testService,
  systemStop,
  testApiSmooth,
}