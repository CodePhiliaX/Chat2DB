import React, { memo, useEffect, useMemo, useState, Fragment, useContext, useCallback, useLayoutEffect } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Button from '@/components/Button';
import { DatabaseTypeCode, databaseTypeList, TreeNodeType, EnvType } from '@/utils/constants';
import { ITreeNode, IConnectionBase } from '@/types';
import connectionServer from '@/service/connection'
import { dataSourceFormConfigs } from '@/config/dataSource';
import { IDataSourceForm, IFormItem, ISelect } from '@/config/types';
import { DatabaseContext } from '@/context/database';
import { InputType } from '@/config/enum';
import { deepClone } from '@/utils'
import {
  Select,
  Modal,
  Form,
  Input,
  message,
  Table,
  Radio,
  // Menu,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import Tabs, { ITab } from '@/components/Tabs';
import Iconfont from '../Iconfont';
import { useUpdateEffect } from '@/utils/hooks';

const { Option } = Select;

type ITabsType = 'ssh' | 'baseInfo'

export enum submitType {
  UPDATE = 'update',
  SAVE = 'save',
  TEST = 'test'
}

export interface IEditDataSourceData {
  dataType: DatabaseTypeCode,
  id?: number
}

interface IProps {
  className?: string;
  submitCallback?: (data: ITreeNode) => void;
}

const tabsConfig = [
  {
    label: '常规',
    key: 'baseInfo'
  },
  {
    label: 'SSH',
    key: 'ssh'
  },
  {
    label: '高级',
    key: 'extendInfo'
  },
]

function VisiblyCreateConnection(props: IProps) {
  const { className, submitCallback } = props;
  const { model, setEditDataSourceData, setRefreshTreeNum, setModel } = useContext(DatabaseContext);
  const editDataSourceData: IEditDataSourceData = model.editDataSourceData as IEditDataSourceData
  const dataSourceId = editDataSourceData.id;
  const dataSourceType = editDataSourceData.dataType;
  const [baseInfoForm] = Form.useForm();
  const [sshForm] = Form.useForm();
  const [currentTab, setCurrentTab] = useState<ITab>(tabsConfig[0]);
  const [backfillData, setBackfillData] = useState({});
  const [loadings, setLoading] = useState({
    confirmButton: false,
    testButton: false,
  });

  useEffect(() => {
    if (dataSourceId) {
      connectionServer.getDetails({ id: dataSourceId + '' }).then((res: any) => {
        if (res.user) {
          res.authentication = 1
        } else {
          res.authentication = 2
        }
        setBackfillData(res)
        // selectChange({ name: 'authentication', value: res.user ? 1 : 2 });
        // regEXFormatting({ url: res.url }, res)
      })
    }
  }, [])

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
    })

    let p: any = {
      ssh,
      ...baseInfo,
      extendInfo,
      // ...values,
      EnvType: EnvType.DAILY,
      type: dataSourceType!
    }

    if (type === submitType.UPDATE) {
      p.id = dataSourceId;
    }

    const api: any = connectionServer[type](p);
    setLoading({
      ...loadings,
      [loadingsButton]: true
    })
    api.then((res: any) => {
      if (type === submitType.TEST) {
        message.success(res === false ? '测试连接失败' : '测试连接成功');
      } else {
        setModel({
          ...model,
          editDataSourceData: false,
          refreshTreeNum: new Date().getTime(),
        })
      }
    }).finally(() => {
      setLoading({
        ...loadings,
        [loadingsButton]: false
      })
    })
  }

  function onCancel() {
    setEditDataSourceData(false)
  }

  function changeTabs(key: string, index: number) {
    setCurrentTab(tabsConfig[index])
  }

  function testSSH() {
    let p = sshForm.getFieldsValue();
    connectionServer.testSSH(p).then(res => {
      message.success('测试连接成功')
    })
  }

  return <div className={classnames(styles.box, className)}>
    <Modal
      title={dataSourceId ? "修改数据源" : "连接数据源"}
      open={!!editDataSourceData}
      onCancel={onCancel}
      footer={false}
      width={560}
      maskClosable={false}
    >
      <Tabs className={styles.tabsBox} tabs={tabsConfig} onChange={changeTabs}></Tabs>
      <div className={classnames(styles.baseInfoBox, { [styles.showFormBox]: currentTab.key === 'baseInfo' })}>
        <RenderForm backfillData={backfillData} form={baseInfoForm} tab='baseInfo' dataSourceType={dataSourceType} dataSourceId={dataSourceId} ></RenderForm>
      </div>
      <div className={classnames(styles.sshBox, { [styles.showFormBox]: currentTab.key === 'ssh' })}>
        <RenderForm backfillData={backfillData} form={sshForm} tab='ssh' dataSourceType={dataSourceType} dataSourceId={dataSourceId} ></RenderForm>
        <div className={styles.testSSHConnect}>
          {/* <Iconfont code="&#xe605;" /> */}
          <div onClick={testSSH} className={styles.testSSHConnectText}>
            测试ssh连接
          </div>
        </div>
      </div>
      <div className={classnames(styles.extendInfoBox, { [styles.showFormBox]: currentTab.key === 'extendInfo' })}>
        <RenderExtendTable backfillData={backfillData} dataSourceType={dataSourceType}></RenderExtendTable>
      </div>
      <div className={styles.formFooter}>
        <div className={styles.test}>
          {
            <Button
              loading={loadings.testButton}
              onClick={saveConnection.bind(null, submitType.TEST)}
              className={styles.test}>
              测试连接
            </Button>
          }
        </div>
        <div className={styles.rightButton}>
          <Button onClick={onCancel} className={styles.cancel}>
            取消
          </Button>
          <Button
            className={styles.save}
            theme="primary"
            loading={loadings.confirmButton}
            onClick={saveConnection.bind(null, dataSourceId ? submitType.UPDATE : submitType.SAVE)}
          >
            {
              dataSourceId ? '修改' : '连接'
            }
          </Button>
        </div>
      </div>
    </Modal>
  </div >
}

