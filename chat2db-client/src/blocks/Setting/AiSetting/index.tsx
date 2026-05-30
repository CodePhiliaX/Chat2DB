import React, { useEffect, useState } from 'react';
import configService from '@/service/config';
import { AIType } from '@/typings/ai';
import { Alert, Button, Flex, Form, Input, Radio, RadioChangeEvent } from 'antd';
import i18n from '@/i18n';
import { IAiConfig } from '@/typings/setting';
import { IRole } from '@/typings/user';
import { AIFormConfig, AITypeName } from './aiTypeConfig';
import styles from './index.less';
import { useUserStore } from '@/store/user';
import { getLinkBasedOnTimezone } from '@/utils/timezone';

interface IProps {
  handleApplyAiConfig: (aiConfig: IAiConfig) => void;
  aiConfig: IAiConfig;
}

function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

// openAI 的设置项
export default function SettingAI(props: IProps) {
  const [aiConfig, setAiConfig] = useState<IAiConfig>();
  const { userInfo } = useUserStore((state) => {
    return {
      userInfo: state.curUser,
    };
  });

  useEffect(() => {
    setAiConfig(props.aiConfig);
  }, [props.aiConfig]);

  if (!aiConfig) {
    return <Alert description={i18n('setting.ai.tips')} type="warning" showIcon />;
  }

  if (userInfo?.roleCode && userInfo?.roleCode === IRole.USER) {
    // 如果是用户，不能配置ai
    return <Alert description={i18n('setting.ai.user.hidden')} type="warning" showIcon />;
  }

  const handleAiTypeChange = async (e: RadioChangeEvent) => {
    const aiSqlSource = e.target.value;

    // 查询对应ai类型的配置
    try {
      const res = await configService.getAiSystemConfig({
        aiSqlSource,
      });
      
      // Special handling for Ollama AI - set defaults if no config found
      if (aiSqlSource === AIType.OLLAMAAI) {
        if (!res || !res.apiHost || !res.model) {
          const ollamaConfig: IAiConfig = {
            aiSqlSource: AIType.OLLAMAAI,
            ollamaApiHost: res?.apiHost || 'http://localhost:11434',
            ollamaModel: res?.model || 'qwen3-coder',
            apiHost: res?.apiHost || 'http://localhost:11434',
            model: res?.model || 'qwen3-coder',
          };
          setAiConfig(ollamaConfig);
        } else {
          // Map server response to Ollama config
          const ollamaConfig: IAiConfig = {
            aiSqlSource: AIType.OLLAMAAI,
            ollamaApiHost: res.apiHost,
            ollamaModel: res.model,
            apiHost: res.apiHost,
            model: res.model,
          };
          setAiConfig(ollamaConfig);
        }
      } else {
        setAiConfig(res);
      }
    } catch (error) {
      console.error('Failed to get AI config:', error);
      // Set default config for other AI types
      if (aiSqlSource === AIType.OLLAMAAI) {
        setAiConfig({
          aiSqlSource: AIType.OLLAMAAI,
          ollamaApiHost: 'http://localhost:11434',
          ollamaModel: 'qwen3-coder',
        });
      } else {
        setAiConfig({
          aiSqlSource: aiSqlSource as AIType,
        });
      }
    }
  };

  /** 应用Ai配置 */
  const handleApplyAiConfig = () => {
    const newAiConfig = { ...aiConfig };
    
    if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
      newAiConfig.apiHost = `${window._appGatewayParams.baseUrl || 'http://test.sqlgpt.cn/gateway'}${'/model/'}`;
    }
    
    // Special handling for Ollama AI
    if (aiConfig?.aiSqlSource === AIType.OLLAMAAI) {
      // Ensure Ollama config has proper defaults
      if (!newAiConfig.ollamaApiHost) {
        newAiConfig.ollamaApiHost = 'http://localhost:11434';
      }
      if (!newAiConfig.ollamaModel) {
        newAiConfig.ollamaModel = 'deepseek-v3.1:671b-cloud';
      }
      
      // Map Ollama fields to standard AI config fields for server compatibility
      newAiConfig.apiHost = newAiConfig.ollamaApiHost;
      newAiConfig.model = newAiConfig.ollamaModel;
      
      // Create JSON content for server storage
      const configContent = JSON.stringify({
        ollamaApiHost: newAiConfig.ollamaApiHost,
        ollamaModel: newAiConfig.ollamaModel
      });
      
      // Update content for server storage
      newAiConfig.content = configContent;
      
      // Debug logging
      console.log('DEBUG: Ollama config being sent:', newAiConfig);
    }

    if (props.handleApplyAiConfig) {
      console.log('DEBUG: Calling handleApplyAiConfig with:', newAiConfig);
      props.handleApplyAiConfig(newAiConfig);
    }
  };

  const renderAIConfig = () => {
    if (aiConfig?.aiSqlSource === AIType.CHAT2DBAI) {
      return (
        <Flex justify="center">
          <Button
            type="primary"
            onClick={() => {
              const link = getLinkBasedOnTimezone();
              window.open(link, '_blank');
            }}
          >
            {i18n('setting.chat2db.ai.button')}
          </Button>
        </Flex>
      );
    }
    
    // Special handling for Ollama AI
    if (aiConfig?.aiSqlSource === AIType.OLLAMAAI) {
      return (
        <>
          <Form layout="vertical">
            <Form.Item
              label="Ollama API Host"
              className={styles.title}
            >
              <Input
                autoComplete="off"
                value={aiConfig.ollamaApiHost || 'http://localhost:11434'}
                placeholder="http://localhost:11434"
                onChange={(e) => {
                  setAiConfig({ ...aiConfig, ollamaApiHost: e.target.value });
                }}
              />
            </Form.Item>
            <Form.Item
              label="Ollama Model"
              className={styles.title}
            >
              <Input
                autoComplete="off"
                value={aiConfig.ollamaModel || 'qwen2.5-coder'}
                placeholder="qwen2.5-coder"
                onChange={(e) => {
                  setAiConfig({ ...aiConfig, ollamaModel: e.target.value });
                }}
              />
            </Form.Item>
          </Form>
          <div className={styles.bottomButton}>
            <Button type="primary" onClick={handleApplyAiConfig}>
              {i18n('setting.button.apply')}
            </Button>
          </div>
        </>
      );
    }
    
    return (
      <>
        <Form layout="vertical">
          {Object.keys(AIFormConfig[aiConfig?.aiSqlSource]).map((key: string) => (
            <Form.Item
              key={key}
              required={key === 'apiKey' || key === 'secretKey'}
              label={capitalizeFirstLetter(key)}
              className={styles.title}
            >
              <Input
                autoComplete="off"
                value={aiConfig[key]}
                placeholder={
                  typeof AIFormConfig[aiConfig?.aiSqlSource]?.[key] === 'boolean' 
                    ? '' 
                    : AIFormConfig[aiConfig?.aiSqlSource]?.[key] || ''
                }
                onChange={(e) => {
                  setAiConfig({ ...aiConfig, [key]: e.target.value });
                }}
              />
            </Form.Item>
          ))}
        </Form>
        {aiConfig.aiSqlSource === AIType.RESTAI && (
          <div style={{ margin: '32px 0 ', fontSize: '12px', opacity: '0.5' }}>{`Tips: ${i18n(
            'setting.tab.aiType.custom.tips',
          )}`}</div>
        )}
        <div className={styles.bottomButton}>
          <Button type="primary" onClick={handleApplyAiConfig}>
            {i18n('setting.button.apply')}
          </Button>
        </div>
      </>
    );
  };

  return (
    <>
      <div className={styles.aiSqlSource}>
        <div className={styles.aiSqlSourceTitle}>{i18n('setting.title.aiSource')}:</div>
        <Radio.Group onChange={handleAiTypeChange} value={aiConfig?.aiSqlSource}>
          {Object.keys(AIType).map((key) => (
            <Radio key={key} value={key} style={{ marginBottom: '8px' }}>
              {AITypeName[key]}
            </Radio>
          ))}
        </Radio.Group>
      </div>

      {renderAIConfig()}

      {/* {aiConfig?.aiSqlSource === AIType.CHAT2DBAI && !aiConfig.apiKey && <Popularize source="setting" />} */}
    </>
  );
}
