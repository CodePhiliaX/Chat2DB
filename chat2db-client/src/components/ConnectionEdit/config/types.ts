import { InputType, AuthenticationType, SSHAuthenticationType } from './enum';
import { DatabaseTypeCode, OperationColumn } from '@/constants';

export type ISelect = {
  value?: AuthenticationType | SSHAuthenticationType | string | boolean;
  label?: string;
  onChange?: (value: IConnectionConfig) => IConnectionConfig;
  rest?: {
    [key in string]: any
  }
  items?: IFormItem[];
};

export interface IFormItem {
  defaultValue: any;
  inputType: InputType;
  labelNameCN: string;
  labelNameEN: string;
  name: string;
  required: boolean;
  selected?: any;
  selects?: ISelect[];
  labelTextAlign?: 'right';
  placeholder?: string;
  placeholderEN?: string;
  styles?: {
    width?: string; // 表单占用的长度 推荐百分比 默认值为 100%
    labelWidthEN?: string; // 英文环境下表单label的长度 推荐px 默认值为 70px
    labelWidthCN?: string; // 中文环境下表单label的长度 推荐px 默认值为 100px
    labelAlign?: 'left' | 'right'; // label的对齐方式 默认值为左对齐
  },
}

// 配置链接数据源表单 Json
export type IConnectionConfig = {
  type: DatabaseTypeCode;
  baseInfo: {
    items: IFormItem[];
    pattern: RegExp;
    template: string;
    excludes?: OperationColumn[];

  },
  driver?: {
    items: IFormItem[];
  }
  ssh: {
    items: IFormItem[];
  },
  extendInfo?: {
    key: string;
    value: any;
  }[],
  // TODO: 先取form里的配置，在取form.item的配置, 最后取默认值，目前没有取全局的
  styles?: {
    width?: string; // 表单占用的长度 推荐百分比 默认值为 100%
    labelWidthEN?: string; // 英文环境下表单label的长度 推荐px 默认值为 70px
    labelWidthCN?: string; // 中文环境下表单label的长度 推荐px 默认值为 100px
    labelAlign?: 'left' | 'right'; // label的对齐方式 默认值为左对齐
  }
};
