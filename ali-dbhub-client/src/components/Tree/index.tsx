import React, { memo, useEffect, useRef, useContext, useState, forwardRef, useImperativeHandle } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '../Iconfont';
import { Dropdown, Modal, Tooltip } from 'antd';
import { ITreeNode } from '@/types';
import { callVar, approximateTreeNode } from '@/utils';
import { TreeNodeType } from '@/utils/constants';
import StateIndicator from '@/components/StateIndicator';
import LoadingContent from '../Loading/LoadingContent';
import { useCanDoubleClick, useUpdateEffect } from '@/utils/hooks';
import connectionService from '@/service/connection';
import TreeNodeRightClick from './TreeNodeRightClick';
import { treeConfig, switchIcon, ITreeConfigItem } from './treeConfig'
import { databaseType } from '@/utils/constants'
import { DatabaseContext } from '@/context/database';

interface IProps {
  className?: string;
  cRef: any;
  addTreeData?: ITreeNode[];
}

interface TreeNodeIProps {
  data: ITreeNode;
  level: number;
  show: boolean;
  setTreeData: Function;
  showAllChildrenPenetrate?: boolean;
}

function Tree(props: IProps, ref: any) {
  const { className, cRef, addTreeData } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);
  const [searchedTreeData, setSearchedTreeData] = useState<ITreeNode[] | null>(null);
  const { model } = useContext(DatabaseContext);
  const { refreshTreeNum } = model;


  useUpdateEffect(() => {
    setTreeData([...(treeData || []), ...(addTreeData || [])]);
  }, [addTreeData])

  function filtrationDataTree(keywords: string) {
    if (!keywords) {
      setSearchedTreeData(null)
    } else if (treeData?.length && keywords) {
      setSearchedTreeData(approximateTreeNode(treeData, keywords));
    }
  }

  function getDataSource() {
    setTreeData(null);

    let p = {
      pageNo: 1,
      pageSize: 999
    }

    connectionService.getList(p).then(res => {
      const treeData = res.data.map(t => {
        return {
          name: t.alias,
          key: t.id!.toString(),
          nodeType: TreeNodeType.DATASOURCE,
          dataSourceId: t.id,
          dataSourceName: t.alias,
          dataType: t.type,
        }
      })
      setTimeout(() => {
        setTreeData(treeData);
      }, 200);
    })
  }

  useImperativeHandle(cRef, () => ({
    getDataSource,
    filtrationDataTree
  }))

  useEffect(() => {
    getDataSource();
  }, [refreshTreeNum])

  return <div className={classnames(className, styles.box)}>
    <LoadingContent data={treeData}>
      {
        (searchedTreeData || treeData)?.map((item, index) => {
          return <TreeNode
            setTreeData={setTreeData}
            key={item.name + index}
            show={true}
            level={0}
            data={item}
          />
        })
      }
    </LoadingContent>
  </div>
};

