import React, { useEffect, useState } from 'react';
import configService from '@/service/config';
import { AIType } from '@/typings/ai';
import { Alert, Button, Form, Input, Radio, RadioChangeEvent } from 'antd';
import i18n from '@/i18n';
import { IAiConfig } from '@/typings/setting';
import { IRole } from '@/typings/user';
import { AIFormConfig, AITypeName } from './aiTypeConfig';
import styles from './index.less';
import { useUserStore } from '@/store/user'

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
  const { userInfo } = useUserStore(state => {
    return {
      userInfo: state.curUser
    }
  })

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
              placeholder={AIFormConfig[aiConfig?.aiSqlSource]?.[key]}
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

      {/* {aiConfig?.aiSqlSource === AIType.CHAT2DBAI && !aiConfig.apiKey && <Popularize source="setting" />} */}
    </>
  );
}
