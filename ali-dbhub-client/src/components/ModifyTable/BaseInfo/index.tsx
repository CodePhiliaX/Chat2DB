import React, { memo, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import {
  Select,
  Button,
  Modal,
  Form,
  Input,
  message,
  Radio,
  // Menu,
} from 'antd';

interface IProps {
  className?: string;
}

export const basicInfo = {
  data: {}
}

export default memo<IProps>(function BaseInfo({ className }) {
  const [form] = Form.useForm();

  function onChangeForm(type: string) {
    basicInfo.data = {
      ...form.getFieldsValue()
    }
    console.log(basicInfo)
  }

  return <div className={classnames(className, styles.box)}>
    <div className={styles.formBox}>
      <Form
        form={form}
        initialValues={{ remember: true }}
        autoComplete="off"
        className={styles.form}
      >
        <Form.Item
          label="表名"
          name="name"
        >
          <Input onChange={() => { onChangeForm('name') }} />
        </Form.Item>
        <Form.Item
          label="注释"
          name="comment"
        >
          <Input onChange={() => { onChangeForm('comment') }} />
        </Form.Item>
      </Form>
    </div>
  </div>
})
