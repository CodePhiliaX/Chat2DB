import { DatabaseTypeCode } from '@/constants';
import { IConnectionConfig } from './types';
import { InputType, AuthenticationType, SSHAuthenticationType, OperationColumn } from './enum';

export const dataSourceFormConfigs: IConnectionConfig[] = [
  // MYSQL
  {
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,
        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '3306',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,
                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,
                },
              ],
              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {
              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,
        },
        {
          defaultValue: 'jdbc:mysql://localhost:3306',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,
        },
        {
          defaultValue: '8.0',
          inputType: InputType.SELECT,
          labelNameCN: 'JDBC驱动',
          labelNameEN: 'JDBC Driver',
          name: 'jdbc',
          required: true,
          selects: [
            {
              value: '8.0',
            },
            {
              value: '5.0',
            },
          ],

        }
      ],
      pattern: /jdbc:mysql:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:mysql://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 主机',
          labelNameEN: 'SSH Hostname',
          name: 'hostName',
          required: false,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'SSH UserName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
    extendInfo: [
      {
        "key": "zeroDateTimeBehavior",
        "value": "convertToNull"
      }
    ],
    type: DatabaseTypeCode.MYSQL,
  },
  // POSTGRESQL
  {
    type: DatabaseTypeCode.POSTGRESQL,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '5432',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],
              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:postgresql://localhost:5432',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:postgresql:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:postgresql://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // ORACLE
  {
    type: DatabaseTypeCode.ORACLE,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '1521',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: 'XE',
          inputType: InputType.INPUT,
          labelNameCN: 'SID',
          labelNameEN: 'SID',
          name: 'sid',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: 'thin',
          inputType: InputType.SELECT,
          labelNameCN: '驱动',
          labelNameEN: 'Driver',
          name: 'driver',
          required: true,
          labelTextAlign: 'right',
          selects: [
            {
              value: 'thin',
            },
            {

              value: 'oci',
            },
            {

              value: 'oci8',
            },
          ],
          styles: {
            width: '30%',

          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],
              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: 'jdbc:oracle:thin:@localhost:1521:XE',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:oracle:(.*):@(.*):(\d+):(.*)/,
      template: 'jdbc:oracle:{driver}:@{host}:{port}:{sid}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // H2
  {
    type: DatabaseTypeCode.H2,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '9092',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],

              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:h2:tcp://localhost:9092',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:h2:tcp:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:h2:tcp://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // SQLSERVER encrypt=true;trustServerCertificate=true;integratedSecurity=false;Trusted_Connection=yes
  {
    type: DatabaseTypeCode.SQLSERVER,
    extendInfo: [
      {
        "key": "encrypt",
        "value": "true"
      },
      {
        "key": "trustServerCertificate",
        "value": "true"
      },
      {
        "key": "integratedSecurity",
        "value": "false"
      },
      {
        "key": "Trusted_Connection",
        "value": "yes"
      },
    ],
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '1433',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'Instance',
          labelNameEN: 'Instance',
          name: 'instance',
          required: false,

        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],

              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:sqlserver://localhost:1433',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:sqlserver:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:sqlserver://{host}:{port};',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // SQLITE
  {
    type: DatabaseTypeCode.SQLITE,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'identifier.sqlite',
          inputType: InputType.INPUT,
          labelNameCN: 'File',
          labelNameEN: 'File',
          name: 'file',
          required: true,

        },
        {
          defaultValue: 'jdbc:sqlite:identifier.sqlite',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:sqlite/,
      template: 'jdbc:sqlite://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // MARIADB
  {
    type: DatabaseTypeCode.MARIADB,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '3306',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],

              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:mariadb://localhost:3306',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:mariadb:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:mariadb://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'host',
          name: 'host',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'userName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
  },
  // CLICKHOUSE
  {
    type: DatabaseTypeCode.CLICKHOUSE,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '8123',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],

              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {

              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:clickhouse://localhost:8123',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:clickhouse:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:clickhouse://{host}:{port}/{database}',
      excludes: [OperationColumn.ExportDDL, OperationColumn.CreateTable] //排除掉导出ddl 和 创建表功能 支持的功能见 ./enum.ts => OperationColumn
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 主机',
          labelNameEN: 'SSH Hostname',
          name: 'hostName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'SSH UserName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    }
  },
  // DM
  {
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '5236',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],
              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {
              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:dm://localhost:5236',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:dm:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:dm://{host}:{port}/{database}',
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],

        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 主机',
          labelNameEN: 'SSH Hostname',
          name: 'hostName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'SSH UserName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,

        },
      ]
    },
    extendInfo: [
      {
        "key": "zeroDateTimeBehavior",
        "value": "convertToNull"
      },
    ],
    type: DatabaseTypeCode.DM
  },
  // HIVE
  {
    type: DatabaseTypeCode.HIVE,
    baseInfo: {
      items: [
        {
          defaultValue: '@localhost',
          inputType: InputType.INPUT,
          labelNameCN: '名称',
          labelNameEN: 'Name',
          name: 'alias',
          required: true,

        },
        {
          defaultValue: 'localhost',
          inputType: InputType.INPUT,
          labelNameCN: '主机',
          labelNameEN: 'Host',
          name: 'host',
          required: true,
          styles: {
            width: '70%',

          }
        },
        {
          defaultValue: '10000',
          inputType: InputType.INPUT,
          labelNameCN: '端口',
          labelNameEN: 'Port',
          name: 'port',
          labelTextAlign: 'right',
          required: true,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: AuthenticationType.USERANDPASSWORD,
          inputType: InputType.SELECT,
          labelNameCN: '身份验证',
          labelNameEN: 'Authentication',
          name: 'authentication',
          required: true,
          selects: [
            {
              items: [
                {
                  defaultValue: 'root',
                  inputType: InputType.INPUT,
                  labelNameCN: '用户名',
                  labelNameEN: 'User',
                  name: 'user',
                  required: true,

                },
                {
                  defaultValue: '',
                  inputType: InputType.PASSWORD,
                  labelNameCN: '密码',
                  labelNameEN: 'Password',
                  name: 'password',
                  required: true,

                },
              ],
              label: 'User&Password',
              value: AuthenticationType.USERANDPASSWORD,
            },
            {
              label: 'NONE',
              value: AuthenticationType.NONE,
            },
          ],
          styles: {
            width: '50%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '数据库',
          labelNameEN: 'Database',
          name: 'database',
          required: false,

        },
        {
          defaultValue: 'jdbc:hive2://localhost:10000',
          inputType: InputType.INPUT,
          labelNameCN: 'URL',
          labelNameEN: 'URL',
          name: 'url',
          required: true,

        },
      ],
      pattern: /jdbc:hive2:\/\/(.*):(\d+)(\/(\w+))?/,
      template: 'jdbc:hive2://{host}:{port}/{database}',
      excludes: [OperationColumn.CreateTable] //创建表功能 支持的功能见 ./enum.ts => OperationColumn
    },
    ssh: {
      items: [
        {
          defaultValue: 'false',
          inputType: InputType.SELECT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          selects: [
            {
              value: 'false',
            },
            {
              value: 'true',
            },
          ],
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '使用SSH',
          labelNameEN: 'USE SSH',
          name: 'use',
          required: false,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 主机',
          labelNameEN: 'SSH Hostname',
          name: 'hostName',
          required: false,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: 'SSH 端口',
          labelNameEN: 'Port',
          name: 'port',
          required: false,
          styles: {
            width: '30%',
            labelWidthEN: '40px',
            labelWidthCN: '40px',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '用户名',
          labelNameEN: 'SSH UserName',
          name: 'userName',
          required: false,
          styles: {
            width: '70%',
          }
        },
        {
          defaultValue: '',
          inputType: InputType.INPUT,
          labelNameCN: '本地端口',
          labelNameEN: 'LocalPort',
          name: 'localPort',
          required: false,
          styles: {
            width: '30%',
            labelAlign: 'right'
          }
        },
        {
          defaultValue: '',
          inputType: InputType.PASSWORD,
          labelNameCN: '密码',
          labelNameEN: 'Password',
          name: 'password',
          required: true,
        },
      ]
    }
  },
];
