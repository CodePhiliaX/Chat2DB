import React, { memo, useEffect, useState } from 'react';
import { Modal as AntdModal } from 'antd';
import { injectOpenModal } from '@/store/common/components';

export type IModalData = {
  title?: string;
  width?: string;
  footer?: React.ReactNode | false;
  content: React.ReactNode | false
} | null | false

const Modal = memo(() => {
  const [open, setOpen] = useState(false);
  const [modalData, setModalData] = useState<IModalData>(null);

  const openModal = (params:IModalData)=>{
    if(params === false){
      setOpen(false)
    }else{
      setOpen(true)
      setModalData(params)
    }
  }

  useEffect(() => {
    injectOpenModal(openModal);
  }, []);

  return (
    !!modalData &&
    <AntdModal
      title={modalData.title}
      open={open}
      width={modalData.width} 
      onCancel={() => {
        setOpen(false);
      }}
      destroyOnClose={true}
      footer={modalData.footer || false}
    >
      {modalData.content}
    </AntdModal>
  );
});

export default Modal;
