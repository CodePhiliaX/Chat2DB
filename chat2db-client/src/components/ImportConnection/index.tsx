import React from 'react';
import { Modal, Upload, Button, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';

interface FileUploadModalProps {
  open: boolean;
  onClose: () => void;
  onUploaded?: (file: File) => void;
  onConfirm?: () => void;
}

const FileUploadModal: React.FC<FileUploadModalProps> = ({ open, onClose, onUploaded, onConfirm }) => {
  const beforeUpload = (file: File) => {
    const fileName = file.name.toLowerCase();
    if (fileName.includes('navicat')) {
      message.success('Navicat file detected');
      onUploaded && onUploaded(file);
    } else if (fileName.includes('dbeaver')) {
      message.success('DBeaver file detected');
      onUploaded && onUploaded(file);
    } else if (fileName.includes('datagrip')) {
      message.success('DataGrip file detected');
      onUploaded && onUploaded(file);
    } else {
      message.error('File type not recognized');
      return false;
    }
    return false; // Prevent auto-upload for the sake of this demo
  };

  const renderSelect = () => {
    // 下拉框 选择Navciat、dbever、datagrip
  };
  return (
    <Modal
      title="Upload your file"
      open={open}
      onCancel={onClose}
      onOk={onConfirm}
      footer={[
        <Button key="back" onClick={onClose}>
          Cancel
        </Button>,
        <Button key="submit" type="primary" onClick={onConfirm}>
          Confirm
        </Button>,
      ]}
    >
      <Upload
        action="/api/converter/ncx/upload"
        name="file"
        accept=".ncx"
        beforeUpload={beforeUpload}
        onChange={(info) => {
          console.log(info);
        }}
      >
        <Button icon={<UploadOutlined />}>Select File</Button>
      </Upload>
    </Modal>
  );
};

export default FileUploadModal;
