import React, { useEffect, useState } from 'react';
import configService from '@/service/config';
import { AiSqlSourceType } from '@/typings/ai';
import { Button, Input, Radio } from 'antd';
import i18n from '@/i18n';
import classnames from 'classnames';
import { IAiConfig } from '@/typings/setting';
import styles from './index.less';
import Popularize from '@/components/Popularize';
interface IProps {
  handleApplyAiConfig: (aiConfig: IAiConfig) => void;
  aiConfig: IAiConfig;
}

// openAI 的设置项
export default function SettingAI(props: IProps) {
  const [aiConfig, setAiConfig] = useState<IAiConfig>(props?.aiConfig);

  if (!aiConfig) {
    return null;
  }

  useEffect(() => {
    setAiConfig(props.aiConfig);
  }, [props.aiConfig]);

  const handleAiTypeChange = async (e) => {
    const aiSqlSource = e.target.value;

    // 查询对应ai类型的配置
    const res = await configService.getAiSystemConfig({
      aiSqlSource,
    });
    setAiConfig(res);
  };

  /** 应用Ai配置 */
  const handleApplyAiConfig = () => {
    const newAiConfig = { ...aiConfig };
    if (newAiConfig.apiHost && !newAiConfig.apiHost?.endsWith('/')) {
      newAiConfig.apiHost = newAiConfig.apiHost + '/';
    }
    if (aiConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI) {
      newAiConfig.apiHost = `${window._appGatewayParams.baseUrl || 'http://test.sqlgpt.cn/gateway'}${'/model/'}`;
    }

    if (props.handleApplyAiConfig) {
      props.handleApplyAiConfig(newAiConfig);
    }
  };

  return (
    <>
      <div className={styles.aiSqlSource}>
        <div className={styles.aiSqlSourceTitle}>{i18n('setting.title.aiSource')}:</div>
        <Radio.Group onChange={handleAiTypeChange} value={aiConfig?.aiSqlSource}>
          <Radio value={AiSqlSourceType.CHAT2DBAI}>Chat2DB AI</Radio>
          <Radio value={AiSqlSourceType.OPENAI}>Open AI</Radio>
          <Radio value={AiSqlSourceType.AZUREAI}>Azure AI</Radio>
          <Radio value={AiSqlSourceType.RESTAI}>{i18n('setting.tab.custom')}</Radio>
        </Radio.Group>
      </div>

      {aiConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.chat2dbApiKey')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AiSqlSourceType.OPENAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiKey')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Api Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiHost')}
              value={aiConfig.apiHost}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'host')}
              value={aiConfig.httpProxyHost}
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  httpProxyHost: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Port</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'port')}
              value={aiConfig.httpProxyPort}
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  httpProxyPort: e.target.value,
                });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AiSqlSourceType.AZUREAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureOpenAIKey')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Endpoint</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureEndpoint')}
              value={aiConfig.apiHost}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>DeploymentId</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureDeployment')}
              value={aiConfig.model}
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  model: e.target.value,
                });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AiSqlSourceType.RESTAI && (
        <div>
          <div className={styles.title}>{i18n('setting.label.customAiUrl')}</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.customUrl')}
              value={aiConfig.apiHost}
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  apiHost: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>{i18n('setting.label.isStreamOutput')}</div>
          <div className={classnames(styles.content)}>
            <Radio.Group
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  stream: e.target.value,
                });
              }}
              value={aiConfig.stream}
            >
              <Radio value={true}>{i18n('common.text.is')}</Radio>
              <Radio value={false}>{i18n('common.text.no')}</Radio>
            </Radio.Group>
          </div>
        </div>
      )}
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={handleApplyAiConfig}>
          {i18n('setting.button.apply')}
        </Button>
      </div>

      {/* {aiConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI && !aiConfig.apiKey && <Popularize source="setting" />} */}
    </>
  );
}
