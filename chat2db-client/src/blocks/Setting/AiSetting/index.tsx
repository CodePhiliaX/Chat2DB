import React, { useEffect, useState } from 'react';
import configService, { IChatGPTConfig } from '@/service/config';
import { AiSqlSourceType } from '@/typings/ai';
import { Button, Input, message, Radio } from 'antd';
import i18n from '@/i18n';
import classnames from 'classnames';
import Popularize from '@/components/Popularize'
import { IAIState } from '@/models/ai';
import styles from './index.less';
const path = require('path');

interface IProps {
  handleUpdateAiConfig: (payload: IAIState['keyAndAiType']) => void;
  chatGPTConfig: IChatGPTConfig;
}

// openAI 的设置项
export default function SettingAI(props: IProps) {
  const { handleUpdateAiConfig } = props;
  const [chatGPTConfig, setChatGPTConfig] = useState<IChatGPTConfig>(props?.chatGPTConfig);

  if (!chatGPTConfig) {
    return null;
  }

  useEffect(() => {
    setChatGPTConfig(props.chatGPTConfig);
  }, [props.chatGPTConfig]);

  function changeChatGPTApiKey() {
    const newChatGPTConfig = { ...chatGPTConfig };
    if (newChatGPTConfig.apiHost && !newChatGPTConfig.apiHost?.endsWith('/')) {
      newChatGPTConfig.apiHost = newChatGPTConfig.apiHost + '/';
    }
    if (chatGPTConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI) {
      newChatGPTConfig.apiHost = `${window._appGatewayParams.baseUrl || 'http://test.sqlgpt.cn/gateway'}${'/model/'}`
    }
    configService.setChatGptSystemConfig(newChatGPTConfig).then((res) => {
      message.success(i18n('common.text.submittedSuccessfully'));
    });

    handleUpdateAiConfig &&
      handleUpdateAiConfig({
        key: newChatGPTConfig.apiKey,
        aiType: newChatGPTConfig.aiSqlSource,
      });
  }

  return (
    <>
      <div className={styles.aiSqlSource}>
        <div className={styles.aiSqlSourceTitle}>{i18n('setting.title.aiSource')}:</div>
        <Radio.Group
          onChange={(e) => {
            setChatGPTConfig({ ...chatGPTConfig, aiSqlSource: e.target.value });
          }}
          value={chatGPTConfig?.aiSqlSource}
        >
          <Radio value={AiSqlSourceType.CHAT2DBAI}>Chat2DB AI</Radio>
          <Radio value={AiSqlSourceType.OPENAI}>Open AI</Radio>
          <Radio value={AiSqlSourceType.AZUREAI}>Azure AI</Radio>
          <Radio value={AiSqlSourceType.RESTAI}>{i18n('setting.tab.custom')}</Radio>
        </Radio.Group>
      </div>
      {chatGPTConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.chat2dbApiKey')}
              value={chatGPTConfig.apiKey}
              onChange={(e) => {
                setChatGPTConfig({ ...chatGPTConfig, apiKey: e.target.value });
              }}
            />
          </div>
        </div>
      )}
      {chatGPTConfig?.aiSqlSource === AiSqlSourceType.OPENAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiKey')}
              value={chatGPTConfig.apiKey}
              onChange={(e) => {
                setChatGPTConfig({ ...chatGPTConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Api Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.apiHost')}
              value={chatGPTConfig.apiHost}
              onChange={(e) => {
                setChatGPTConfig({ ...chatGPTConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'host')}
              value={chatGPTConfig.httpProxyHost}
              onChange={(e) => {
                setChatGPTConfig({
                  ...chatGPTConfig,
                  httpProxyHost: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>HTTP Proxy Prot</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.httpsProxy', 'port')}
              value={chatGPTConfig.httpProxyPort}
              onChange={(e) => {
                setChatGPTConfig({
                  ...chatGPTConfig,
                  httpProxyPort: e.target.value,
                });
              }}
            />
          </div>
        </div>
      )}
      {chatGPTConfig?.aiSqlSource === AiSqlSourceType.AZUREAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureOpenAIKey')}
              value={chatGPTConfig.azureApiKey}
              onChange={(e) => {
                setChatGPTConfig({ ...chatGPTConfig, azureApiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Endpoint</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureEndpoint')}
              value={chatGPTConfig.azureEndpoint}
              onChange={(e) => {
                setChatGPTConfig({ ...chatGPTConfig, azureEndpoint: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>DeploymentId</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.azureDeployment')}
              value={chatGPTConfig.azureDeploymentId}
              onChange={(e) => {
                setChatGPTConfig({
                  ...chatGPTConfig,
                  azureDeploymentId: e.target.value,
                });
              }}
            />
          </div>
        </div>
      )}
      {chatGPTConfig?.aiSqlSource === AiSqlSourceType.RESTAI && (
        <div>
          <div className={styles.title}>{i18n('setting.label.customAiUrl')}</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              placeholder={i18n('setting.placeholder.customUrl')}
              value={chatGPTConfig.restAiUrl}
              onChange={(e) => {
                setChatGPTConfig({
                  ...chatGPTConfig,
                  restAiUrl: e.target.value,
                });
              }}
            />
          </div>
          <div className={styles.title}>{i18n('setting.label.isStreamOutput')}</div>
          <div className={classnames(styles.content)}>
            <Radio.Group
              onChange={(e) => {
                setChatGPTConfig({
                  ...chatGPTConfig,
                  restAiStream: e.target.value,
                });
              }}
              value={chatGPTConfig.restAiStream}
            >
              <Radio value={true}>{i18n('common.text.is')}</Radio>
              <Radio value={false}>{i18n('common.text.no')}</Radio>
            </Radio.Group>
          </div>
        </div>
      )}
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={changeChatGPTApiKey}>
          {i18n('setting.button.apply')}
        </Button>
      </div>
      {chatGPTConfig?.aiSqlSource === AiSqlSourceType.CHAT2DBAI && (
        <Popularize source='setting'></Popularize>
      )
      }
    </>
  );
}
