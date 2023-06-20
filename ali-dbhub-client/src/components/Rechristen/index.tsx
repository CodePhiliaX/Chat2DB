import React, { memo, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Input, Modal } from 'antd';
import { ITreeNode } from '@/types';
import { Opens } from 'antd/lib/slider';

interface IProps {
  className?: string;
  operationOpen: [boolean, (value: boolean) => void];
  data: ITreeNode;
}

export default memo<IProps>(function Rechristen(props) {
  const { className, operationOpen, data } = props;
  return <Modal
    maskClosable={false}
    title={`将数据源${data.name} 重命名`}
    open={operationOpen[0]}
    onCancel={() => { operationOpen[1](false) }}
    footer={false}
  >
    <Input></Input>
  </Modal >
})
