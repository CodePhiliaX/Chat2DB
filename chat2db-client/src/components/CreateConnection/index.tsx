import React, { memo, useEffect, useMemo, useState, Fragment, useContext, useCallback, useLayoutEffect } from 'react';
import { i18n, isEn } from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';

import connectionService, { IDriverResponse } from '@/service/connection';

import { DatabaseTypeCode, ConnectionEnvType, databaseMap } from '@/constants';
import { dataSourceFormConfigs } from './config/dataSource';
import { IConnectionConfig, IFormItem, ISelect } from './config/types';
import { IConnectionDetails } from '@/typings';
import { InputType } from './config/enum';
import { deepClone } from '@/utils';
import { Select, Form, Input, message, Table, Button, Collapse, Modal } from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import Driver from './components/Driver';

const { Option } = Select;

type ITabsType = 'ssh' | 'baseInfo' | 'driver';

export enum submitType {
  UPDATE = 'update',
  SAVE = 'save',
  TEST = 'test',
}

interface IProps {
  className?: string;
  closeCreateConnection: () => void;
  connectionData: IConnectionDetails;
  submitCallback?: Function;
}

enum DownloadStatus {
  Default,
  Loading,
  Error,
  Success
}

export default function CreateConnection(props: IProps) {
  const { className, closeCreateConnection, submitCallback, connectionData } = props;
  const [baseInfoForm] = Form.useForm();
  const [sshForm] = Form.useForm();
  const [driveData, setDriveData] = useState<any>({});
  const [backfillData, setBackfillData] = useState<IConnectionDetails>(connectionData);
  const [loadings, setLoading] = useState({
    confirmButton: false,
    testButton: false,
    backfillDataLoading: false
  });
  const dataSourceFormConfigPropsMemo = useMemo<IConnectionConfig>(() => {
    const deepCloneDataSourceFormConfigs = deepClone(dataSourceFormConfigs)
    return deepCloneDataSourceFormConfigs.find((t: IConnectionConfig) => {
      const flag = t.type === backfillData.type;
      return flag
    });
  }, []);

  const [dataSourceFormConfigProps, setDataSourceFormConfigProps] = useState(dataSourceFormConfigPropsMemo);

  useEffect(() => {
    setBackfillData(props.connectionData);
  }, [props.connectionData]);

  useEffect(() => {
    if (backfillData.id) {
      getConnectionDetails(backfillData.id);
    }
  }, [backfillData.id]);

  function getConnectionDetails(id: number) {
    setLoading({
      ...loadings,
      backfillDataLoading: true
    })
    connectionService.getDetails({ id }).then((res) => {
      if (!res) {
        return;
      }
      if (res.user) {
        res.authentication = 1;
      } else {
        res.authentication = 2;
      }
      setBackfillData(res);
    }).finally(() => {
      setTimeout(() => {
        setLoading({
          ...loadings,
          backfillDataLoading: false
        })
      }, 100)
    });
  }

  function driverFormChange(data: any) {
    setDriveData(data)
  }

  const getItems = () => [
    {
      key: 'driver',
      label: i18n('connection.title.driver'),
      children: <Driver backfillData={backfillData} onChange={driverFormChange}></Driver>,
    },
    {
      key: 'ssh',
      label: i18n('connection.label.sshConfiguration'),
      children: (
        <div className={styles.sshBox}>
          <RenderForm
            dataSourceFormConfigProps={dataSourceFormConfigProps}
            backfillData={backfillData!}
            form={sshForm}
            tab="ssh"
          />
          <div className={styles.testSSHConnect}>
            <div onClick={testSSH} className={styles.testSSHConnectText}>
              {i18n('connection.message.testSshConnection')}
            </div>
          </div>
        </div>
      ),
    },
    {
      key: 'extendInfo',
      label: i18n('connection.label.advancedConfiguration'),
      children: (
        <div className={styles.extendInfoBox}>
          <RenderExtendTable backfillData={backfillData!}></RenderExtendTable>
        </div>
      ),
    },
  ];

  // 测试、保存、修改连接
  function saveConnection(type: submitType) {
    const ssh = sshForm.getFieldsValue();
    const baseInfo = baseInfoForm.getFieldsValue();
    const extendInfo: any = [];
    const loadingsButton = type === submitType.TEST ? 'testButton' : 'confirmButton';
    extendTableData.map((t: any) => {
      if (t.label || t.value) {
        extendInfo.push({
          key: t.label,
          value: t.value,
        });
      }
    });

    let p: any = {
      ssh,
      driverConfig: driveData,
      ...baseInfo,
      extendInfo,
      connectionEnvType: ConnectionEnvType.DAILY,
      type: backfillData.type,
    };

    if (type !== submitType.SAVE) {
      p.id = backfillData.id;
    }

    const api: any = connectionService[type](p);

    setLoading({
      ...loadings,
      [loadingsButton]: true,
    });

    api
      .then((res: any) => {
        if (type === submitType.TEST) {
          message.success(
            res === false
              ? i18n('connection.message.testConnectResult', i18n('common.text.failure'))
              : i18n('connection.message.testConnectResult', i18n('common.text.successful')),
          );
        } else {
          submitCallback?.();
          message.success(
            type === submitType.UPDATE
              ? i18n('common.message.modifySuccessfully')
              : i18n('common.message.addedSuccessfully'),
          );

          if (type === submitType.SAVE) {
            setBackfillData({
              ...backfillData,
              id: res,
            });
          }
        }
      })
      .finally(() => {
        setLoading({
          ...loadings,
          [loadingsButton]: false,
        });
      });
  }

  function onCancel() {
    closeCreateConnection();
    // setEditDataSourceData(false)
  }

  function testSSH() {
    let p = sshForm.getFieldsValue();
    connectionService.testSSH(p).then((res) => {
      message.success(i18n('connection.message.testConnectResult', i18n('common.text.successful')));
    });
  }

  return (
    <div className={classnames(styles.box, className)}>
      <LoadingContent className={styles.loadingContent} data={!loadings.backfillDataLoading}>
        <div className={styles.connectionBox}>
          <div className={styles.title}>
            <Iconfont code={databaseMap[backfillData.type]?.icon}></Iconfont>
            <div>{databaseMap[backfillData.type]?.name}</div>
          </div>
          <div className={styles.baseInfoBox}>
            <RenderForm dataSourceFormConfigProps={dataSourceFormConfigProps} backfillData={backfillData!} form={baseInfoForm} tab="baseInfo" />
          </div>
          <Collapse defaultActiveKey={['driver']} items={getItems()} />
          <div className={styles.formFooter}>
            <div className={styles.test}>
              {
                <Button
                  loading={loadings.testButton}
                  onClick={saveConnection.bind(null, submitType.TEST)}
                  className={styles.test}
                >
                  {i18n('connection.button.testConnection')}
                </Button>
              }
            </div>
            <div className={styles.rightButton}>
              <Button onClick={onCancel} className={styles.cancel}>
                {i18n('common.button.cancel')}
              </Button>
              <Button
                className={styles.save}
                type="primary"
                loading={loadings.confirmButton}
                onClick={saveConnection.bind(null, backfillData.id ? submitType.UPDATE : submitType.SAVE)}
              >
                {backfillData.id ? i18n('common.button.edit') : i18n('common.button.save')}
              </Button>
            </div>
          </div>
        </div>
      </LoadingContent>
    </div>
  );
}

