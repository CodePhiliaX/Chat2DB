import { CreateTabIntroType, WorkspaceTabType } from '@/constants';
import { ITreeNode } from '@/typings';


export interface ICreateTabIntro {
  type: CreateTabIntroType;
  workspaceTabType: WorkspaceTabType;
  treeNodeData: ITreeNode;
}

export interface IWorkspaceTab {
  id: number | string; // Tab的id
  type: WorkspaceTabType; // 工作区tab的类型
  title: string; // 工作区tab的名称
  uniqueData?: any;
}

