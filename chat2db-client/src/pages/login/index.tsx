import { Button, Form, Input, Tooltip } from 'antd';
import React, { useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
import { logoutClearSomeLocalStorage, navigate } from '@/utils';

import Iconfont from '@/components/Iconfont';
import LogoImg from '@/assets/logo/logo.png';
import Setting from '@/blocks/Setting';
import i18n from '@/i18n';
import { queryCurUser } from '@/store/user';
import styles from './index.less';
import { userLogin } from '@/service/user';

interface IFormData {
  userName: string;
  password: string;
}

const Login: React.FC = () => {
  useEffect(() => {
    logoutClearSomeLocalStorage();
  }, []);

  const handleLogin = async (formData: IFormData) => {
    const token = await userLogin(formData);
    // 这里兼容token的null的情况，这里配置一下token内容
    if(!token){
        navigate('/');
        return
    }
   
    const res = await queryCurUser();
    if (token && res) {
      navigate('/');
    }
  };

  return (
    <div className={styles.loginPage}>
      <div className={styles.logo}>
        <img className={styles.logoImage} src={LogoImg} />
        <div className={styles.logoText}>Chat2DB</div>
      </div>
      <div className={styles.loginPlane}>
        <div className={styles.loginWelcome}>{i18n('login.text.welcome')}</div>
        <Tooltip
          placement="right"
          color={window._AppThemePack?.colorBgBase}
          title={
            <div style={{ color: window._AppThemePack?.colorText, opacity: 0.8, padding: '8px 4px' }}>
              {i18n('login.text.tips')}
            </div>
          }
        >
          <div className={styles.whyLogin}>{i18n('login.text.tips.title')}</div>
        </Tooltip>

        <Form className={styles.loginForm} size="large" onFinish={handleLogin}>
          <Form.Item
            className={styles.loginFormItem}
            name="userName"
            rules={[{ required: true, message: i18n('login.form.user.placeholder') }]}
          >
            <Input autoComplete="off" placeholder={i18n('login.form.user')} />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: i18n('login.form.password.placeholder') }]}>
            <Input.Password placeholder={i18n('login.form.password')} />
          </Form.Item>
          <div className={styles.defaultPasswordTips}>{i18n('login.tips.defaultPassword')}</div>
          <Button type="primary" htmlType="submit" className={styles.loginFormSubmit}>
            {i18n('login.button.login')}
          </Button>
        </Form>
      </div>

      <Setting
        className={styles.setting}
        noLogin
        render={
          <Button
            type="text"
            icon={<Iconfont style={{ fontSize: '14px' }} code="&#xe630;" />}
            className={styles.settingBtn}
          >
            {i18n('login.text.setting')}
          </Button>
        }
      />
    </div>
  );
};

export default Login;
