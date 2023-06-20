import React, { memo, useEffect, useState, useRef, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import { Dropdown } from 'antd';
import i18n from '@/i18n';
import Iconfont from '@/components/Iconfont';
import Tree from '@/components/Tree';
import DraggableContainer from '@/components/DraggableContainer';
import OperationTableModal, {
  IOperationData,
} from '@/components/OperationTableModal';
import GlobalAddMenu from '@/components/GlobalAddMenu';
import ConsoleList from '@/components/ConsoleList';
import SearchInput from '@/components/SearchInput';
import { IConnectionBase, ITreeNode, IWindowTab, IDB, IConsole } from '@/types';
const monaco = require('monaco-editor/esm/vs/editor/editor.api');
import { language } from 'monaco-editor/esm/vs/basic-languages/sql/sql';
const { keywords } = language;
import DatabaseContextProvider, { DatabaseContext } from '@/context/database';
import CreateConnection from '@/components/CreateConnection';


interface IProps {
  className?: any;
}
type ITabType = 'sql' | 'editTable';

interface IEditTableConsole {
  label: string;
  key: string;
  tabType: ITabType;
  id: number;
  operationData: any;
}

let monacoEditorExternalList: any = {};


function DatabasePage({ className }: IProps) {
  const { model, setShowSearchResult } = useContext(DatabaseContext);
  const { showSearchResult } = model;
  const [activeKey, setActiveKey] = useState<string>();
  const [openDropdown, setOpenDropdown] = useState(false);
  const [isUnfold, setIsUnfold] = useState(true);
  const [addTreeNode, setAddTreeNode] = useState<ITreeNode[]>();
  const treeRef = useRef<any>();
  const volatileRef = useRef<any>();
  const [windowList, setWindowList] = useState<IConsole[]>([]);

  const closeDropdownFn = () => {
    setOpenDropdown(false);
  };

  useEffect(() => {
    if (openDropdown) {
      document.documentElement.addEventListener('click', closeDropdownFn);
    }
    return () => {
      document.documentElement.removeEventListener('click', closeDropdownFn);
    };
  }, [openDropdown]);

  const moveLeftAside = () => {
    if (volatileRef.current) {
      if (volatileRef.current.offsetWidth === 0) {
        volatileRef.current.style.width = '250px';
        setIsUnfold(true);
      } else {
        volatileRef.current.style.width = '0px';
        setIsUnfold(false);
      }
    }
  };

  const callback = () => {
    monacoEditorExternalList[activeKey!] && monacoEditorExternalList[activeKey!].layout();
  };

  const searchTable = (value: string) => {
    treeRef.current?.filtrationDataTree(value);
  };

  function refresh() {
    treeRef.current?.getDataSource();
  }

  function getAddTreeNode(data: ITreeNode) {
    setAddTreeNode([data]);
  }

  function windowListChange(value: IConsole[]) {
    setWindowList(value)
  }

  return <>
    <DraggableContainer className={classnames(className, styles.box)} callback={callback} volatileDom={{ volatileRef, volatileIndex: 0 }} >
      <div ref={volatileRef} className={styles.asideBox}>
        <div className={styles.aside}>
          <div className={styles.header}>
            <div className={styles.searchBox}>
              <SearchInput onChange={searchTable} placeholder="搜索数据源" />
              <div
                className={classnames(styles.refresh, styles.button)}
                onClick={refresh}
              >
                <Iconfont code="&#xec08;" />
              </div>
              <Dropdown
                overlay={<GlobalAddMenu getAddTreeNode={getAddTreeNode} />}
                trigger={['click']}
              >
                <div
                  onClick={() => setOpenDropdown(true)}
                  className={classnames(styles.create, styles.button)}
                >
                  <Iconfont code="&#xe631;" />
                </div>
              </Dropdown>
            </div>
          </div>
          <div className={styles.overview}>
            <Iconfont code="&#xe63d;" />
            <span>{i18n('connection.button.overview')}</span>
          </div>
          <Tree
            cRef={treeRef}
            className={styles.tree}
            addTreeData={addTreeNode}
          />
        </div>
      </div>
      <div className={styles.main}>
        <ConsoleList windowListChange={windowListChange} />
        <div className={styles.footer}>
          <div className={classnames({ [styles.reversalIconBox]: !isUnfold }, styles.iconBox)} onClick={moveLeftAside}>
            <Iconfont code='&#xeb93;' />
          </div>
          {
            !!windowList.length &&
            <div onClick={() => { setShowSearchResult(!showSearchResult) }} className={classnames(styles.commandSearchResult, { [styles.unfoldSearchResult]: showSearchResult })}>
              查询结果
              <Iconfont code='&#xeb93;' />
            </div>
          }
        </div>
      </div>
    </DraggableContainer>
    <OperationTableModal />
    // 创建修改连接
    <CreateConnection />
  </>
};

export default function () {
  return <DatabaseContextProvider>
    <DatabasePage />
  </DatabaseContextProvider>
} 
