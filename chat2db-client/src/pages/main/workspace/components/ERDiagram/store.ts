/**
 * ER图状态管理Store
 * 使用Zustand管理ER图的状态，包括数据、加载状态、过滤条件、布局类型等
 */
import { create } from 'zustand';
import sqlServer, { IErDiagram, IErParams, IInferVirtualFkResult } from '@/service/sql';

/** 布局类型：力导向布局或层级布局 */
export type LayoutType = 'force' | 'dagre';

/** ER图Store接口定义 */
interface IErDiagramStore {
  erDiagramData: IErDiagram | null;
  loading: boolean;
  filterText: string;
  layoutType: LayoutType;
  includeVirtualFk: boolean;
  showOnlyRelatedTables: boolean;
  selectedTableId: string | null;

  fetchErDiagram: (params: IErParams) => Promise<void>;
  inferVirtualForeignKeys: (params: IErParams) => Promise<IInferVirtualFkResult>;
  deleteVirtualForeignKey: (edgeId: string, params: IErParams) => Promise<void>;
  setFilterText: (text: string) => void;
  setLayoutType: (type: LayoutType) => void;
  setSelectedTableId: (id: string | null) => void;
  setIncludeVirtualFk: (value: boolean) => void;
  setShowOnlyRelatedTables: (value: boolean) => void;
}

const useErDiagramStore = create<IErDiagramStore>((set, get) => ({
  erDiagramData: null,
  loading: false,
  filterText: '',
  layoutType: 'dagre',
  includeVirtualFk: true,
  showOnlyRelatedTables: false,
  selectedTableId: null,

  fetchErDiagram: async (params: IErParams) => {
    set({ loading: true });
    try {
      const res = await sqlServer.getErDiagram(params);
      set({ erDiagramData: res });
    } catch (error) {
      console.error('Failed to fetch ER diagram data:', error);
    } finally {
      set({ loading: false });
    }
  },

  inferVirtualForeignKeys: async (params: IErParams) => {
    try {
      const count = await sqlServer.inferVirtualForeignKeys(params);
      // 推断完成后刷新ER图
      await get().fetchErDiagram(params);
      return count;
    } catch (error) {
      console.error('Failed to infer virtual foreign keys:', error);
      throw error;
    }
  },

  deleteVirtualForeignKey: async (edgeId: string, params: IErParams) => {
    try {
      // 从ER图数据中找到对应的虚拟外键
      const currentData = get().erDiagramData;
      if (currentData) {
        const edge = currentData.edges.find(e => e.id === edgeId);
        if (edge && edge.virtual) {
          // 通过名称匹配删除
          await sqlServer.deleteVirtualForeignKey({
            dataSourceId: params.dataSourceId,
            databaseName: params.databaseName,
            schemaName: params.schemaName,
            tableName: edge.source,
            keyName: edge.id,
          });
          // 删除完成后刷新ER图
          await get().fetchErDiagram(params);
        }
      }
    } catch (error) {
      console.error('Failed to delete virtual foreign key:', error);
      throw error;
    }
  },

  setFilterText: (text: string) => set({ filterText: text }),
  setLayoutType: (type: LayoutType) => set({ layoutType: type }),
  setSelectedTableId: (id: string | null) => set({ selectedTableId: id }),
  setIncludeVirtualFk: (value: boolean) => set({ includeVirtualFk: value }),
  setShowOnlyRelatedTables: (value: boolean) => set({ showOnlyRelatedTables: value }),
}));

export default useErDiagramStore;
