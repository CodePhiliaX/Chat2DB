import { DivProps } from '@chat2db/ui/es/types';
import { memo } from 'react';
import { useStyle } from './style';
import { Dropdown } from 'antd';

export interface DataItemProps {
  id?: string;
  name: string;
  icon: string;
}

export interface ConnectionListProps extends DivProps {
  dataList: DataItemProps[];
  handleMenuItemSingleClick: (t: DataItemProps) => void;
}

const ConnectionList = memo<ConnectionListProps>(({ className, dataList, ...rest }) => {
  const { styles, cx } = useStyle();
  return (
    <div className={cx(styles.connectionListWrapper, className)} {...rest}>
      {(dataList||[]).map(menuItem=>{
          return <Dropdown
            key={menuItem.id} 
            trigger={['contextMenu']}
            menu={{
              items: 
            }}
          >
            <
          </Dropdown>
        })}
    </div>
  );
});

export default ConnectionList;
