import React, { useCallback, useEffect, useState } from 'react';
import { Button, Form, Input } from 'antd';
import { getLocationHash } from '@/utils';
import './index.less';
import { getUser, userLogin } from '@/service/user';
import { history } from 'umi';
const path = require('path');

interface IFormData {
  userName: string;
  password: string;
}

const App: React.FC = () => {
  // const handleLogin = useCallback(async (values: IFormData) => {
  //   let res = await userLogin(values);
  //   const params = getLocationHash();
  //   const href = '#' + (params?.callback ?? '');
  //   history.push('/');
  // }, []);
  const [formData, setFormData] = useState<{ userName: string; password: string; } | {}>({});
  function handleLogin() {
    userLogin(formData).then(res => {
      const params = getLocationHash();
      if (window._ENV === 'desktop') {
        history.push('/')
      } else {
        window.location.href = '/'
      }
      // console.log(path.join(__dirname, '#' + (params?.callback || '/')))
    })
  }

  // return (
  //   <div className="login">
  //     <Form
  //       size="large"
  //       name="login"
  //       className="login-form"
  //       labelCol={{ span: 8 }}
  //       wrapperCol={{ span: 16 }}
  //       onFinish={(values: IFormData) => {
  //         handleLogin(values);
  //       }}
  //     >
  //       <div className="logo">ChatDB</div>
  //       <Form.Item
  //         label="用户名"
  //         name="userName"
  //         rules={[{ required: true, message: '请输入用户名' }]}
  //       >
  //         <Input placeholder="默认用户名: dbhub" />
  //       </Form.Item>
  //       <Form.Item
  //         label="密码"
  //         name="password"
  //         rules={[{ required: true, message: '请输入密码' }]}
  //       >
  //         <Input type="password" placeholder="默认密码: dbhub" />
  //       </Form.Item>

  //       <Button type="primary" htmlType="submit" className="login-form-button">
  //         登 录
  //       </Button>
  //     </Form>
  //   </div>
  // );
  return <div className='box'>
    <div className="form-box">
      <form className="form">
        <span className="title">欢迎登陆Chat2DB</span>
        <span className="subtitle"></span>
        <div className="form-container">
          <input autoComplete='off' type="text" onChange={(e) => { setFormData({ ...formData, userName: e.target.value }) }} className="input" placeholder="UserName" />
          <input autoComplete='new-password' type="password" onChange={(e) => { setFormData({ ...formData, password: e.target.value }) }} className="input" placeholder="Password" />
        </div>
        <div className='button' onClick={handleLogin}>登陆</div>
      </form>
    </div>
  </div>
};

export default App;
