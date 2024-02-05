import React from 'react';
import { DraggablePanel, ListItem } from '@chat2db/ui';
import { useStyle } from './style';
import PageTitle from '@/components/PageTitle';
import i18n from '@/i18n';
import { mockList } from './mock';
import { LayoutDashboard } from 'lucide-react';

const Dashboard = () => {
  const { styles, cx } = useStyle();

  const renderDashboardMenuList = () => {
    return (
      <div className={styles.listWrapper}>
        {(mockList || []).map((item, index) => {
          return (
            <ListItem
              key={index}
              size="small"
              icon={LayoutDashboard}
              label={item.label}
              dropdownProps={{
                trigger:['contextMenu'],
                menu: {
                  items: [
                    {
                      key: 'Edit',
                      label: i18n('dashboard.edit'),
                    },
                    {
                      key: 'Delete',
                      label: i18n('dashboard.delete'),
                    },
                  ],
                },
              }}
              onClick={() => {
                console.log('click', item);
              }}
            />
          );
        })}
      </div>
    );
  };
  return (
    <DraggablePanel direction={'horizontal'} className={styles.container} defaultSize={[15]} minSize={[15, 70]}>
      <div className={styles.containerLeft}>
        <PageTitle title={i18n('dashboard.title')} />
        {renderDashboardMenuList()}
      </div>
      <div className={styles.containerRight}></div>
    </DraggablePanel>
  );
};

export default Dashboard;
