import React from 'react';
import i18n from '@/i18n';
import Output from '@/components/Output';
import SaveList from '../SaveList';

interface IToolbar {
  code: string;
  title: string;
  icon: string;
  components: any;
}

export const extendConfig: IToolbar[] = [
  // {
  //   code: 'ai',
  //   title: 'AI',
  //   icon: '\ue8ad',
  //   components: <div>ai</div>,
  // },
  {
    code: 'executiveLog',
    title: i18n('common.title.executiveLogging'),
    icon: '\ue8ad',
    components: <Output />,
  },
  {
    code: 'saveList',
    title: i18n('workspace.title.savedConsole'),
    icon: '\ue619',
    components: <SaveList />,
  },
];
