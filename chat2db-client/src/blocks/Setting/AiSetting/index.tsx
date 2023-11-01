import React, { useEffect, useState } from 'react';
import configService from '@/service/config';
import { AIType } from '@/typings/ai';
import { Alert, Button, Form, Input, Radio, RadioChangeEvent, Spin } from 'antd';
import i18n from '@/i18n';
import classnames from 'classnames';
import { IAiConfig } from '@/typings/setting';
import styles from './index.less';
import { getUser } from '@/service/user';
import { ILoginUser, IRole } from '@/typings/user';

interface IProps {
  handleApplyAiConfig: (aiConfig: IAiConfig) => void;
  aiConfig: IAiConfig;
}

const AITypeName = {
  [AIType.CHAT2DBAI]: 'Chat2DB',
  [AIType.ZHIPUAI]: i18n('setting.tab.aiType.zhipu'),
  [AIType.BAICHUANAI]: i18n('setting.tab.aiType.baichuan'),
  [AIType.OPENAI]: 'Open AI',
  [AIType.AZUREAI]: 'Azure AI',
  [AIType.RESTAI]: i18n('setting.tab.custom'),
};

// openAI 的设置项
export default function SettingAI(props: IProps) {
  const [aiConfig, setAiConfig] = useState<IAiConfig>();
  const [userInfo, setUserInfo] = useState<ILoginUser>();
  const [loading, setLoading] = useState(false);

  const queryUserInfo = async () => {
    setLoading(true);
    try {
      const res = await getUser();
      // 向cookie中写入当前用户id
      // const date = new Date('2030-12-30 12:30:00').toUTCString();
      // document.cookie = `CHAT2DB.USER_ID=${res?.id};Expires=${date}`;
      setUserInfo(res);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    queryUserInfo();
  }, []);

  useEffect(() => {
    setAiConfig(props.aiConfig);
  }, [props.aiConfig]);

  if (!aiConfig) {
    return <Alert description={i18n('setting.ai.tips')} type="warning" showIcon />;
  }

  if (userInfo?.roleCode === IRole.USER) {
    // 如果是用户，不能配置ai
    return <Alert description={i18n('setting.ai.user.hidden')} type="warning" showIcon />;
  }

  const handleAiTypeChange = async (e: RadioChangeEvent) => {
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
    if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
      newAiConfig.apiHost = `${window._appGatewayParams.baseUrl || 'http://test.sqlgpt.cn/gateway'}${'/model/'}`;
    }

    if (props.handleApplyAiConfig) {
      props.handleApplyAiConfig(newAiConfig);
    }
  };

  return (
    <Spin spinning={loading}>
      <div className={styles.aiSqlSource}>
        <div className={styles.aiSqlSourceTitle}>{i18n('setting.title.aiSource')}:</div>
        <Radio.Group onChange={handleAiTypeChange} value={aiConfig?.aiSqlSource}>
          {Object.keys(AIType).map((key) => (
            <Radio key={key} value={key}>
              {AITypeName[key]}
            </Radio>
          ))}
        </Radio.Group>
      </div>

      {/* <Form layout='vertical'>
        <Form.Item  label={'Api Key'} className={styles.title}>
          <Input
            autoComplete="off"
            value={aiConfig.apiKey}
            onChange={(e) => {
              setAiConfig({ ...aiConfig, apiKey: e.target.value });
            }}
          />
        </Form.Item>
      </Form> */}

      {aiConfig?.aiSqlSource === AIType.CHAT2DBAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              placeholder={i18n('setting.placeholder.chat2dbApiKey')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AIType.ZHIPUAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.chat2dbApiHost')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.apiHost')}
              defaultValue={'https://open.bigmodel.cn/api/paas/v3/model-api/'}
              value={aiConfig.apiHost}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Model</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.azureEndpoint')}
              defaultValue={'chatglm_turbo'}
              value={aiConfig.model}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, model: e.target.value });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AIType.BAICHUANAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.chat2dbApiHost')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>SecretKey Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.chat2dbApiHost')}
              value={aiConfig.secretKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, secretKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Host</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.apiHost')}
              defaultValue={'https://open.bigmodel.cn/api/paas/v3/model-api/'}
              value={aiConfig.apiHost}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiHost: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Model</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.azureEndpoint')}
              defaultValue={'chatglm_turbo'}
              value={aiConfig.model}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, model: e.target.value });
              }}
            />
          </div>
        </div>
      )}
      {aiConfig?.aiSqlSource === AIType.OPENAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
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
              autoComplete="off"
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
              autoComplete="off"
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
              autoComplete="off"
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
      {aiConfig?.aiSqlSource === AIType.AZUREAI && (
        <div>
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
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
              autoComplete="off"
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
              autoComplete="off"
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
      {aiConfig?.aiSqlSource === AIType.RESTAI && (
        <div>
          <div className={styles.title}>{i18n('setting.label.customAiUrl')}</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
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
          <div className={styles.title}>Api Key</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.apiKey')}
              value={aiConfig.apiKey}
              onChange={(e) => {
                setAiConfig({ ...aiConfig, apiKey: e.target.value });
              }}
            />
          </div>
          <div className={styles.title}>Model</div>
          <div className={classnames(styles.content, styles.chatGPTKey)}>
            <Input
              autoComplete="off"
              // placeholder={i18n('setting.placeholder.azureDeployment')}
              value={aiConfig.model}
              onChange={(e) => {
                setAiConfig({
                  ...aiConfig,
                  model: e.target.value,
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

          <div style={{ marginTop: '32px', fontSize: '12px', opacity: '0.5' }}>tips: 输出格式参考OpenAI</div>
        </div>
      )}
      <div className={styles.bottomButton}>
        <Button type="primary" onClick={handleApplyAiConfig}>
          {i18n('setting.button.apply')}
        </Button>
      </div>

      {/* {aiConfig?.aiSqlSource === AIType.CHAT2DBAI && !aiConfig.apiKey && <Popularize source="setting" />} */}
    </Spin>
  );
}
