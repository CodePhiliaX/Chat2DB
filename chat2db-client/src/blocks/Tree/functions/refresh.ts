import { ITreeNode } from '@/typings';

export const refreshTreeNode = (props:{
  treeNodeData: ITreeNode;
}) => {
  const { treeNodeData } = props;
  console.log(treeNodeData)
}
