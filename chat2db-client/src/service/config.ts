import { IAiConfig } from '@/typings';
import createRequest from './base';

export interface ILatestVersion {
  /**
   * 桌面
   */
  desktop: boolean;
  /**
   * 新版本
   */
  version: string;
  /**
   * 热更新包地址，可用来判断是否热更新
   */
  hotUpgradeUrl: null | string;
  /**
   * 用户选择的是手动更新还是自动更新
   */
  type: 'manual' | 'auto';
  /**
   * 是否需要更新
   */
  needUpdate?: boolean;
  /**
   * 下载地址
   */
  downloadLink?: null | string;
  /**
   * 更新日志
   */
  updateLog?: null | string;
  /**
   * 白名单，用于测试
   */
  whiteList?: null | string;
}

const getSystemConfig = createRequest<{ code: string }, { code: string; content: string }>(
  '/api/config/system_config/:code',
  { errorLevel: false },
);
const setSystemConfig = createRequest<{ code: string; content: string }, void>('/api/config/system_config', {
  errorLevel: 'toast',
  method: 'post',
});

const getAiSystemConfig = createRequest<{ aiSqlSource?: string }, IAiConfig>('/api/config/system_config/ai', {
  errorLevel: false,
});

const setAiSystemConfig = createRequest<IAiConfig, void>('/api/config/system_config/ai', {
  errorLevel: 'toast',
  method: 'post',
});

const getAiWhiteAccess = createRequest<{ apiKey: string }, boolean>('/api/ai/embedding/white/check', {
  method: 'get',
});

// 检测仪更新获取最新的版本信息，如果返回结果为null，说明没有更新
const getLatestVersion = createRequest<{ currentVersion: string }, ILatestVersion>('/api/system/get_latest_version', {
  method: 'get',
});

// 检测最新的包后端是否下载成功
const isUpdateSuccess = createRequest<{ version: string }, boolean>('/api/system/is_update_success', {
  method: 'get',
});

// 告诉后端下载最新的包
const updateDesktopVersion = createRequest<ILatestVersion, boolean>('/api/system/update_desktop_version', {
  method: 'post',
});

// 告诉后端下载最新的包
const setAppUpdateType = createRequest<ILatestVersion['type'], boolean>('/api/system/set_update_type', {
  method: 'post',
});

export default {
  getSystemConfig,
  setSystemConfig,
  getAiSystemConfig,
  setAiSystemConfig,
  getAiWhiteAccess,
  getLatestVersion,
  isUpdateSuccess,
  updateDesktopVersion,
  setAppUpdateType
};
