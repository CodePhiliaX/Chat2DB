export type FocusTreeNode = {
  dataSourceId: number;
  dataSourceName: string;
  databaseType: string;
  databaseName?: string;
  schemaName?: string;
  tableName?: string;
} | null;

export interface TreeState {
  focusId: number | string | null;
  focusTreeNode: FocusTreeNode;
}

export const initTreeState = {
  focusId: null,
  focusTreeNode: null,
};