interface IRenderFormProps {
  tab: ITabsType;
  form: any;
  backfillData: IConnectionDetails;
  dataSourceFormConfigProps: IConnectionConfig
}

function RenderForm(props: IRenderFormProps) {
  const { tab, form, backfillData, dataSourceFormConfigProps } = props;
  useEffect(() => {
    form.resetFields()
  }, [backfillData.id, backfillData.type])

  let aliasChanged = false;

  const [dataSourceFormConfig, setDataSourceFormConfig] = useState<IConnectionConfig>(dataSourceFormConfigProps);

  useEffect(() => {
    setDataSourceFormConfig(dataSourceFormConfigProps)
  }, [dataSourceFormConfigProps])

  const initialValuesMemo = useMemo(() => {
    return initialFormData(dataSourceFormConfigProps[tab]?.items);
  }, []);

  const [initialValues] = useState(initialValuesMemo);

  useEffect(() => {
    if (!backfillData) {
      return;
    }
    if (tab === 'baseInfo') {
      // TODO:
      // selectChange({ name: 'authentication', value: backfillData.user ? 1 : 2 });
      regEXFormatting({ url: backfillData.url }, backfillData);
    }

    if (tab === 'ssh') {
      regEXFormatting({}, backfillData.ssh || {});
    }
    if (tab === 'driver') {
      regEXFormatting({}, backfillData.driverConfig || {});
    }
  }, [backfillData]);

  function initialFormData(dataSourceFormConfig: IFormItem[] | undefined) {
    let initValue: any = {};
    dataSourceFormConfig?.map((t) => {
      initValue[t.name] = t.defaultValue;
      if (t.selects?.length) {
        t.selects?.map((item) => {
          if (item.value === t.defaultValue) {
            initValue = {
              ...initValue,
              ...initialFormData(item.items),
            };
          }
        });
      }
    });
    return initValue;
  }

  function selectChange(t: { name: string; value: any }) {
    dataSourceFormConfig[tab]?.items.map((j, i) => {
      if (j.name === t.name) {
        j.defaultValue = t.value;
      }
    });
    setDataSourceFormConfig({ ...dataSourceFormConfig });
  }

  function onFieldsChange(data: any, datas: any) {
    // 将antd的格式转换为正常的对象格式
    if (!data.length) {
      return;
    }
    const keyName = data[0].name[0];
    const keyValue = data[0].value;
    const variableData = {
      [keyName]: keyValue,
    };
    const dataObj: any = {};
    datas.map((t: any) => {
      dataObj[t.name[0]] = t.value;
    });
    // 正则拆分url/组建url
    if (tab === 'baseInfo') {
      regEXFormatting(variableData, dataObj);
    }
  }

  function extractObj(url: any) {
    const { template, pattern } = dataSourceFormConfig.baseInfo;
    // 提取关键词对应的内容 value
    const matches = url.match(pattern)!;
    // 提取花括号内的关键词 key
    const reg = /{(.*?)}/g;
    let match;
    const arr = [];
    while ((match = reg.exec(template)) !== null) {
      arr.push(match[1]);
    }
    // key与value一一对应
    const newExtract: any = {};
    arr.map((t, i) => {
      newExtract[t] = t === 'database' ? matches[i + 2] || '' : matches[i + 1];
    });
    return newExtract;
  }

  function regEXFormatting(variableData: { [key: string]: any }, dataObj: { [key: string]: any }) {
    const { template, pattern } = dataSourceFormConfig.baseInfo;
    const keyName = Object.keys(variableData)[0];
    const keyValue = variableData[Object.keys(variableData)[0]];
    let newData: any = {};
    if (keyName === 'url') {
      //先判断url是否符合规定的正则
      if (pattern.test(keyValue)) {
        newData = extractObj(keyValue);
      }
    } else if (keyName === 'alias') {
      aliasChanged = true;
    } else {
      // 改变上边url动
      let url = template;
      Object.keys(dataObj).map((t) => {
        url = url.replace(`{${t}}`, dataObj[t]);
      });
      newData = {
        url,
      };
    }
    if (keyName === 'host' && !aliasChanged) {
      newData.alias = '@' + keyValue;
    }

    form.setFieldsValue({
      ...dataObj,
      ...newData,
    });
  }

  function renderFormItem(t: IFormItem): React.ReactNode {
    const label = isEn ? t.labelNameEN : t.labelNameCN;
    const name = t.name;
    const width = t?.styles?.width || '100%';
    const labelWidth = isEn ? t?.styles?.labelWidthEN || '100px' : t?.styles?.labelWidthCN || '70px';
    const labelAlign = t?.styles?.labelAlign || 'left';

    const FormItemTypes: { [key in InputType]: () => React.ReactNode } = {
      [InputType.INPUT]: () => (
        <Form.Item
          label={label}
          name={name}
          style={{ '--form-label-width': labelWidth } as any}
          labelAlign={labelAlign}
        >
          <Input />
        </Form.Item>
      ),

      [InputType.SELECT]: () => (
        <Form.Item
          label={label}
          name={name}
          style={{ '--form-label-width': labelWidth } as any}
          labelAlign={labelAlign}
        >
          <Select
            value={t.defaultValue}
            onChange={(e) => {
              selectChange({ name: name, value: e });
            }}
          >
            {t.selects?.map((t: ISelect) => (
              <Option key={t.value} value={t.value}>
                {t.label}
              </Option>
            ))}
          </Select>
        </Form.Item>
      ),

      [InputType.PASSWORD]: () => (
        <Form.Item
          label={label}
          name={name}
          style={{ '--form-label-width': labelWidth } as any}
          labelAlign={labelAlign}
        >
          <Input.Password />
        </Form.Item>
      ),
    };

    return (
      <Fragment key={t.name}>
        <div
          key={t.name}
          className={classnames({ [styles.labelTextAlign]: t.labelTextAlign })}
          style={{ width: width }}
        >
          {FormItemTypes[t.inputType]()}
        </div>
        {t.selects?.map((item) => {
          if (t.defaultValue === item.value) {
            return item.items?.map((t) => renderFormItem(t));
          }
        })}
      </Fragment>
    );
  }

  return (
    <Form
      colon={false}
      name={tab}
      form={form}
      initialValues={initialValues}
      className={styles.form}
      autoComplete="off"
      labelAlign="left"
      onFieldsChange={onFieldsChange}
    >
      {dataSourceFormConfig[tab]!.items.map((t) => renderFormItem(t))}
    </Form>
  );
}

