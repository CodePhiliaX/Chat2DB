import React, { useEffect, useState } from 'react';
import { connect } from 'umi';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import { Tooltip } from 'antd';
import { ITreeNode } from '@/typings';
import { callVar, approximateTreeNode } from '@/utils';
import { TreeNodeType, databaseMap } from '@/constants';
import TreeNodeRightClick from './TreeNodeRightClick';
import { treeConfig, switchIcon, ITreeConfigItem } from './treeConfig';
import { IWorkspaceModelType, ICurWorkspaceParams } from '@/models/workspace';
import { useCommonStore } from '@/store/common';

interface IProps {
  className?: string;
  initialData?: ITreeNode[];
  workspaceModel: IWorkspaceModelType['state'];
  dispatch: any;
}

interface TreeNodeIProps {
  data: ITreeNode;
  level: number;
  show: boolean;
  setTreeData: Function;
  showAllChildrenPenetrate?: boolean;
  curWorkspaceParams: ICurWorkspaceParams;
  dispatch: any;
}

const dvaModel = connect(({ workspace }: { workspace: IWorkspaceModelType }) => ({
  workspaceModel: workspace,
}));

const Tree = dvaModel((props: IProps) => {
  const { className, initialData, workspaceModel, dispatch } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | undefined>();
  const [searchedTreeData, setSearchedTreeData] = useState<ITreeNode[] | null>(null);

  useEffect(() => {
    setTreeData(initialData);
  }, [initialData]);

  function filtrationDataTree(keywords: string) {
    if (!keywords) {
      setSearchedTreeData(null);
    } else if (treeData?.length && keywords) {
      setSearchedTreeData(approximateTreeNode(treeData, keywords));
    }
  }

  return (
    <div className={classnames(className, styles.treeBox)}>
      {(searchedTreeData || treeData)?.map((item, index) => {
        return (
          <TreeNode
            curWorkspaceParams={workspaceModel.curWorkspaceParams}
            dispatch={dispatch}
            setTreeData={setTreeData}
            key={item.name + index}
            show={true}
            level={0}
            data={item}
          />
        );
      })}
    </div>
  );
});

