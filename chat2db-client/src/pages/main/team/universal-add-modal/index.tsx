import { getCommonUserList } from '@/service/team';
import { IUserPageQueryVO, ManagementType } from '@/typings/team';
import { Input, Modal, Select, Spin } from 'antd';
import debounce from 'lodash/debounce';
import React, { useMemo, useState } from 'react';
import styles from './index.less';

interface IProps {
  open: boolean;

  type: ManagementType;

  onClose: () => void;
}

interface ValueType {
  key: number;

  label: React.ReactNode;

  value: number;
}

function UniversalAddModal(props: IProps) {
  const { open } = props;

  const [fetching, setFetching] = useState(false);

  const [options, setOptions] = useState<ValueType[]>([]);

  const loadOptions = (value: string) => {
    setOptions([]);

    setFetching(true);

    getCommonUserList({ searchKey: value }).then((res) => {
      const newOptions = (res || []).map((i) => ({
        value: i.id,

        label: i.userName,

        key: i.id,
      }));

      console.log('newOptions', newOptions);

      setOptions(newOptions);

      setFetching(false);
    });
  };

  return (
    <Modal
      open={open}
      onCancel={() => {
        props.onClose && props.onClose();
      }}
      title="添加xx"
    >
      <Select
        size="large"
        mode="multiple"
        style={{ width: '100%' }}
        onSearch={debounce(loadOptions, 300)}
        placeholder="Select users"
        filterOption={false}
        notFoundContent={fetching ? <Spin style={{ margin: '16px 0' }} size="small" /> : null}
        options={options}
      />
    </Modal>
  );
}

export default UniversalAddModal;
