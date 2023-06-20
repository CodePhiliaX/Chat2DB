import { InputType, AuthenticationType, SSHAuthenticationType } from './enum';
import { DatabaseTypeCode } from '@/utils/constants';
import { OperationColumn } from '@/components/Tree/treeConfig';

export type ISelect = {
  value?: AuthenticationType | SSHAuthenticationType | string;
  label?: string;
  items?: IFormItem[];
};

export interface IFormItem {
  defaultValue: any;
  inputType: InputType;
  labelNameCN: string;
  labelNameEN: string;
  name: string;
  required: boolean;
  width: number;
  selected?: any;
  selects?: ISelect[];
  labelTextAlign?: 'right';
}

// 配置链接数据源表单 Json
export type IDataSourceForm = {
  type: DatabaseTypeCode;
  baseInfo: {
    items: IFormItem[];
    pattern: RegExp;
    template: string;
    excludes?: OperationColumn[];
  },
  ssh: {
    items: IFormItem[];
  },
  extendInfo?: {
    key: string;
    value: any;
  }[]
};