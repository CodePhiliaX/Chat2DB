import React, { memo, Fragment, ReactElement, cloneElement } from 'react';
import { Modal } from 'antd';

interface ITriggeredModal extends React.ComponentProps<typeof Modal> {
  children: ReactElement;
  modalContent: React.ReactNode;
  onOk?: any;
}

const TriggeredModal = memo<ITriggeredModal>((props) => {
  const { children, modalContent, ...orgs } = props;
  const [open, setOpen] = React.useState(false);
  const onClose = () => {
    setOpen(false);
  };

  const onOk = () => {
    orgs?.onOk?.(setOpen);
  };

  return (
    <Fragment>
      {cloneElement(children, { onClick: () => setOpen(true) })}
      <Modal {...orgs} open={open} onCancel={onClose} onOk={onOk}>
        {modalContent}
      </Modal>
    </Fragment>
  );
});

export default TriggeredModal;
