import React, { memo, useEffect, useMemo, useState } from 'react';
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

interface IProps {
  className?: string;
  initialData: ITreeNode[] | null;
}

interface TreeNodeIProps {
  data: ITreeNode;
  level: number;
}

const Tree = memo((props: IProps) => {
  const { className, initialData } = props;
  const [treeData, setTreeData] = useState<ITreeNode[] | null>(null);

  useEffect(() => {
    setTreeData(initialData);
  }, [initialData]);

  const treeNodes = useMemo(() => {
    return treeData?.map((item, index) => {
      return <TreeNode key={item.name + index} level={0} data={item} />;
    });
  }, [treeData]);

  return <div className={classnames(className, styles.treeBox)}>{treeNodes}</div>;
});

const TreeNode = memo((props: TreeNodeIProps) => {
  const { data: initData, level } = props;
  const [isLoading, setIsLoading] = useState(false);
  const indentArr = new Array(level).fill('indent');
  const [treeNodeData, setTreeNodeData] = useState<ITreeNode>({
    ...initData,
  });
  const isFocus = useTreeStore((state) => state.focusId) === treeNodeData.key;

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
      .then((res) => {
        if (res.length) {
          setTimeout(() => {
            setTreeNodeData({
              ...treeNodeData,
              children: res,
            });
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

  function nodeDoubleClick() {
    // if (
    //   data.treeNodeType === TreeNodeType.TABLE ||
    //   data.treeNodeType === TreeNodeType.FUNCTION ||
    //   data.treeNodeType === TreeNodeType.TRIGGER ||
    //   data.treeNodeType === TreeNodeType.VIEW ||
    //   data.treeNodeType === TreeNodeType.PROCEDURE
    // ) {
    //   dispatch({
    //     type: 'workspace/setDoubleClickTreeNodeData',
    //     payload: data,
    //   });
    // }
    // else {
    //   handleClick(data);
    // }
  }

  // 点击节点
  const handelClickTreeNode = () => {
    useCommonStore.setState({
      focusedContent: (treeNodeData.name || '') as any,
    });
    setFocusId(treeNodeData.key || '');
  };

  // 递归渲染
  const treeNodes = useMemo(() => {
    return treeNodeData.children?.map((item: any, index: number) => {
      return <TreeNode key={item.name + index} level={level + 1} data={item} />;
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
        onClick: item.onClick,
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
              <div className={styles.dblclickArea} onDoubleClick={nodeDoubleClick}>
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

  return (
    <>
      {treeNodeDom}
      {treeNodes}
    </>
  );
});

export default Tree;