const TreeNode = (props: TreeNodeIProps) => {
  const {
    setTreeData,
    data,
    level,
    show = false,
    showAllChildrenPenetrate = false,
    dispatch,
    curWorkspaceParams,
  } = props;
  const [showChildren, setShowChildren] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const indentArr = new Array(level).fill('indent');
  const [openTooltipComment, setOpenTooltipComment] = useState(false);
  const contentTextRef = React.useRef<HTMLDivElement>(null);
  const { setFocusedContent } = useCommonStore((state) => {
    return {
      setFocusedContent: state.setFocusedContent,
    };
  });

  useEffect(() => {
    if (!data.comment) {
      return;
    }
    if (contentTextRef.current) {
      const contentTextRefDom = contentTextRef.current;
      contentTextRefDom.addEventListener('mouseenter', () => {
        setOpenTooltipComment(true);
      });
      contentTextRefDom.addEventListener('mouseleave', () => {
        setOpenTooltipComment(false);
      });
    }
  }, [contentTextRef]);

  function loadData(data: ITreeNode) {
    const treeNodeConfig: ITreeConfigItem = treeConfig[data.treeNodeType];
    treeNodeConfig
      .getChildren?.({
        ...data.extraParams,
        extraParams: {
          ...data.extraParams,
        },
        // ...data,
        // ...(data.extraParams || {}),
      })
      .then((res) => {
        if (res.length) {
          data.children = res;
          setShowChildren(true);
          setIsLoading(false);
        } else {
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
      })
      .catch(() => {
        setIsLoading(false);
      });
  }

  useEffect(() => {
    setShowChildren(showAllChildrenPenetrate);
  }, [showAllChildrenPenetrate]);

  //展开-收起
  const handleClick = (data: ITreeNode) => {
    if (!showChildren && !data.children) {
      setIsLoading(true);
    }

    if (treeConfig[data.treeNodeType] && !data.children) {
      loadData(data);
    } else {
      setShowChildren(!showChildren);
    }
  };

  const recognizeIcon = (treeNodeType: TreeNodeType) => {
    if (treeNodeType === TreeNodeType.DATA_SOURCE) {
      return databaseMap[data.extraParams?.databaseType!]?.icon;
    } else {
      return switchIcon[treeNodeType]?.[showChildren ? 'unfoldIcon' : 'icon'] || switchIcon[treeNodeType]?.icon;
    }
  };

  function nodeDoubleClick() {
    if (
      data.treeNodeType === TreeNodeType.TABLE ||
      data.treeNodeType === TreeNodeType.FUNCTION ||
      data.treeNodeType === TreeNodeType.TRIGGER ||
      data.treeNodeType === TreeNodeType.VIEW ||
      data.treeNodeType === TreeNodeType.PROCEDURE
    ) {
      dispatch({
        type: 'workspace/setDoubleClickTreeNodeData',
        payload: data,
      });
    }
    // else if (data.treeNodeType === TreeNodeType.TABLE) {
    //   dispatch({
    //     type: 'workspace/setCreateTabIntro',
    //     payload: {
    //       type: CreateTabIntroType.EditTableData,
    //       workspaceTabType: WorkspaceTabType.EditTableData,
    //       treeNodeData: data,
    //     },
    //   });
    // }
    else {
      handleClick(data);
    }
  }

  const handelClickTreeNode = () => {
    setFocusedContent((data.key || '') as any);
  };

  return show ? (
    <>
      <TreeNodeRightClick
        setIsLoading={setIsLoading}
        dispatch={dispatch}
        className={styles.moreButton}
        data={data}
        curWorkspaceParams={curWorkspaceParams}
        trigger={['contextMenu']}
      >
        <Tooltip
          open={openTooltipComment}
          placement="right"
          color={window._AppThemePack.colorPrimary}
          title={data.comment}
        >
          <div
            className={classnames(styles.treeNode, { [styles.hiddenTreeNode]: !show })}
            onClick={handelClickTreeNode}
            data-chat2db-general-can-copy-element
          >
            <div className={styles.left}>
              {indentArr.map((item, i) => {
                return <div key={i} className={styles.indent} />;
              })}
            </div>
            <div className={styles.right}>
              {!data.isLeaf && (
                <div
                  onClick={handleClick.bind(null, data)}
                  className={classnames(styles.arrows, { [styles.loadingArrows]: isLoading })}
                >
                  {isLoading ? (
                    <div className={styles.loadingIcon}>
                      <Iconfont code="&#xe6cd;" />
                    </div>
                  ) : (
                    <Iconfont
                      className={classnames(styles.arrowsIcon, { [styles.rotateArrowsIcon]: showChildren })}
                      code="&#xeb6d;"
                    />
                  )}
                </div>
              )}
              <div className={styles.dblclickArea} onDoubleClick={nodeDoubleClick}>
                <div className={styles.typeIcon}>
                  <Iconfont code={recognizeIcon(data.treeNodeType)!} />
                </div>
                <div className={styles.contentText} ref={contentTextRef}>
                  <div className={styles.name} dangerouslySetInnerHTML={{ __html: data.name }} />
                  {data.treeNodeType === TreeNodeType.COLUMN && <div className={styles.type}>{data.columnType}</div>}
                </div>
              </div>
              <div className={styles.moreBox}>
                <TreeNodeRightClick
                  setIsLoading={setIsLoading}
                  dispatch={dispatch}
                  className={styles.moreButton}
                  data={data}
                  curWorkspaceParams={curWorkspaceParams}
                />
              </div>
            </div>
          </div>
        </Tooltip>
      </TreeNodeRightClick>
      {data.children?.map((item: any, i: number) => {
        return (
          <TreeNode
            curWorkspaceParams={curWorkspaceParams}
            dispatch={dispatch}
            key={i}
            data={item}
            level={level + 1}
            setTreeData={setTreeData}
            showAllChildrenPenetrate={showAllChildrenPenetrate}
            show={showChildren && show}
          />
        );
      })}
    </>
  ) : (
    <></>
  );
};

export default Tree;