interface IRenderExtendTableProps {
  backfillData: IConnectionDetails;
}

let extendTableData: any = [];

interface IExtendTable {
  key: number,
  label: string,
  value: string
}

function RenderExtendTable(props: IRenderExtendTableProps) {
  const { backfillData } = props;
  const databaseType = backfillData.type;
  const [data, setData] = useState<IExtendTable[]>([{ key: 0, label: '', value: '' }]);
  const dataSourceFormConfigMemo = useMemo<IConnectionConfig>(() => {
    return deepClone(dataSourceFormConfigs).find((t: IConnectionConfig) => {
      return t.type === databaseType;
    });
  }, [backfillData.type]);

  useEffect(() => {
    const extendInfoList = backfillData?.extendInfo?.length ? backfillData?.extendInfo : dataSourceFormConfigMemo.extendInfo;

    const extendInfo = extendInfoList?.map((t, i) => {
      return {
        key: i,
        label: t.key,
        value: t.value,
      };
    }) || [];

    setData([...extendInfo, { key: extendInfo.length, label: '', value: '' }])
  }, [dataSourceFormConfigMemo, backfillData])

  useEffect(() => {
    extendTableData = data;
  }, [data]);

  const columns: any = [
    {
      title: i18n('connection.tableHeader.name'),
      dataIndex: 'label',
      width: '60%',
      render: (value: any, row: any, index: number) => {
        let isCustomLabel = true;

        dataSourceFormConfigMemo.extendInfo?.map((item) => {
          if (item.key === row.label) {
            isCustomLabel = false;
          }
        });

        function change(e: any) {
          const newData = [...data];
          newData[index] = {
            key: index,
            label: e.target.value,
            value: '',
          };
          setData(newData);
        }

        function blur() {
          const newData = [];
          data.map((t) => {
            if (t.label) {
              newData.push(t);
            }
          });
          if (index === data.length - 1 && row.label) {
            newData[index] = {
              key: index,
              label: row.label,
              value: '',
            };
          }
          setData([...newData, { key: newData.length, label: '', value: '' }]);
        }

        if (index === data.length - 1 || isCustomLabel) {
          return (
            <Input
              onBlur={blur}
              placeholder={index === data.length - 1 ? i18n('common.text.custom') : ''}
              onChange={change}
              value={value}
            ></Input>
          );
        } else {
          return <span>{value}</span>;
        }
      },
    },
    {
      title: i18n('connection.tableHeader.value'),
      dataIndex: 'value',
      width: '40%',
      render: (value: any, row: any, index: number) => {
        function change(e: any) {
          const newData = [...data];
          newData[index] = {
            key: index,
            label: row.label,
            value: e.target.value,
          };
          setData(newData);
        }

        function blur() { }

        if (index === data.length - 1) {
          return <Input onBlur={blur} disabled placeholder="<value>" onChange={change} value={value}></Input>;
        } else {
          return <Input onChange={change} value={value}></Input>;
        }
      },
    },
  ];

  return (
    <div className={styles.extendTable}>
      <Table bordered size="small" pagination={false} columns={columns} dataSource={data} />
    </div>
  );
}
