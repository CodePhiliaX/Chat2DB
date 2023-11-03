import React, { memo, useMemo } from 'react';
import { Dropdown } from 'antd';
import i18n from '@/i18n';
import MenuLabel from '@/components/MenuLabel';

interface IProps {
  className?: string;
  children?: React.ReactNode;
  menuList: IMenu[]
}

export interface IMenu {
  key: string;
  callback?: () => void;
  children?: {
    callback: () => void;
  }[]
}

export enum AllSupportedMenusType  {
  CopyCell = 'copy-cell',
  CopyRow = 'copy-row',
  CloneRow = 'clone-row',
  DeleteRow = 'delete-row',
}

export default memo<IProps>((props) => {
  const { children, menuList } = props;
  const allSupportedMenus = {
    [AllSupportedMenusType.CopyCell]: {
      label: <MenuLabel icon="&#xec7a;" label="拷贝" />,
      key: AllSupportedMenusType.CopyCell,
    },
    [AllSupportedMenusType.CopyRow]: {
      label: <MenuLabel icon="&#xec7a;" label="拷贝行" />,
      key: AllSupportedMenusType.CopyRow,
      children: [
        {
          label: 'Insert 语句',
          key: 'copy-row-1',
        },
        {
          label: 'Update 语句',
          key: 'copy-row-2',
        },
        {
          label: '制表符分隔值(数据)',
          key: 'copy-row-3',
        },
        {
          label: '制表符分隔值(字段名)',
          key: 'copy-row-4',
        },
        {
          label: '制表符分隔值(字段名和数据)',
          key: 'copy-row-5',
        },
      ]
    },
    [AllSupportedMenusType.CloneRow]: {
      label: <MenuLabel icon="&#xec7a;" label="克隆行" />,
      key: AllSupportedMenusType.CloneRow,
    },

    [AllSupportedMenusType.DeleteRow]: {
      label: <MenuLabel icon="&#xe6a7;" label="删除行" />,
      key: AllSupportedMenusType.DeleteRow,
    }
  }

  const items = useMemo(()=>{
    return menuList.map((menu) => {
      return {
        ...allSupportedMenus[menu.key],
        onClick: menu.callback,
        children: menu.children?.map((child,index) => {
          return {
            ...allSupportedMenus[menu.key]['children'][index],
            onClick: child.callback,
          }
        })
      }
    })
  }, [menuList])

  return <Dropdown menu={{ items }} trigger={["contextMenu"]} >
    {children}
  </Dropdown>;
});
