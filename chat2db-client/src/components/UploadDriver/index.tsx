import React, { memo, useEffect, useState, useRef } from 'react';
import i18n from '@/i18n'
import styles from './index.less';
import classnames from 'classnames';
import { Button, message, Upload, Form, Input } from 'antd';
import type { UploadProps } from 'antd';
import connectionService from '@/service/connection';
import { DatabaseTypeCode } from '@/constants'

interface IProps {
  className?: string;
  databaseType: DatabaseTypeCode;
  formChange: Function;
  jdbcDriverClass: string | undefined;
}

export default memo<IProps>(function UploadDriver(props) {
  const { className, databaseType = DatabaseTypeCode.MYSQL, formChange, jdbcDriverClass } = props;
  const [formData, setFormData] = useState<any>({
    dbType: databaseType,
    jdbcDriverClass: jdbcDriverClass,
    jdbcDriver: []
  });

  const uploadProps: UploadProps = {
    name: 'multipartFiles',
    action: `${window._BaseURL}/api/jdbc/driver/upload`,
    multiple: true,
    onChange(info) {
      if (info.file.percent === 100 && info.file?.response?.data?.[0]) {
        setFormData({
          ...formData,
          jdbcDriver: [...(formData.jdbcDriver), info.file?.response?.data?.[0]]
        })
      }
    },
    accept: "application/java-archive"
  };

  useEffect(() => {
    formChange(formData)
  }, [formData])

  function onChange(e: any) {
    setFormData({
      ...formData,
      jdbcDriverClass: e.target.value
    })
  }

  return <div className={classnames(styles.box, className)}>
    <div>
      <Form
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 16 }}
      >
        <Form.Item label="Class">
          <Input value={formData.jdbcDriverClass} onChange={onChange} />
        </Form.Item>
        <Form.Item label={i18n('connection.title.uploadDriver')}>
          <Upload {...uploadProps}>
            <Button>{i18n('connection.button.clickUpload')}</Button>
          </Upload>
        </Form.Item>
      </Form>
    </div>
  </div >
})
