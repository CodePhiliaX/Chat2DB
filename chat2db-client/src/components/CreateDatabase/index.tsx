import React, { useCallback, useMemo, useState, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Form, Input, Modal } from 'antd';
import MonacoEditor, { IExportRefFunction } from '@/components/MonacoEditor';
import { v4 as uuid } from 'uuid';
import sqlService from '@/service/sql';
import i18n from '@/i18n';
import { debounce } from 'lodash';
import { DatabaseTypeCode } from '@/constants';
import { setOpenCreateDatabaseModal } from '@/pages/main/workspace/store/modal';

interface IProps {
  relyOnParams: {
    databaseType: DatabaseTypeCode;
    dataSourceId: number;
    databaseName?: string;
  };
  executedCallback?: () => void;
}

export type CreateType = 'database' | 'schema';

export interface ICreateDatabase {
  databaseName?: string;
  schemaName?: string;
  comment?: string;
}

// 创建database不支持注释的数据库
const noCommentDatabase = [DatabaseTypeCode.MYSQL];

const CreateDatabase = () => {
  const [form] = Form.useForm<ICreateDatabase>();
  const monacoEditorUuid = useMemo(() => uuid(), []);
  const monacoEditorRef = React.useRef<IExportRefFunction>(null);
  const [open, setOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState<{ success: boolean; message: string; originalSql: string } | null>(
    null,
  );
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [createType, setCreateType] = useState<CreateType>('database');
  const [relyOnParams, setRelyOnParams] = useState<IProps['relyOnParams'] | null>(null);
  const executedCallbackRef = React.useRef<IProps['executedCallback']>();

  useEffect(() => {
    if (!open) {
      setErrorMessage(null);
      form.resetFields();
      monacoEditorRef.current?.setValue('', 'cover');
    }
  }, [open]);

  const config = useMemo(() => {
    return createType === 'database'
      ? {
          title: `${i18n('common.title.create')} Database`,
          api: sqlService.getCreateDatabaseSql,
          formName: 'databaseName',
        }
      : {
          title: `${i18n('common.title.create')} Schema`,
          api: sqlService.getCreateSchemaSql,
          formName: 'schemaName',
        };
  }, [createType]);

  const labelCol = { flex: '70px' };

  const handleFieldsChange = useCallback(
    debounce(() => {
      const formData: ICreateDatabase = form.getFieldsValue();
      if (!formData.databaseName && createType === 'database') {
        return;
      }
      if (!formData.schemaName && createType === 'schema') {
        return;
      }
      const params = {
        databaseType: relyOnParams?.databaseType,
        dataSourceId: relyOnParams?.dataSourceId,
        databaseName: relyOnParams?.databaseName,
        ...formData,
      };
      config.api(params as any).then((res) => {
        const { sql } = res;
        monacoEditorRef.current?.setValue(sql, 'cover');
      });
    }, 500),
    [relyOnParams, createType, monacoEditorRef, config],
  );

  const executeUpdateDataSql = (sql: string) => {
    const params: any = {
      dataSourceId: relyOnParams?.dataSourceId,
      databaseType: relyOnParams?.databaseType,
      databaseName: relyOnParams?.databaseName,
      sql,
    };
    setConfirmLoading(true);
    setErrorMessage(null);
    return sqlService
      .executeDDL(params)
      .then((res) => {
        if (res.success) {
          setOpen(false);
          executedCallbackRef.current?.();
        } else {
          setErrorMessage(res);
        }
      })
      .finally(() => {
        setConfirmLoading(false);
      });
  };

  const onOk = () => {
    const sql = monacoEditorRef.current?.getAllContent() || '';
    executeUpdateDataSql(sql);
  };

  const openCreateDatabaseModal = (params: {
    type: CreateType;
    relyOnParams: {
      databaseType: DatabaseTypeCode;
      dataSourceId: number;
      databaseName?: string;
    };
    executedCallback?: () => void;
  }) => {
    setOpen(true);
    setCreateType(params.type);
    setRelyOnParams(params.relyOnParams);
    executedCallbackRef.current = params.executedCallback;
  };

  useEffect(() => {
    setOpenCreateDatabaseModal(openCreateDatabaseModal);
  }, []);

  return (!!relyOnParams && (
    <Modal
      onCancel={() => {
        setOpen(false);
      }}
      title={config.title}
      destroyOnClose
      confirmLoading={confirmLoading}
      open={open}
      onOk={onOk}
    >
      <div className={styles.createDatabaseDom}>
        <Form labelAlign="left" form={form} labelCol={labelCol} onFieldsChange={handleFieldsChange} name="create">
          <Form.Item label={i18n('common.label.name')} name={config.formName}>
            <Input autoComplete="off" />
          </Form.Item>
          {noCommentDatabase.includes(relyOnParams.databaseType) ? null : (
            <Form.Item label={i18n('common.label.comment')} name="comment">
              <Input autoComplete="off" />
            </Form.Item>
          )}
        </Form>
        <div className={styles.previewBox}>
          <div className={styles.previewText}>{i18n('common.title.preview')}</div>
          <div className={styles.previewLine} />
        </div>
        <div className={styles.monacoEditorBox}>
          <MonacoEditor
            ref={monacoEditorRef}
            options={{
              lineNumbers: 'off',
            }}
            id={monacoEditorUuid}
          />
        </div>
        {errorMessage && (
          <>
            <div className={classnames(styles.previewBox, styles.errorBox)}>
              <div className={styles.previewText}>{i18n('common.title.errorMessage')}</div>
              <div className={styles.previewLine} />
            </div>
            <div>{errorMessage.message}</div>
          </>
        )}
      </div>
    </Modal>
  ))

};

export default CreateDatabase;
