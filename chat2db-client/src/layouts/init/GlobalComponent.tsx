import React from 'react';
import MyNotification from '@/components/MyNotification';
import Modal from '@/components/Modal/BaseModal';

const GlobalComponent = () => {
  return <>
    <MyNotification />
    <Modal />
  </>
}

export default GlobalComponent;
