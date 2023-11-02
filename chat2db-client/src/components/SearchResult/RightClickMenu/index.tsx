import React, { memo } from 'react';
import { Dropdown } from 'antd';
import i18n from '@/i18n';
import MenuLabel from '@/components/MenuLabel';

interface IProps {
  className?: string;
  children?: React.ReactNode;
}

export default memo<IProps>((props) => {
  const { children } = props;
  const items = [
    {
      label: <MenuLabel icon="&#xec7a;" label="拷贝" />,
      key: '0',
    },
    {
      label: <MenuLabel icon="&#xec7a;" label="拷贝行" />,
      key: '1',
      children: [
        {
          label: 'Insert 语句',
          key: '1-1',
          onClick: () => {},
        },
        {
          label: 'Update 语句',
          key: '1-2',
          onClick: () => {},
        },
        {
          label: '制表符分隔值(数据)',
          key: '1-3',
          onClick: () => {},
        },
        {
          label: '制表符分隔值(字段名)',
          key: '1-4',
          onClick: () => {},
        },
        {
          label: '制表符分隔值(字段名和数据)',
          key: '1-5',
          onClick: () => {},
        },
      ]
    },
    {
      label: <MenuLabel icon="&#xec7a;" label="克隆行" />,
      key: '2',
      onClick: () => {},
    },
    {
      label: <MenuLabel icon="&#xe6a7;" label="删除行" />,
      key: '3',
      onClick: () => {},
    },
  ]

  return <Dropdown menu={{ items }} trigger={["contextMenu"]} >
    {children}
  </Dropdown>;
});
