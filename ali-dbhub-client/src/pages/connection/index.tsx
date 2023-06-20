import React, { memo, useEffect, useLayoutEffect, useRef, useState } from 'react';
import classnames from 'classnames';
import { formatNaturalDate } from '@/utils/index';
import Iconfont from '@/components/Iconfont';
import ScrollLoading from '@/components/ScrollLoading';
import StateIndicator from '@/components/StateIndicator';
import LoadingContent from '@/components/Loading/LoadingContent';
import CreateConnection from '@/components/CreateConnection';
import i18n from '@/i18n';
import { history } from 'umi';
import connectionServer from '@/service/connection'
import { IConnectionBase } from '@/types'
import { databaseTypeList, DatabaseTypeCode, databaseType, EnvType } from '@/utils/constants'
import moreDBLogo from '@/assets/moreDB-logo.png';
import {
  Dropdown,
  Space,
  Select,
  Button,
  Modal,
  Form,
  Input,
  Checkbox,
  message,
  // Menu,
  Pagination
} from 'antd';
import { IDatabase, ITreeNode } from '@/types'

import styles from './index.less';
import globalStyle from '@/global.less';
import Menu, { MenuItem } from '@/components/Menu';

const { Option } = Select;

interface IProps {
  className?: any;
  onlyList?: boolean;
}

enum handleType {
  EDIT = 'edit',
  DELETE = 'delete',
  CLONE = 'clone'
}

const menuList = [
  {
    key: handleType.EDIT,
    icon: '\ue60f',
    title: i18n('connection.button.edit'),
  },
  {
    key: handleType.CLONE,
    icon: '\ue6ca',
    title: i18n('connection.button.clone'),
  },
  {
    key: handleType.DELETE,
    icon: '\ue604',
    title: i18n('connection.button.delete'),
  }
];

export default memo<IProps>(function ConnectionPage(props) {

  const { className, onlyList } = props;
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [connectionList, setConnectionList] = useState<IConnectionBase[] | null>(null);
  const [finished, setFinished] = useState(false);
  const [rowData, setRowData] = useState<IConnectionBase | null>();
  const [form] = Form.useForm();
  const scrollerRef = useRef(null);
  const [pageNo, setPageNo] = useState(0);

  useEffect(() => {
  }, [])

  type IParams = {
    superposition: boolean
  }

  const resetGetList = () => {
    setPageNo(0);
  }

  const getConnectionList = (params?: IParams) => {
    const { superposition } = params || {};
    let p = {
      pageNo: pageNo + 1,
      pageSize: 10
    }
    return connectionServer.getList(p).then(res => {
      if (connectionList?.length && superposition) {
        setConnectionList([...connectionList, ...res.data])
      } else {
        setConnectionList(res.data)
      }

      if (!res.hasNextPage) {
        setFinished(true)
      }
    })
  }

  const jumpPage = (item: IConnectionBase) => {
    history.push({
      pathname: `/database`,
    });
  };

  const showLinkModal = () => {
    setIsModalVisible(true);
    // setRowData(null);
    // form.resetFields();
  };

  const closeModal = () => {
    setRowData(null);
    form.resetFields();
    setIsModalVisible(false);
  }

  const RenderCard = ({ item }: { item: IConnectionBase }) => {
    const [openDropdown, setOpenDropdown] = useState(false);
    const closeDropdownFn = () => {
      setOpenDropdown(false);
    }
    useEffect(() => {
      if (openDropdown) {
        document.documentElement.addEventListener('click', closeDropdownFn);
      }
      return () => {
        document.documentElement.removeEventListener('click', closeDropdownFn);
      }
    }, [openDropdown])

    const renderMenu = (rowData: IConnectionBase) => {
      const editConnection = () => {
        setRowData(rowData);
        setIsModalVisible(true);
        form.setFieldsValue(rowData);
      }

      const deleteConnection = () => {
        resetGetList()
        connectionServer.remove({ id: rowData.id! }).then(res => {
          message.success('删除成功');
          getConnectionList();
        })
      }

      const cloneConnection = () => {
        resetGetList()
        connectionServer.clone({ id: rowData.id! }).then(res => {
          message.success('克隆成功');
          getConnectionList();
        })
      }

      const clickMenuList = (item: any) => {
        switch (item.key) {
          case handleType.EDIT:
            return editConnection();
          case handleType.DELETE:
            return deleteConnection();
          case handleType.CLONE:
            return cloneConnection();
        }
      }

      return <Menu>
        {
          menuList.map((item, index) => {
            return <MenuItem key={index} onClick={clickMenuList.bind(null, item)}>
              <span>
                <Iconfont code={item.icon!}></Iconfont>
                {item.title}
              </span>
            </MenuItem>
          })
        }
      </Menu>
    };

    return <div key={item.id} className={styles.connectionItem}>
      <div className={styles.left} onClick={jumpPage.bind(null, item)}>
        <div
          className={styles.logo}
          style={{
            backgroundImage: `url(${databaseType[item.type]?.img || moreDBLogo})`,
          }}
        ></div>
        <div className={styles.name}>{item.alias}</div>
        <div className={styles.EnvType}>
          {item.EnvType === EnvType.DAILY ? "日常" : "线上"}
        </div>
        <div className={styles.user}>{item.user}</div>
        <div className={styles.url}>{item.url}</div>
      </div>
      {
        !onlyList &&
        <div className={styles.right}>
          <Dropdown overlay={renderMenu(item)} trigger={['click']}>
            <a onClick={(event) => { event.stopPropagation(); setOpenDropdown(true) }}>
              <div className={styles.moreActions}>
                <Iconfont code="&#xe601;" />
              </div>
            </a>
          </Dropdown>
        </div>
      }
    </div>
  }

  function submitCallback(data: ITreeNode) {
    getConnectionList();
    setIsModalVisible(false);
  }

  return (
    <div className={classnames(className, styles.box)}>
      {
        !onlyList &&
        <div className={styles.header}>
          <div className={styles.title}>{i18n('home.nav.database')}</div>
          <Button
            size='small'
            className={classnames(styles.linkButton)}
            type="primary"
            onClick={showLinkModal}
          >
            <Iconfont code="&#xe631;"></Iconfont>
            {i18n('connection.input.newLink')}
          </Button>
        </div>
      }
      <div className={styles.scrollBox} ref={scrollerRef}>
        <ScrollLoading
          finished={finished}
          scrollerElement={scrollerRef}
          onReachBottom={getConnectionList.bind(null, { superposition: true })}
          threshold={200}
        >
          {
            !!connectionList?.length &&
            <div className={styles.connectionList}>
              {connectionList?.map(item => <RenderCard key={item.id} item={item}></RenderCard>)}
            </div>
          }
        </ScrollLoading>
        {!connectionList?.length && connectionList !== null && <StateIndicator state='empty'></StateIndicator>}
      </div>

      {/* <CreateConnection
        submitCallback={submitCallback}
        onCancel={() => { setIsModalVisible(false) }}
        openModal={isModalVisible}
      /> */}

    </div>
  );
});
