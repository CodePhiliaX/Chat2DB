import React, { memo, useRef } from 'react';
import i18n from '@/i18n';
import { Button, Steps } from 'antd'
import { LoadingOutlined, SmileOutlined, SolutionOutlined, UserOutlined } from '@ant-design/icons';
import styles from './index.less';
import Setting from '@/blocks/Setting';
import Tabs from '@/components/Tabs';
import { DatabaseTypeCode } from '@/constants'
import CreateConnection from '@/blocks/CreateConnection'


export default function Demo() {

  // 编辑的话就传入全部数据
  const mockData: any = {
    "id": 1,
    "ssh": {
      "use": false,
      "hostName": "1",
      "port": "22",
      "userName": "1",
      "localPort": "",
      "authenticationType": "password",
      "password": ""
    },
    "driverConfig": {
      "jdbcDriverClass": "com.mysql.cj.jdbc.Driver",
      "jdbcDriver": "mysql-connector-java-8.0.30.jar"
    },
    "alias": "@localhost",
    "host": "localhost",
    "port": "3306",
    "authenticationType": "1",
    "user": "root",
    "password": "11",
    "database": "1111111",
    "url": "jdbc:mysql://localhost:3306/1111111",
    "extendInfo": [
      {
        "key": "zeroDateTimeBehavior",
        "value": "convertToNull"
      }
    ],
    "connectionEnvType": "DAILY",
    "type": "MYSQL"
  }

  // 新建置顶数据库类型的话只需要传入type
  const mockData2: any = {
    type: DatabaseTypeCode.MYSQL
  }

  // 如果你想要列表的话，传空对象或者不传都行
  const mockData3: any = {

  }
  return <div>
    <div>
      <CreateConnection
        onSubmit={(data) => {
          console.log(data)
        }}
        connectionDetail={mockData3}
      />
    </div>

  </div>
}