import React, { useContext, useEffect, useImperativeHandle, ForwardedRef, forwardRef, useState } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Form, Input, Switch, Select } from 'antd';
import { Context } from '../index';
import { ISequenceInfo } from '@/typings';
import sqlService from '@/service/sql';
import i18n from '@/i18n';

export interface ISequenceInfoRef {
  getSequenceInfo: () => ISequenceInfo;
}

interface IProps {
  className?: string;
}

const BaseInfo = forwardRef((props: IProps, ref: ForwardedRef<ISequenceInfoRef>) => {
  const { className } = props;
  const { sequenceDetails,databaseType,databaseName,dataSourceId,schemaName } = useContext(Context);
  const [userOptions, setUserOptions] = useState<{ value: string; label: string }[]>([]);
  const [form] = Form.useForm();
  useEffect(() => {
    form.setFieldsValue({
      comment: sequenceDetails.comment,
      relname: sequenceDetails.relname,
      typname: sequenceDetails.typname,
      seqcache: sequenceDetails.seqcache,
      rolname: sequenceDetails.rolname,
      seqstart: sequenceDetails.seqstart,
      seqincrement: sequenceDetails.seqincrement,
      seqmax: sequenceDetails.seqmax,
      seqmin: sequenceDetails.seqmin,
      seqcycle: sequenceDetails.seqcycle,
    });
    const params = {
      databaseType,
      databaseName,
      dataSourceId,
      schemaName,
      refresh: true,
    };
    sqlService.
      getDatabaseUserNameList(params).then((res) => {
        setUserOptions(res.map(user => ({value: user,label: user})));
      });
  }, [sequenceDetails]);

  function getSequenceInfo(): ISequenceInfo {
    return form.getFieldsValue();
  }

  function onChange(checked: boolean) {
    form.setFieldsValue({
      seqcycle: checked,
    });
  }

  function handleChange(value: string) {
    form.setFieldsValue({
      typname: value,
    });
  }
  function rolnameChange(value: string) {
    form.setFieldsValue({
      rolname: value,
    });
  }

  useImperativeHandle(ref, () => ({
    getSequenceInfo,
  }));

  return (
    <div className={classnames(className, styles.baseInfo)}>
      <div className={styles.formBox}>
        <Form
          layout="vertical"
          form={form}
          initialValues={{ remember: true }}
          autoComplete="off"
          className={styles.form}
        >
          <Form.Item label={`${i18n('editSequence.label.relname')}:`} name="relname">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.comment')}:`} name="comment">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.typname')}:`} name="typname">
            <Select
              defaultValue={sequenceDetails?.typname ?? 'INTEGER'}
              onChange={handleChange}
              options={[
                { value: 'SMALLINT', label: 'SMALLINT' },
                { value: 'BIGINT', label: 'BIGINT' },
                { value: 'INTEGER', label: 'INTEGER' },
              ]}
            />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqcache')}:`} name="seqcache">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.rolname')}:`} name="rolname">
            <Select
              defaultValue={sequenceDetails?.rolname ?? ''}
              onChange={rolnameChange}
              options={userOptions}
            />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqstart')}:`} name="seqstart">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqincrement')}:`} name="seqincrement">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqmax')}:`} name="seqmax">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqmin')}:`} name="seqmin">
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={`${i18n('editSequence.label.seqcycle')}:`}>
            <Switch defaultChecked onChange={onChange} />
          </Form.Item>
        </Form>
      </div>
    </div>
  );
});

export default BaseInfo;
