import React, { memo } from 'react';
import i18n from '@/i18n';
import { Button, Steps } from 'antd'
import { LoadingOutlined, SmileOutlined, SolutionOutlined, UserOutlined } from '@ant-design/icons';
import styles from './index.less';
import Setting from '@/blocks/Setting';
import LazyLoading from '@/components/Loading/LazyLoading';


export default function Demo() {
  return <div className={styles.page}>
    <Setting />
    <div>
      <Steps
        items={[
          {
            title: 'Login',
            status: 'finish',
            icon: <UserOutlined />,
          },
          {
            title: 'Verification',
            status: 'finish',
            icon: <SolutionOutlined />,
          },
          {
            title: 'Pay',
            status: 'process',
            icon: <LoadingOutlined />,
          },
          {
            title: 'Done',
            status: 'wait',
            icon: <SmileOutlined />,
          },
        ]}
      />
    </div>
    <div>
      <Button type="primary">我是button</Button>
    </div>
    <div>
      <Button type="primary">我是button</Button>
    </div>
    <div>
      <Button type="primary">我是button</Button>
    </div>
    <div>
      <Button type="primary">我是button</Button>
    </div>
  </div>
}