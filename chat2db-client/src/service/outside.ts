import createRequest from "./base";
import {IVersionResponse} from '@/typings'

const checkVersion = createRequest<void, IVersionResponse>('/client/version/check/v2', { errorLevel: false, outside: true });

export default {    
  checkVersion,
}