interface IRenderFormProps {
  dataSourceId: number | undefined,
  dataSourceType: string,
  tab: ITabsType;
  form: any;
  backfillData: any;
}

function RenderForm(props: IRenderFormProps) {
  const { dataSourceId, dataSourceType, tab, form, backfillData } = props;

  let aliasChanged = false;

  const dataSourceFormConfigMemo = useMemo<IDataSourceForm>(() => {
    return deepClone(dataSourceFormConfigs).find((t: IDataSourceForm) => {
      return t.type === dataSourceType
    })
  }, [])

  const [dataSourceFormConfig, setDataSourceFormConfig] = useState<IDataSourceForm>(dataSourceFormConfigMemo);

  const initialValuesMemo = useMemo(() => {
    return initialFormData(dataSourceFormConfigMemo[tab].items)
  }, [])

  const [initialValues] = useState(initialValuesMemo);

  useUpdateEffect(() => {
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
    form.setFieldsValue({
      ...dataObj,
      ...newData,
    });
  }

  function renderFormItem(t: IFormItem): React.ReactNode {
    const FormItemTypes: { [key in InputType]: () => React.ReactNode } = {
      [InputType.INPUT]: () => <Form.Item
        label={t.labelNameCN}
        name={t.name}
      >
        <Input />
      </Form.Item>,

      [InputType.SELECT]: () => <Form.Item
        label={t.labelNameCN}
        name={t.name}
      >
        <Select value={t.defaultValue} onChange={(e) => { selectChange({ name: t.name, value: e }) }}>
          {t.selects?.map((t: ISelect) => <Option key={t.value} value={t.value}>{t.label}</Option>)}
        </Select>
      </Form.Item>,

      [InputType.PASSWORD]: () => <Form.Item
        label={t.labelNameCN}
        name={t.name}
      >
        <Input.Password />
      </Form.Item>
    }

    return <Fragment key={t.name}>
      <div key={t.name} className={classnames({ [styles.labelTextAlign]: t.labelTextAlign })} style={{ width: `${t.width}%` }}>
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
    name={tab}
    form={form}
    initialValues={initialValues}
    className={styles.form}
    autoComplete='off'
    onFieldsChange={onFieldsChange}
  >
    {dataSourceFormConfig[tab]!.items.map((t => renderFormItem(t)))}
  </Form>
}
interface IRenderExtendTableProps {
  dataSourceType: string;
  backfillData: any;
}

let extendTableData: any = []

function RenderExtendTable(props: IRenderExtendTableProps) {
  const { dataSourceType, backfillData } = props;

  const dataSourceFormConfigMemo = useMemo<IDataSourceForm>(() => {
    return deepClone(dataSourceFormConfigs).find((t: IDataSourceForm) => {
      return t.type === dataSourceType
    })
  }, [])

  const extendInfo = dataSourceFormConfigMemo.extendInfo?.map(t => {
    return {
      label: t.key,
      value: t.value
    }
  }) || []

  const [data, setData] = useState([...extendInfo, { label: '', value: '' }])

  useEffect(() => {
    const list = Object.keys(backfillData.extendInfo || {})
    if (list.length) {
      const backfillDataExtendInfo = list.map(t => {
        return {
          label: backfillData.extendInfo?.[t]?.key,
          value: backfillData.extendInfo?.[t]?.value
        }
      })
      setData([...backfillDataExtendInfo, { label: '', value: '' }])
    }
  }, [backfillData])

  useEffect(() => {
    extendTableData = data
  }, [data])

  const columns: any = [
    {
      title: '名称',
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
              label: row.label,
              value: ''
            }
          }
          setData([...newData, { label: '', value: '' }])
        }

        if (index === data.length - 1 || isCustomLabel) {
          return <Input onBlur={blur} placeholder={index === data.length - 1 ? '自定义' : ''} onChange={change} value={value}></Input>
        } else {
          return <span>{value}</span>
        }
      }
    },
    {
      title: '值',
      dataIndex: 'value',
      width: '40%',
      render: (value: any, row: any, index: number) => {
        function change(e: any) {
          const newData = [...data]
          newData[index] = {
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

export default function CreateConnection() {
  const { model } = useContext(DatabaseContext);
  const editDataSourceData = model.editDataSourceData
  return editDataSourceData ? <VisiblyCreateConnection /> : <></>
}


