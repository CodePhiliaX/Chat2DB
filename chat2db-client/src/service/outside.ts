import createRequest from './base';

const dynamicUrl = createRequest<string, void>('', {
  dynamicUrl: true,
});

export default {
  dynamicUrl,
};
