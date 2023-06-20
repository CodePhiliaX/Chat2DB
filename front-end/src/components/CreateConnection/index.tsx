import React, { memo, useEffect, useMemo, useState, Fragment, useContext, useCallback, useLayoutEffect } from 'react';
import { i18n, isEn } from '@/i18n'
import styles from './index.less';
import classnames from 'classnames';

import connectionService from '@/service/connection';

import { DatabaseTypeCode, ConnectionEnvType, databaseMap } from '@/constants/database';
import { dataSourceFormConfigs } from './config/dataSource';
import { IConnectionConfig, IFormItem, ISelect } from './config/types';
import { IConnectionDetails } from '@/typings/connection'
import { InputType } from './config/enum';
import { deepClone } from '@/utils';
import {
  Select,
  Form,
  Input,
  message,
  Table,
  Button,
  Collapse,
} from 'antd';
import Iconfont from '@/components/Iconfont';
import LoadingContent from '@/components/Loading/LoadingContent';
import { useTheme } from '@/hooks/useTheme';

const { Option } = Select;

type ITabsType = 'ssh' | 'baseInfo'

export enum submitType {
  UPDATE = 'update',
  SAVE = 'save',
  TEST = 'test'
}

interface IProps {
  className?: string;
  closeCreateConnection: () => void;
  createType?: DatabaseTypeCode;
  editId?: number;
  submitCallback?: Function;
}

export default function CreateConnection(props: IProps) {
  const { className, closeCreateConnection, createType, editId, submitCallback } = props;
  const [baseInfoForm] = Form.useForm();
  const [sshForm] = Form.useForm();
  const [backfillData, setBackfillData] = useState<IConnectionDetails>();
  const [loadings, setLoading] = useState({
    confirmButton: false,
    testButton: false,
  });

  const [currentType, setCurrentType] = useState<DatabaseTypeCode>(createType || DatabaseTypeCode.MYSQL);

  useEffect(() => {
    if (editId) {
      getConnectionDetails(editId);
    }
  }, [editId])

  function getConnectionDetails(id: number) {
    setBackfillData(undefined);
    connectionService.getDetails({ id }).then((res) => {
      if (res.user) {
        res.authentication = 1;
      } else {
        res.authentication = 2;
      }
      setTimeout(() => {
        setBackfillData(res);
      }, 300);
      setCurrentType(res.type);
    })
  }


  const getItems = () => [
    {
      key: 'ssh',
      label: 'SSH Configuration',
      children: <div className={styles.sshBox}>
        <RenderForm backfillData={backfillData!} form={sshForm} tab='ssh' databaseType={currentType} editId={editId} />
        <div className={styles.testSSHConnect}>
          <div onClick={testSSH} className={styles.testSSHConnectText}>
            {i18n('connection.message.testSshConnection')}
          </div>
        </div>
      </div>
    },
    {
      key: 'extendInfo',
      label: 'Advanced Configuration',
      children: <div className={styles.extendInfoBox}>
        <RenderExtendTable backfillData={backfillData!} databaseType={currentType}></RenderExtendTable>
      </div>
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
          value: t.value
        })
      }
    });

    let p: any = {
      ssh,
      ...baseInfo,
      extendInfo,
      // ...values,
      ConnectionEnvType: ConnectionEnvType.DAILY,
      type: createType!
    }

    if (type !== submitType.SAVE) {
      p.id = editId;
    }

    const api: any = connectionService[type](p);

    setLoading({
      ...loadings,
      [loadingsButton]: true
    })

    api.then((res: any) => {
      if (type === submitType.TEST) {
        message.success(res === false ? i18n('connection.message.testConnectResult', i18n('common.text.failure')) : i18n('connection.message.testConnectResult', i18n('common.text.successful')));
      } else {
        submitCallback?.();
        message.success(type === submitType.UPDATE ? i18n('common.message.modifySuccessfully') : i18n('common.message.addedSuccessfully'))
      }
    }).finally(() => {
      setLoading({
        ...loadings,
        [loadingsButton]: false
      })
    })
  }

  function onCancel() {
    closeCreateConnection()
    // setEditDataSourceData(false)
  }

  function testSSH() {
    let p = sshForm.getFieldsValue();
    connectionService.testSSH(p).then(res => {
      message.success(i18n('connection.message.testConnectResult', i18n('common.text.successful')))
    })
  }

  return <div className={classnames(styles.box, className)}>
    <LoadingContent className={styles.loadingContent} data={backfillData || createType}>
      <div className={styles.connectionBox}>
        <div className={styles.title}>
          <Iconfont code={databaseMap[currentType]?.icon}></Iconfont>
          <div>
            {`${editId ? i18n('connection.title.editConnection') : i18n('connection.title.createConnection')} ${databaseMap[currentType]?.name}`}
          </div>
        </div>
        <div className={styles.baseInfoBox}>
          <RenderForm backfillData={backfillData!} form={baseInfoForm} tab='baseInfo' databaseType={currentType} editId={editId} />
        </div>
        <Collapse items={getItems()} />
        <div className={styles.formFooter}>
          <div className={styles.test}>
            {
              <Button
                loading={loadings.testButton}
                onClick={saveConnection.bind(null, submitType.TEST)}
                className={styles.test}>
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
              onClick={saveConnection.bind(null, editId ? submitType.UPDATE : submitType.SAVE)}
            >
              {
                editId ? i18n('common.button.edit') : i18n('connection.button.connect')
              }
            </Button>
          </div>
        </div>
      </div>
    </LoadingContent>
  </div >
}

