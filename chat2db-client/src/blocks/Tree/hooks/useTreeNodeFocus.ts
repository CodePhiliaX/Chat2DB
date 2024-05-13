import { useTreeStore } from '../treeStore';

export const useTreeNodeFocus = (treeId) => {
  const focusId = useTreeStore((state) => state.focusId);
  return focusId === treeId;
}
