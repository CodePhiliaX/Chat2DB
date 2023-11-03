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
    hide?: boolean;
  }[]
}

export enum AllSupportedMenusType  {
  CopyCell = 'copy-cell',
  CopyRow = 'copy-row',
  CloneRow = 'clone-row',
  DeleteRow = 'delete-row',
  SetDefault = 'set-default',
  SetNull = 'set-null',
}

export default memo<IProps>((props) => {
  const { children, menuList } = props;
  const allSupportedMenus = {
    [AllSupportedMenusType.CopyCell]: {
      label: <MenuLabel icon="&#xec7a;" label={i18n('common.button.copy')} />,
      key: AllSupportedMenusType.CopyCell,
    },
    [AllSupportedMenusType.CopyRow]: {
      label: <MenuLabel icon="&#xec7a;" label={i18n('common.button.copyRowAs')} />,
      key: AllSupportedMenusType.CopyRow,
      children: [
        {
          label: i18n('common.button.insertSql'),
          key: 'copy-row-1',
        },
        {
          label: i18n('common.button.updateSql'),
          key: 'copy-row-2',
        },
        {
          label: i18n('common.button.tabularSeparatedValues'),
          key: 'copy-row-3',
        },
        {
          label: i18n('common.button.tabularSeparatedValuesFieldName'),
          key: 'copy-row-4',
        },
        {
          label: i18n('common.button.tabularSeparatedValuesFieldNameAndData'),
          key: 'copy-row-5',
        },
      ]
    },
    [AllSupportedMenusType.CloneRow]: {
      label: <MenuLabel icon="&#xe8db;" label={i18n('common.button.cloneRow')} />,
      key: AllSupportedMenusType.CloneRow,
    },
    [AllSupportedMenusType.DeleteRow]: {
      label: <MenuLabel icon="&#xe6a7;" label={i18n('common.button.deleteRow')} />,
      key: AllSupportedMenusType.DeleteRow,
    },
    [AllSupportedMenusType.SetDefault]: {
      label: <MenuLabel label={i18n('common.button.setDefault')} />,
      key: AllSupportedMenusType.SetDefault,
    },
    [AllSupportedMenusType.SetNull]: {
      label: <MenuLabel label={i18n('common.button.setNull')} />,
      key: AllSupportedMenusType.SetNull,
    },
  }

  const items = useMemo(()=>{
    return menuList.map((menu) => {
      return {
        ...allSupportedMenus[menu.key],
        onClick: menu.callback,
        children: menu.children?.map((child,index) => {
          if (child.hide) return null;
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