interface IRenderFormProps {
  editId: number | undefined,
  databaseType: DatabaseTypeCode,
  tab: ITabsType;
  form: any;
  backfillData: IConnectionDetails;
}

function RenderForm(props: IRenderFormProps) {
  const { editId, databaseType, tab, form, backfillData } = props;

  let aliasChanged = false;

  const dataSourceFormConfigMemo = useMemo<IConnectionConfig>(() => {
    return deepClone(dataSourceFormConfigs).find((t: IConnectionConfig) => {
      return t.type === databaseType
    })
  }, [])

  const [dataSourceFormConfig, setDataSourceFormConfig] = useState<IConnectionConfig>(dataSourceFormConfigMemo);

  const initialValuesMemo = useMemo(() => {
    return initialFormData(dataSourceFormConfigMemo[tab].items)
  }, [])

  const [initialValues] = useState(initialValuesMemo);

  useEffect(() => {
    if (!backfillData) {
      return
    }
    if (tab === 'baseInfo') {
      selectChange({ name: 'authentication', value: backfillData.user ? 1 : 2 });
      regEXFormatting({ url: backfillData.url }, backfillData)
    }

    if (tab === 'ssh') {
      regEXFormatting({}, backfillData.ssh || {})
    }
  }, [backfillData])

  function initialFormData(dataSourceFormConfig: IFormItem[] | undefined) {
    let initValue: any = {};
    dataSourceFormConfig?.map(t => {
      initValue[t.name] = t.defaultValue
      if (t.selects?.length) {
        t.selects?.map(item => {
          if (item.value === t.defaultValue) {
            initValue = {
              ...initValue,
              ...initialFormData(item.items)
            }
          }
        })
      }
    })
    return initValue
  }

  function selectChange(t: { name: string, value: any }) {
    dataSourceFormConfig[tab].items.map((j, i) => {
      if (j.name === t.name) {
        j.defaultValue = t.value
      }
    })
    setDataSourceFormConfig({ ...dataSourceFormConfig })
  }

  function onFieldsChange(data: any, datas: any) {
    // 将antd的格式转换为正常的对象格式
    if (!data.length) {
      return
    }
    const keyName = data[0].name[0];
    const keyValue = data[0].value;
    const variableData = {
      [keyName]: keyValue
    }
    const dataObj: any = {}
    datas.map((t: any) => {
      dataObj[t.name[0]] = t.value
    })
    // 正则拆分url/组建url
    if (tab === 'baseInfo') {
      regEXFormatting(variableData, dataObj);
    }
  }

  function extractObj(url: any) {
    const { template, pattern } = dataSourceFormConfig.baseInfo
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
    const newExtract: any = {}
    arr.map((t, i) => {
      newExtract[t] = t === 'database' ? (matches[i + 2] || '') : matches[i + 1]
    })
    return newExtract
  }

  function regEXFormatting(variableData: { [key: string]: any }, dataObj: { [key: string]: any }) {
    const { template, pattern } = dataSourceFormConfig.baseInfo
    const keyName = Object.keys(variableData)[0]
    const keyValue = variableData[Object.keys(variableData)[0]]
    let newData: any = {}
    if (keyName === 'url') {
      //先判断url是否符合规定的正则
      if (pattern.test(keyValue)) {
        newData = extractObj(keyValue);
      }
    } else if (keyName === 'alias') {
      aliasChanged = true
    } else {
      // 改变上边url动
      let url = template;
      Object.keys(dataObj).map(t => {
        url = url.replace(`{${t}}`, dataObj[t])
      })
      newData = {
        url
      }
    }
    if (keyName === 'host' && !aliasChanged) {
      newData.alias = '@' + keyValue
    }
    console.log({
      ...dataObj,
      ...newData,
    })
    form.setFieldsValue({
      ...dataObj,
      ...newData,
    });
  }

  function renderFormItem(t: IFormItem): React.ReactNode {
    const label = isEn ? t.labelNameEN : t.labelNameCN;
    const name = t.name;
    const width = t?.styles?.width || '100%';
    const labelWidth = isEn ? (t?.styles?.labelWidthEN || '100px') : (t?.styles?.labelWidthCN || '70px');
    const labelAlign = t?.styles?.labelAlign || 'left';

    const FormItemTypes: { [key in InputType]: () => React.ReactNode } = {
      [InputType.INPUT]: () => <Form.Item
        label={label}
        name={name}
        style={{ '--form-label-width': labelWidth } as any}
        labelAlign={labelAlign}
      >
        <Input />
      </Form.Item >,

      [InputType.SELECT]: () => <Form.Item
        label={label}
        name={name}
        style={{ '--form-label-width': labelWidth } as any}
        labelAlign={labelAlign}
      >
        <Select value={t.defaultValue} onChange={(e) => { selectChange({ name: name, value: e }) }}>
          {t.selects?.map((t: ISelect) => <Option key={t.value} value={t.value}>{t.label}</Option>)}
        </Select>
      </Form.Item>,

      [InputType.PASSWORD]: () => <Form.Item
        label={label}
        name={name}
        style={{ '--form-label-width': labelWidth } as any}
        labelAlign={labelAlign}
      >
        <Input.Password />
      </Form.Item>
    }

    return <Fragment key={t.name}>
      <div key={t.name} className={classnames({ [styles.labelTextAlign]: t.labelTextAlign })} style={{ width: width }}>
        {FormItemTypes[t.inputType]()}
      </div>
      {
        t.selects?.map(item => {
          if (t.defaultValue === item.value) {
            return item.items?.map(t => renderFormItem(t))
          }
        })
      }
    </Fragment>
  }

  return <Form
    colon={false}
    name={tab}
    form={form}
    initialValues={initialValues}
    className={styles.form}
    autoComplete='off'
    labelAlign='left'
    onFieldsChange={onFieldsChange}
  >
    {dataSourceFormConfig[tab]!.items.map((t => renderFormItem(t)))}
  </Form>
}
interface IRenderExtendTableProps {
  databaseType: DatabaseTypeCode;
  backfillData: IConnectionDetails;
}

