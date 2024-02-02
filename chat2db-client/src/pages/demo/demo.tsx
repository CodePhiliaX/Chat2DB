import { Copy } from 'lucide-react';
import { IconButton, staticMessage, staticModal, staticNotification } from '@chat2db/ui';
import {
  Alert,
  Button,
  Dropdown,
  Flex,
  Input,
  MenuProps,
  Modal,
  Pagination,
  Popconfirm,
  Radio,
  Segmented,
  Select,
  Skeleton,
  Tabs,
  TabsProps,
  Tooltip,
  Typography,
} from 'antd';
import { createStyles } from 'antd-style';

const menuItems: MenuProps['items'] = [
  {
    key: '1',
    label: (
      <a target="_blank" rel="noopener noreferrer" href="https://www.antgroup.com">
        1st menu item
      </a>
    ),
  },
  {
    key: '2',
    label: (
      <a target="_blank" rel="noopener noreferrer" href="https://www.aliyun.com">
        2nd menu item
      </a>
    ),
  },
  {
    key: '3',
    label: (
      <a target="_blank" rel="noopener noreferrer" href="https://www.luohanacademy.com">
        3rd menu item
      </a>
    ),
  },
];

const tabItems: TabsProps['items'] = [
  { key: '1', label: 'Tab 1' },
  { key: '2', label: 'Tab 2' },
  { key: '3', label: 'Tab 3' },
];

const plainOptions = ['Apple', 'Pear', 'Orange'];
const options = [
  { label: 'Apple', value: 'Apple' },
  { label: 'Pear', value: 'Pear' },
  { label: 'Orange', value: 'Orange', title: 'Orange' },
];
const optionsWithDisabled = [
  { label: 'Apple', value: 'Apple' },
  { label: 'Pear', value: 'Pear' },
  { label: 'Orange', value: 'Orange', disabled: true },
];

const useStyles = createStyles(({ css, token }) => {
  return {
    container: css`
      /* height: 200px; */
      padding: 20px;
      width: 100%;
      background-color: ${token.colorBgContainer};
      border: 1px solid ${token.colorBorder};
      border-radius: ${token.borderRadius}px;
    `,
  };
});

export default () => {
  const { styles } = useStyles();
  return (
    <Flex vertical align="center" justify="center" gap={20} className={styles.container}>
      <Modal>
        <p>Some contents...</p>
        <p>Some contents...</p>
        <p>Some contents...</p>
      </Modal>
      <Tooltip title="Copy">
        <Button>123123</Button>
      </Tooltip>
      <Flex justify="start" align="center" gap={10}>
        <IconButton icon={Copy} size="large" />
      </Flex>
      <Flex justify="start" align="center" gap={10}>
        Button:
        <Button type="primary">Chat2DB</Button>
      </Flex>
      <Flex justify="start" align="center" gap={10}>
        Select:
        <Select defaultValue="Chat2DB">
          <Select.Option value="Chat2DB">Chat2DB</Select.Option>
        </Select>
      </Flex>
      <Flex justify="start" align="center" gap={10}>
        Input:
        <Input defaultValue="Chat2DB" />
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Dropdown:
        <Dropdown menu={{ items: menuItems }} placement="bottomLeft">
          <Button>bottomLeft</Button>
        </Dropdown>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Pagination:
        <Pagination defaultCurrent={1} total={50} />
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Tabs:
        <Tabs defaultActiveKey="1" items={tabItems} />
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Segmented:
        <Segmented
          defaultValue="center"
          style={{ marginBottom: 8 }}
          // onChange={(value) => setAlignValue(value as Align)}
          options={['start', 'center', 'end']}
        />
      </Flex>

      <Flex justify="start" align="start" gap={10}>
        Radio:
        <Flex vertical gap={4}>
          <Radio.Group options={plainOptions} defaultValue={'apple'} />
          <Radio.Group options={optionsWithDisabled} defaultValue={'apple'} />
          <Radio.Group options={options} optionType="button" defaultValue={'apple'} />
          <Radio.Group options={optionsWithDisabled} optionType="button" buttonStyle="solid" defaultValue={'apple'} />
        </Flex>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Typography:
        <Typography.Text>Chat2DB</Typography.Text>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Static Message:
        <Button
          onClick={() => {
            staticMessage.success('This is a success message');
          }}
        >
          message
        </Button>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Static Modal:
        <Button
          onClick={() => {
            staticModal.confirm({
              title: 'Confirm',
              content: 'Bla bla ...',
              okText: '确认',
              cancelText: '取消',
            });
          }}
        >
          modal
        </Button>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Static Notification:
        <Button
          onClick={() => {
            staticNotification.success({
              message: 'Notification Title',
              description:
                'This is the content of the notification. This is the content of the notification. This is the content of the notification.',
            });
          }}
        >
          notification
        </Button>
      </Flex>

      <Flex justify="start" align="center" gap={10}>
        Popconfirm:
        <Popconfirm
          placement="topLeft"
          title={'text'}
          description={'descriptiondescriptiondescriptiondescription'}
          okText="Yes"
          cancelText="No"
        >
          <Button>TL</Button>
        </Popconfirm>
      </Flex>

      <Flex justify="start" align="center" gap={10} style={{ width: '100%' }}>
        Skeleton:
        <Skeleton active />
      </Flex>

      <Flex justify="start" align="start" gap={10} style={{ width: '100%' }}>
        Alert:
        <Flex vertical style={{ width: '100%' }} gap={4}>
          <Alert message="Success Text" type="success" />
          <Alert message="Info Text" type="info" />
          <Alert message="Warning Text" type="warning" />
          <Alert message="Error Text" type="error" />
          <Alert message="Success Tips" type="success" showIcon />
          <Alert message="Informational Notes" type="info" showIcon />
          <Alert message="Warning" type="warning" showIcon closable />
          <Alert message="Error" type="error" showIcon />
          <Alert
            message="Success Tips"
            description="Detailed description and advice about successful copywriting."
            type="success"
            showIcon
          />
          <Alert
            message="Informational Notes"
            description="Additional description and information about copywriting."
            type="info"
            showIcon
          />
          <Alert
            message="Warning"
            description="This is a warning notice about copywriting."
            type="warning"
            showIcon
            closable
          />
          <Alert message="Error" description="This is an error message about copywriting." type="error" showIcon />
        </Flex>
      </Flex>
    </Flex>
  );
};
