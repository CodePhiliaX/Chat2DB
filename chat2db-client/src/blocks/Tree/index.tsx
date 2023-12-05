import React, { memo, useEffect, useMemo, useState, forwardRef, createContext, useContext } from 'react';
import styles from './index.less';
import classnames from 'classnames';
import Iconfont from '@/components/Iconfont';
import { Tooltip, Dropdown } from 'antd';
import { ITreeNode } from '@/typings';
import { TreeNodeType, databaseMap } from '@/constants';
import { treeConfig, switchIcon, ITreeConfigItem } from './treeConfig';
import { useCommonStore } from '@/store/common';
import LoadingGracile from '@/components/Loading/LoadingGracile';
import { setFocusId, useTreeStore } from './treeStore';
import { useGetRightClickMenu } from './hooks/useGetRightClickMenu';
import MenuLabel from '@/components/MenuLabel';
import LoadingContent from '@/components/Loading/LoadingContent';

interface IProps {
  className?: string;
  treeData: ITreeNode[] | null;
  searchValue: string;
}
interface TreeNodeIProps {
  data: ITreeNode;
  level: number;
  setShowParentNode?: (value: boolean) => void;
}

interface IContext {
  searchValue?: string;
}

export const Context = createContext<IContext>({} as any);

const Tree = (props: IProps) => {
  const { className, treeData: outerTreeData, searchValue } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);

  useEffect(() => {
    setTreeData(outerTreeData);
  }, [outerTreeData]);

  const treeNodes = useMemo(() => {
    return treeData?.map((item, index) => {
      return <TreeNode key={item.name + index} level={0} data={item} />;
    });
  }, [treeData]);
  // 如果treeBox滚动的高度>0那么久加一个上边框
  const [treeBoxScrollTop, setTreeBoxScrollTop] = useState<number>(0);
  const handleScroll = (e: any) => {
    setTreeBoxScrollTop(e.target.scrollTop);
  };

  return (
    <Context.Provider value={{ searchValue }}>
      <LoadingContent isLoading={!treeData} className={classnames(className)}>
        <div
          className={classnames(styles.treeBox, { [styles.treeBoxScroll]: treeBoxScrollTop > 0 })}
          onScroll={handleScroll}
        >
          {treeNodes}
        </div>
      </LoadingContent>
    </Context.Provider>
  );
};