function TreeNode(props: TreeNodeIProps) {
  const { setTreeData, data, level, show = false, showAllChildrenPenetrate = false } = props;
  console.log(data)
  const [showChildren, setShowChildren] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const indentArr = new Array(level).fill('indent');
  const { model, setNeedRefreshNodeTree, setDblclickNodeData } = useContext(DatabaseContext);
  const { needRefreshNodeTree } = model;

  const treeNodeClick = useCanDoubleClick();

  function loadData(data: ITreeNode) {
    const treeNodeConfig: ITreeConfigItem = treeConfig[data.pretendNodeType || data.nodeType];
    treeNodeConfig.getChildren?.(data).then(res => {
      if (res.length) {
        console.log(res)
        setTimeout(() => {
          data.children = res;
          setShowChildren(true);
          setIsLoading(false);
        }, 200);
      } else {
        if (treeNodeConfig.next) {
          data.pretendNodeType = treeNodeConfig.next;
          loadData(data);
        } else {
          data.children = [];
          setShowChildren(true);
          setIsLoading(false);
        }
      }
    }).catch(error => {
      setIsLoading(false);
    });
  }

  useEffect(() => {
    if (data?.dataSourceId === needRefreshNodeTree?.dataSourceId &&
      data?.databaseName === needRefreshNodeTree?.databaseName &&
      data.nodeType === needRefreshNodeTree.nodeType) {
      setIsLoading(true);
      setNeedRefreshNodeTree(false);
      data.children = [];
      loadData(data)
    }
  }, [needRefreshNodeTree])

  useEffect(() => {
    setShowChildren(showAllChildrenPenetrate);
  }, [showAllChildrenPenetrate])

  //展开-收起
  const handleClick = (data: ITreeNode) => {
    if (!showChildren && !data.children) {
      setIsLoading(true);
    }

    if (treeConfig[data.nodeType] && !data.children) {
      loadData(data)
    } else {
      setShowChildren(!showChildren);
    }
  };

  const renderMenu = () => {
    return <TreeNodeRightClick
      data={data}
      setTreeData={setTreeData}
      setIsLoading={setIsLoading}
    />
  }

  const recognizeIcon = (nodeType: TreeNodeType) => {
    if (nodeType === TreeNodeType.DATASOURCE) {
      return databaseType[data.dataType!]?.icon
    } else {
      return switchIcon[nodeType]?.icon || '\ue62c'
    }
  }

  function renderTitle(data: ITreeNode) {
    return <>
      <span>{data.name}</span>
      {
        data.columnType && data.nodeType === TreeNodeType.COLUMN &&
        <span style={{ color: callVar('--custom-primary-color') }}>（{data.columnType}）</span>
      }
    </>
  }

  function nodeDoubleClick() {
    if (data.nodeType === TreeNodeType.TABLE || data.nodeType === TreeNodeType.COLUMN) {
      setDblclickNodeData(data);
    } else {
      handleClick(data);
    }
  }

  return show ? <>
    <Dropdown overlay={renderMenu()} trigger={['contextMenu']}>
      <Tooltip placement="right" title={renderTitle(data)}>
        <div
          className={classnames(styles.treeNode, { [styles.hiddenTreeNode]: !show })} >
          <div className={styles.left}>
            {
              indentArr.map((item, i) => {
                return <div key={i} className={styles.indent}></div>
              })
            }
          </div>
          <div className={styles.right}>
            {
              !data.isLeaf &&
              <div onClick={handleClick.bind(null, data)} className={styles.arrows}>
                {
                  isLoading
                    ?
                    <div className={styles.loadingIcon}>
                      <Iconfont code='&#xe6cd;' />
                    </div>
                    :
                    <Iconfont className={classnames(styles.arrowsIcon, { [styles.rotateArrowsIcon]: showChildren })} code='&#xe608;' />
                }
                {/* <Iconfont className={classnames(styles.arrowsIcon, { [styles.rotateArrowsIcon]: showChildren })} code='&#xe608;' /> */}
              </div>
            }
            <div className={styles.dblclickArea} onDoubleClick={nodeDoubleClick}>
              <div className={styles.typeIcon}>
                <Iconfont code={recognizeIcon(data.nodeType)!}></Iconfont>
              </div>
              <div className={styles.contentText} >
                <div className={styles.name} dangerouslySetInnerHTML={{ __html: data.name }}></div>
                {data.nodeType === TreeNodeType.COLUMN && <div className={styles.type}>{data.columnType}</div>}
              </div>
            </div>
          </div>
        </div>
      </Tooltip>
    </Dropdown>
    {
      data.children?.map((item: any, i: number) => {
        return <TreeNode
          key={i}
          data={item}
          level={level + 1}
          setTreeData={setTreeData}
          showAllChildrenPenetrate={showAllChildrenPenetrate}
          show={(showChildren && show)}
        />
      })
    }
  </> : <></>
}

export default forwardRef(Tree);
