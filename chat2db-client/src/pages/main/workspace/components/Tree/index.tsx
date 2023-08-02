import React, { memo, useEffect, useRef, useContext, useState, useImperativeHandle } from 'react';
import { connect } from 'umi';
import i18n from '@/i18n';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import { Dropdown, Modal, Tooltip } from 'antd';
import { ITreeNode } from '@/typings';
import { callVar, approximateTreeNode } from '@/utils';
import { TreeNodeType, databaseMap } from '@/constants';
import LoadingContent from '@/components/Loading/LoadingContent';
import TreeNodeRightClick from './TreeNodeRightClick';
import { treeConfig, switchIcon, ITreeConfigItem } from './treeConfig';
import { workspaceActionType } from '@/pages/main/workspace/context';
import { IWorkspaceModelType } from '@/models/workspace';

interface IProps {
  className?: string;
  initialData?: ITreeNode[];
}

interface TreeNodeIProps {
  data: ITreeNode;
  level: number;
  show: boolean;
  setTreeData: Function;
  showAllChildrenPenetrate?: boolean;
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace
}))

function Tree(props: IProps) {
  const { className, initialData } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | undefined>();
  const [searchedTreeData, setSearchedTreeData] = useState<ITreeNode[] | null>(null);


  useEffect(() => {
    setTreeData(initialData);
  }, [initialData])

  function filtrationDataTree(keywords: string) {
    if (!keywords) {
      setSearchedTreeData(null)
    } else if (treeData?.length && keywords) {
      setSearchedTreeData(approximateTreeNode(treeData, keywords));
    }
  }

  return <div className={classnames(className, styles.box)}>
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
  </div>
}

const TreeNode = dvaModel((props: TreeNodeIProps) => {
  const {
    setTreeData,
    data,
    level,
    show = false,
    showAllChildrenPenetrate = false,
    dispatch,
    workspaceModel
  } = props;
  const [showChildren, setShowChildren] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const indentArr = new Array(level).fill('indent');

  function loadData(data: ITreeNode) {
    const treeNodeConfig: ITreeConfigItem = treeConfig[data.treeNodeType];
    treeNodeConfig.getChildren?.({
      ...data,
      ...(data.extraParams || {}),
    }).then(res => {
      if (res.length) {
        setTimeout(() => {
          data.children = res;
          setShowChildren(true);
          setIsLoading(false);
        }, 200);
      }
      else {
        // 处理树可能出现不连续的情况 
        // if (treeNodeConfig.next) {
        //   data.pretendNodeType = treeNodeConfig.next;
        //   loadData(data);
        // } else {
        data.children = [];
        setShowChildren(true);
        setIsLoading(false);
        // }
      }
    }).catch(error => {
      setIsLoading(false);
    });
  }

  // useEffect(() => {
  //   if (data?.dataSourceId === needRefreshNodeTree?.dataSourceId &&
  //     data?.databaseName === needRefreshNodeTree?.databaseName &&
  //     data.nodeType === needRefreshNodeTree.nodeType) {
  //     setIsLoading(true);
  //     setNeedRefreshNodeTree(false);
  //     data.children = [];
  //     loadData(data)
  //   }
  // }, [needRefreshNodeTree])

  useEffect(() => {
    setShowChildren(showAllChildrenPenetrate);
  }, [showAllChildrenPenetrate])

  //展开-收起
  const handleClick = (data: ITreeNode) => {
    if (!showChildren && !data.children) {
      setIsLoading(true);
    }

    if (treeConfig[data.treeNodeType] && !data.children) {
      loadData(data)
    } else {
      setShowChildren(!showChildren);
    }
  };

  // const renderMenu = () => {
  //   return <TreeNodeRightClick
  //     data={data}
  //     setTreeData={setTreeData}
  //     setIsLoading={setIsLoading}
  //   />
  // }

  const recognizeIcon = (treeNodeType: TreeNodeType) => {
    if (treeNodeType === TreeNodeType.DATA_SOURCE) {
      return databaseMap[data.extraParams?.databaseType!]?.icon
    } else {
      return switchIcon[treeNodeType]?.[showChildren ? 'unfoldIcon' : 'icon'] || switchIcon[treeNodeType]?.icon
    }
  }

  function renderTitle(data: ITreeNode) {
    return <>
      <span>{data.name}</span>
      {
        data.columnType && data.treeNodeType === TreeNodeType.COLUMN &&
        <span style={{ color: callVar('--color-primary') }}>（{data.columnType}）</span>
      }
    </>
  }

  function nodeDoubleClick() {
    if (data.treeNodeType === TreeNodeType.TABLE) {
      dispatch({
        type: 'workspace/setDoubleClickTreeNodeData',
        payload: data
      });
    } else {
      handleClick(data);
    }
  }

  return show ? <>
    {/* <Tooltip placement="right" title={renderTitle(data)}>
    </Tooltip> */}
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
                <Iconfont className={classnames(styles.arrowsIcon, { [styles.rotateArrowsIcon]: showChildren })} code='&#xeb6d;' />
            }
            {/* <Iconfont className={classnames(styles.arrowsIcon, { [styles.rotateArrowsIcon]: showChildren })} code='&#xe608;' /> */}
          </div>
        }
        <div className={styles.dblclickArea} onDoubleClick={nodeDoubleClick}>
          <div className={styles.typeIcon}>
            <Iconfont code={recognizeIcon(data.treeNodeType)!}></Iconfont>
          </div>
          <div className={styles.contentText} >
            <div className={styles.name} dangerouslySetInnerHTML={{ __html: data.name }}></div>
            {data.treeNodeType === TreeNodeType.COLUMN && <div className={styles.type}>{data.columnType}</div>}
            {/* {data.treeNodeType === TreeNodeType.TABLE &&
              <Tooltip placement='right' title='我是描述呀我是描述呀我是描述呀我是描述呀我是描述呀我是描述呀'>
                <div className={styles.describe}>我是描述呀我是描述呀我是描述呀我是描述呀我是描述呀我是描述呀</div>
              </Tooltip>
            } */}
          </div>
        </div>
        <div className='moreBox'>
          <TreeNodeRightClick
            setIsLoading={setIsLoading}
            dispatch={dispatch}
            className={styles.moreButton}
            data={data}
            workspaceModel={workspaceModel}
          ></TreeNodeRightClick>
        </div>
      </div>
    </div>
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
})

export default Tree;
