import {
  getCommonDataSourceList,
  getCommonTeamList,
  getCommonUserAndTeamList,
  getCommonUserList,
} from '@/service/team';
import { IDataSourceVO, ITeamAndUserVO, ITeamVO, IUserVO, ManagementType, SearchType } from '@/typings/team';
import { Modal, Select, Spin } from 'antd';
import debounce from 'lodash/debounce';
import React, { useEffect, useMemo, useState } from 'react';
import styles from './index.less';
import i18n from '@/i18n';

interface IProps {
  open: boolean;
  type?: SearchType;
  onConfirm: (values: Object) => void;
  onClose: () => void;
}

interface ValueType {
  id?: number;
  key: number;
  label: React.ReactNode;
  value: number;
}

const addAuthMap = {
  [SearchType['USER/TEAM']]: {
    title: i18n('team.action.addUserAndTeam'),
    loadRequest: getCommonUserAndTeamList,
    searchLabel: (data: ITeamAndUserVO) => data.name,
    searchValue: (data: ITeamAndUserVO) => JSON.stringify({ id: data.id, type: data.type }),
    searchListKey: 'accessObjectList',
    placeholder: i18n('team.action.addUserAndTeam.placeholder'),
  },
  [SearchType.TEAM]: {
    title: i18n('team.action.addTeam'),
    loadRequest: getCommonTeamList,
    searchLabel: (data: ITeamVO) => data.name,
    searchValue: (data: ITeamVO) => data.id,
    searchListKey: 'teamIdList',
    placeholder: i18n('team.action.addTeam.placeholder'),
  },
  [SearchType.USER]: {
    title: i18n('team.action.addUser'),
    loadRequest: getCommonUserList,
    searchLabel: (data: IUserVO) => data.userName,
    searchValue: (data: IUserVO) => data.id,
    searchListKey: 'userIdList',
    placeholder: i18n('team.action.addUser.placeholder'),
  },
  [SearchType.DATASOURCE]: {
    title: i18n('team.action.addDatasource'),
    loadRequest: getCommonDataSourceList,
    searchLabel: (data: IDataSourceVO) => data.alias,
    searchValue: (data: IDataSourceVO) => data.id,
    searchListKey: 'dataSourceIdList',
    placeholder: i18n('team.action.addDatasource.placeholder'),
  },
};

function UniversalAddModal(props: IProps) {
  const { open, type } = props;

  const [fetching, setFetching] = useState(false);
  const [options, setOptions] = useState<ValueType[]>([]);
  const [selectedValues, setSelectedValues] = useState([]);

  const authData = useMemo(() => {
    if (type) {
      return addAuthMap[type];
    }
  }, [type]);

  useEffect(() => {
    loadOptions('');
  }, []);

  const loadOptions = (value: string) => {
    setOptions([]);
    setFetching(true);

    authData?.loadRequest({ searchKey: value }).then((res) => {
      const newOptions = (res || []).map((i) => ({
        ...i,
        label: authData.searchLabel(i),
        value: authData.searchValue(i),
        key: i.id,
      }));

      setOptions(newOptions);

      setFetching(false);
    });
  };

  const handleOk = () => {
    if (!props.onConfirm || !authData) {
      return;
    }

    const realValue = {
      [authData.searchListKey]:
        type !== SearchType['USER/TEAM'] ? selectedValues : selectedValues.map((i) => JSON.parse(i)),
    };

    props.onConfirm(realValue);
    props.onClose && props.onClose();
    setSelectedValues([]);
    setOptions([]);
  };

  return (
    <Modal
      open={open}
      onOk={handleOk}
      onCancel={() => {
        props.onClose && props.onClose();
      }}
      title={authData?.title}
    >
      <Select
        size="large"
        mode="multiple"
        style={{ width: '100%' }}
        onSearch={debounce(loadOptions, 300)}
        placeholder={authData?.placeholder}
        filterOption={false}
        notFoundContent={fetching ? <Spin style={{ margin: '16px 0' }} size="small" /> : null}
        options={options}
        value={selectedValues}
        onChange={(values) => {
          setSelectedValues(values);
        }}
      />
    </Modal>
  );
}

export default UniversalAddModal;