const TreeNode = memo((props: TreeNodeIProps) => {
  const { data: initData, level, setShowParentNode: _setShowParentNode } = props;
  const [isLoading, setIsLoading] = useState(false);
  const indentArr = new Array(level).fill('indent');
  const { searchValue } = useContext(Context);

  // 加载数据
  function loadData(_props?: { refresh: boolean }) {
    const treeNodeConfig: ITreeConfigItem = treeConfig[treeNodeData.pretendNodeType || treeNodeData.treeNodeType];
    setIsLoading(true);
    setTreeNodeData({
      ...treeNodeData,
      children: null,
    });
    treeNodeConfig
      .getChildren?.({
        ...treeNodeData.extraParams,
        extraParams: {
          ...treeNodeData.extraParams,
        },
        refresh: _props?.refresh || false,
      })
      .then((res: any) => {
        if (res.length || res.data) {
          setTimeout(() => {
            console.log(res);
            if (res.data) {
              // res.data每次只插入200条数据，间隔30ms
              const count = res.data.length / 200;
              for (let i = 0; i < count; i++) {
                setTimeout(() => {
                  setTreeNodeData({
                    ...treeNodeData,
                    children: res.data.slice(0, (i + 1) * 50),
                    total: res.total,
                  });
                }, 100 * i);
              }

              // setTreeNodeData({
              //   ...treeNodeData,
              //   children: res.data,
              //   total: res.total,
              // });
            } else {
              setTreeNodeData({
                ...treeNodeData,
                children: res,
              });
            }
            setIsLoading(false);
          }, 200);
        } else {
          // 处理树可能出现不连续的情况
          if (treeNodeConfig.next) {
            treeNodeData.pretendNodeType = treeNodeConfig.next;
            loadData();
          } else {
            setTreeNodeData({
              ...treeNodeData,
              children: [],
            });
            setIsLoading(false);
          }
        }
      })
      .catch(() => {
        setIsLoading(false);
      });
  }

  // 当前节点数据
  const [treeNodeData, setTreeNodeData] = useState<ITreeNode>({
    ...initData,
    loadData,
  });

  // 当前节点是否是focus
  const isFocus = useTreeStore((state) => state.focusId) === treeNodeData.uuid;

  // 如果showTreeNode为true，那么他的父节点也要展示
  const [showParentNode, setShowParentNode] = useState<boolean>(false);

  const showTreeNode = useMemo(() => {
    const reg = new RegExp(searchValue || '', 'i');
    return reg.test(treeNodeData.name || '');
  }, [searchValue]);

  useEffect(() => {
    if (showTreeNode) {
      _setShowParentNode?.(true);
    }
  }, [showTreeNode]);

  useEffect(() => {
    if (showParentNode) {
      _setShowParentNode?.(true);
    }
  }, [showParentNode]);

  //展开-收起
  const handleClick = () => {
    if (
      treeConfig[treeNodeData.treeNodeType] &&
      (treeNodeData.children === null || treeNodeData.children === undefined)
    ) {
      loadData();
    } else {
      setTreeNodeData({
        ...treeNodeData,
        children: null,
      });
    }
  };

  // 找到对应的icon
  const recognizeIcon = (treeNodeType: TreeNodeType) => {
    if (treeNodeType === TreeNodeType.DATA_SOURCE) {
      return databaseMap[treeNodeData.extraParams!.databaseType!]?.icon;
    } else {
      return (
        switchIcon[treeNodeType]?.[treeNodeData.children ? 'unfoldIcon' : 'icon'] || switchIcon[treeNodeType]?.icon
      );
    }
  };

  // 点击节点
  const handelClickTreeNode = () => {
    useCommonStore.setState({
      focusedContent: (treeNodeData.name || '') as any,
    });
    setFocusId(treeNodeData.uuid || '');
  };

  // 双击节点
  const handelDoubleClickTreeNode = () => {
    rightClickMenu.find((item) => item.doubleClickTrigger)?.onClick(treeNodeData);
  };

  // 递归渲染
  const treeNodes = useMemo(() => {
    return treeNodeData.children?.map((item: any, index: number) => {
      return (
        <TreeNode
          setShowParentNode={setShowParentNode}
          key={item.name + index}
          level={level + 1}
          data={{
            ...item,
            parentNode: treeNodeData,
          }}
        />
      );
    });
  }, [treeNodeData]);

  const rightClickMenu = useGetRightClickMenu({
    treeNodeData,
    loadData,
  });

  const treeNodeDom = useMemo(() => {
    const dropdownsItems: any = rightClickMenu.map((item) => {
      return {
        key: item.key,
        onClick: () => {
          item.onClick(treeNodeData);
        },
        label: <MenuLabel icon={item.labelProps.icon} label={item.labelProps.label} />,
      };
    });

    return (
      <Dropdown
        trigger={['contextMenu']}
        menu={{
          items: dropdownsItems,
          style: dropdownsItems?.length ? {} : { display: 'none' }, // 有菜单项才显示
        }}
        overlayStyle={{
          zIndex: 1080,
        }}
      >
        <Tooltip placement="right" color={window._AppThemePack.colorPrimary} title={treeNodeData.comment}>
          <div
            className={classnames(styles.treeNode, { [styles.treeNodeFocus]: isFocus })}
            onClick={handelClickTreeNode}
            onContextMenu={handelClickTreeNode}
            onDoubleClick={handelDoubleClickTreeNode}
            data-chat2db-general-can-copy-element
          >
            <div className={styles.left}>
              {indentArr.map((item, i) => {
                return <div key={i} className={styles.indent} />;
              })}
            </div>
            <div className={styles.right}>
              {!treeNodeData.isLeaf && (
                <div onClick={handleClick} className={classnames(styles.arrows, { [styles.loadingArrows]: isLoading })}>
                  {isLoading ? (
                    <LoadingGracile />
                  ) : (
                    <Iconfont
                      className={classnames(styles.arrowsIcon, {
                        [styles.rotateArrowsIcon]: treeNodeData.children,
                      })}
                      code="&#xe641;"
                    />
                  )}
                </div>
              )}
              <div className={styles.dblclickArea}>
                <div className={styles.typeIcon}>
                  <Iconfont code={recognizeIcon(treeNodeData.treeNodeType)!} />
                </div>
                <div className={styles.contentText}>
                  <div className={styles.name} dangerouslySetInnerHTML={{ __html: treeNodeData.name }} />
                  {treeNodeData.treeNodeType === TreeNodeType.COLUMN && (
                    <div className={styles.type}>
                      {/* 转小写 */}
                      {treeNodeData.columnType?.toLowerCase()}
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </Tooltip>
      </Dropdown>
    );
  }, [isFocus, isLoading, rightClickMenu]);

  // const sectionHeight = useMemo(() => {
  //   if (treeNodeData.total && treeNodeData.children) {
  //     return `${treeNodeData.total * 26}px`;
  //   } else {
  //     return 'auto';
  //   }
  // }, [treeNodeData.total, treeNodeData.children]);

  return (
    // style={{ height: sectionHeight }}
    <div>
      {(showTreeNode || showParentNode) && treeNodeDom}
      {treeNodes}
    </div>
  );
});

export default memo(forwardRef(Tree));
