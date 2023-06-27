import React, { memo } from 'react';
import i18n from '@/i18n';
import { Button, Steps } from 'antd'
import { LoadingOutlined, SmileOutlined, SolutionOutlined, UserOutlined } from '@ant-design/icons';
import styles from './index.less';
import Setting from '@/blocks/Setting';
import Tab from '@/components/Tab';


export default function Demo() {
  const tabs = [
    {
      label: 'components/121212',
      value: 1
    },
    {
      label: 'components/121212components/121212components/121212',
      value: 2
    },
    {
      label: 'components/121212components/121212components/121212components/121212components/121212components/121212',
      value: 3
    },
    {
      label: 'components/121212components/121212components/121212components/121212',
      value: 4
    },
    {
      label: 'components/121212',
      value: 5
    },
  ]
  return <div className={styles.page}>
    <Tab
      tabs={tabs}
    />
  </div>
}