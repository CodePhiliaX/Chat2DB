import React, { useCallback, forwardRef, ForwardedRef, useImperativeHandle, useMemo, useState, useEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import {Form, Input, Modal, Select} from 'antd';
import MonacoEditor, { IExportRefFunction } from '@/components/Console/MonacoEditor';
import { v4 as uuid } from 'uuid';
import sqlService from '@/service/sql';
import i18n from '@/i18n';
import { debounce } from 'lodash';
import { DatabaseTypeCode } from '@/constants';

interface IProps {
  className?: string;
  curWorkspaceParams: any;
  executedCallback?: () => void;
}

interface IOption {
  label: string;
  value: string | number | null;
}

export type CreateType = 'database' | 'schema';

export interface ICreateDatabaseRef {
  setOpen: (open: boolean, type?: CreateType) => void;
}

export interface ICreateDatabase {
  databaseName?: string;
  schemaName?: string;
  comment?: string;
}

export interface IDatabaseCharsetList {
  charsets: IOption[];
}

// 创建database不支持注释的数据库
const noCommentDatabase = [DatabaseTypeCode.MYSQL];
// 支持charset的数据库
const supportCharset = [DatabaseTypeCode.MYSQL];

export default forwardRef((props: IProps, ref: ForwardedRef<ICreateDatabaseRef>) => {
  const { className, curWorkspaceParams, executedCallback } = props;
  const [form] = Form.useForm<ICreateDatabase>();
  const monacoEditorUuid = useMemo(() => uuid(), []);
  const monacoEditorRef = React.useRef<IExportRefFunction>(null);
  const [open, setOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState<{ success: boolean; message: string; originalSql: string } | null>(
    null,
  );
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [createType, setCreateType] = useState<CreateType>('database');
  const [databaseCharsetList, setDatabaseCharsetList] = useState<IDatabaseCharsetList>({
    charsets: [],
  });

  useEffect(() => {
    if (!open) {
      setErrorMessage(null);
      form.resetFields();
      monacoEditorRef.current?.setValue('', 'cover');
    }
  }, [open]);

  useEffect(() => {
    if (curWorkspaceParams.databaseType && databaseCharsetList.charsets.length === 0) {
      getDatabaseCharsetList();
    }
  }, [curWorkspaceParams])

  const initialOption = {
    label: '',
    value: '',
  };
  const getDatabaseCharsetList = () => {
    sqlService
        .getDatabaseCharsetList(curWorkspaceParams)
        .then((res) => {
          const charsets = [initialOption,
              ...(res?.charsets?.map((i) => {
                return {
                  label: i.charsetName,
                  value: i.charsetName,
                };
              }) || [])];
          setDatabaseCharsetList({
            charsets,
          });
        });
  }

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

  const exposedSetOpen = (_open: boolean, type?: CreateType) => {
    setOpen(_open);
    setCreateType(type || 'database');
  };

  useImperativeHandle(ref, () => ({
    setOpen: exposedSetOpen,
  }));

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
        databaseType: curWorkspaceParams.databaseType,
        dataSourceId: curWorkspaceParams.dataSourceId,
        databaseName: curWorkspaceParams.databaseName,
        ...formData,
      };
      config.api(params).then((res) => {
        const { sql } = res;
        monacoEditorRef.current?.setValue(sql, 'cover');
      });
    }, 500),
    [curWorkspaceParams, createType, monacoEditorRef, config],
  );

  const executeUpdateDataSql = (sql: string) => {
    const params: any = {
      dataSourceId: curWorkspaceParams.dataSourceId,
      databaseType: curWorkspaceParams.databaseType,
      databaseName: curWorkspaceParams.databaseName,
      sql,
    };
    setConfirmLoading(true);
    setErrorMessage(null);
    return sqlService
      .executeDDL(params)
      .then((res) => {
        if (res.success) {
          setOpen(false);
          executedCallback?.();
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

  return (
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
      <div className={classnames(styles.box, className)}>
        <Form labelAlign="left" form={form} labelCol={labelCol} onFieldsChange={handleFieldsChange} name="create">
          <Form.Item label={i18n('common.label.name')} name={config.formName}>
            <Input autoComplete="off" />
          </Form.Item>
          <Form.Item label={i18n('common.label.charset')} name="charset">
            <Select
                bordered={false}
                placeholder="请选择字符集"
                showSearch
                popupMatchSelectWidth={false}
                options={databaseCharsetList.charsets}
            />
          </Form.Item>
          {noCommentDatabase.includes(curWorkspaceParams.databaseType) ? null : (
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
  );
});
