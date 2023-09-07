import { CreateTabIntroType, TabType } from '@/constants';
import { ITreeNode } from '@/typings';


export interface ICreateTabIntro {
  type: CreateTabIntroType;
  tabType: TabType;
  treeNodeData: ITreeNode;
}

