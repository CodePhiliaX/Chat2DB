import { useCommonStore } from './index';
import { IModalData } from '@/components/Modal/BaseModal';

export interface IComponentsContent {
  openModal: ((params: IModalData) => void) | null;
}

export const initComponentsContent = {
  openModal: null,
};

export const injectOpenModal = (openModal: IComponentsContent['openModal']) => {
  return useCommonStore.setState({ openModal });
};

export const openModal = (modal: IModalData) => {
  return useCommonStore.getState().openModal?.(modal);
};