let extendTableData: any = []

function RenderExtendTable(props: IRenderExtendTableProps) {
  const { databaseType, backfillData } = props;
  const dataSourceFormConfigMemo = useMemo<IConnectionConfig>(() => {
    return deepClone(dataSourceFormConfigs).find((t: IConnectionConfig) => {
      return t.type === databaseType
    })
  }, [])

  const extendInfo = dataSourceFormConfigMemo.extendInfo?.map((t, i) => {
    return {
      key: i,
      label: t.key,
      value: t.value
    }
  }) || []

  const [data, setData] = useState([...extendInfo, { key: extendInfo.length, label: '', value: '' }])

  useEffect(() => {
    const backfillDataExtendInfo = backfillData?.extendInfo.map((t, i) => {
      return {
        key: i,
        label: t.key,
        value: t.value
      }
    }) || []
    setData([...backfillDataExtendInfo, { key: extendInfo.length, label: '', value: '' }])
  }, [backfillData])

  useEffect(() => {
    extendTableData = data
  }, [data])

  const columns: any = [
    {
      title: i18n('connection.tableHeader.name'),
      dataIndex: 'label',
      width: '60%',
      render: (value: any, row: any, index: number) => {
        let isCustomLabel = true

        dataSourceFormConfigMemo.extendInfo?.map(item => {
          if (item.key === row.label) {
            isCustomLabel = false
          }
        })

        function change(e: any) {
          const newData = [...data]
          newData[index] = {
            key: index,
            label: e.target.value,
            value: ''
          }
          setData(newData)
        }

        function blur() {
          const newData = []
          data.map(t => {
            if (t.label) {
              newData.push(t)
            }
          })
          if (index === data.length - 1 && row.label) {
            newData[index] = {
              key: index,
              label: row.label,
              value: ''
            }
          }
          setData([...newData, { key: newData.length, label: '', value: '' }])
        }

        if (index === data.length - 1 || isCustomLabel) {
          return <Input onBlur={blur} placeholder={index === data.length - 1 ? i18n('common.text.custom') : ''} onChange={change} value={value}></Input>
        } else {
          return <span>{value}</span>
        }
      }
    },
    {
      title: i18n('connection.tableHeader.value'),
      dataIndex: 'value',
      width: '40%',
      render: (value: any, row: any, index: number) => {
        function change(e: any) {
          const newData = [...data]
          newData[index] = {
            key: index,
            label: row.label,
            value: e.target.value
          }
          setData(newData)
        }

        function blur() {

        }

        if (index === data.length - 1) {
          return <Input onBlur={blur} disabled placeholder='<value>' onChange={change} value={value}></Input>
        } else {
          return <Input onChange={change} value={value}></Input>
        }
      }
    },
  ];

  return <div className={styles.extendTable}>
    <Table
      bordered
      size="small"
      pagination={false}
      columns={columns}
      dataSource={data}
    />
  </div>
}