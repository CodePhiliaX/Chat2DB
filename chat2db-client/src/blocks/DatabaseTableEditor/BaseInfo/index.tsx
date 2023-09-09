import React, { memo, useState, useContext, useEffect, useImperativeHandle, ForwardedRef, forwardRef } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Form, Input } from 'antd';
import { Context } from '../index';

export interface IBaseInfoRef {
  getBaseInfo: () => IBaseInfo;
}

interface IProps {
  className?: string;
}

export interface IBaseInfo {
  name: string;
  comment: string;
}

const BaseInfo = forwardRef((props: IProps, ref: ForwardedRef<IBaseInfoRef>) => {
  const { className } = props;
  const { tableDetails } = useContext(Context);
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue({
      name: tableDetails.name,
      comment: tableDetails.comment,
    });
  }, [tableDetails]);

  function getBaseInfo(): IBaseInfo {
    return form.getFieldsValue();
  }

  useImperativeHandle(ref, () => ({
    getBaseInfo,
  }));

  return (
    <div className={classnames(className, styles.box)}>
      <div className={styles.formBox}>
        <Form form={form} initialValues={{ remember: true }} autoComplete="off" className={styles.form}>
          <Form.Item label="表名" name="name">
            <Input />
          </Form.Item>
          <Form.Item label="注释" name="comment">
            <Input />
          </Form.Item>
        </Form>
      </div>
    </div>
  );
});

export default BaseInfo
