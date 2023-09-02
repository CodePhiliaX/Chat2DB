import React, { useState } from 'react';
import { Button, Form, Input, Tooltip } from 'antd';
import { getUser, userLogin } from '@/service/user';
import { history } from 'umi';
import LogoImg from '@/assets/logo/logo.png';
import styles from './index.less';
import Setting from '@/blocks/Setting';
import Iconfont from '@/components/Iconfont';

interface IFormData {
  userName: string;
  password: string;
}

const App: React.FC = () => {
  const handleLogin = async (formData: { userName: string; password: string }) => {
    let res = await userLogin(formData);
    if (res) {
      window.location.href = '/';
    }
  };

  return (
    <div className={styles.loginPage}>
      <div className={styles.logo}>
        <img className={styles.logoImage} src={LogoImg} />
        <div className={styles.logoText}>Chat2DB</div>
      </div>
      <div className={styles.loginPlane}>
        <div className={styles.loginWelcome}>欢迎使用 Chat2DB</div>
        <Tooltip
          placement="right"
          color={window._AppThemePack.colorBgBase}
          title={
            <div style={{ color: window._AppThemePack.colorText, opacity: 0.8, padding: '8px 4px' }}>
              Chat2DB 账号仅用于团队协作管理
            </div>
          }
        >
          <div className={styles.whyLogin}>为什么需要登录？</div>
        </Tooltip>

        <Form className={styles.loginForm} size="large" onFinish={handleLogin}>
          <Form.Item
            className={styles.loginFormItem}
            name="userName"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input autoComplete="off" placeholder="用户名" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password placeholder="密码" />
          </Form.Item>
          <Button type="primary" htmlType="submit" className={styles.loginFormSubmit}>
            登 录
          </Button>
        </Form>
      </div>

      <Setting
        className={styles.setting}
        render={
          <Button
            type="text"
            icon={<Iconfont style={{ fontSize: '14px' }} code="&#xe630;" />}
            className={styles.settingBtn}
          >
            设 置
          </Button>
        }
      />
    </div>
  );
};

export default App;
