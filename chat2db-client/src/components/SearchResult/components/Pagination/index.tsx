import React, { useEffect, useState } from 'react';
import { VerticalLeftOutlined, VerticalRightOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons';
import cs from 'classnames';
import { Button, InputNumber, Popover, Select } from 'antd';
import { IResultConfig } from '@/typings';
import i18n from '@/i18n';
import _ from 'lodash';
import styles from './index.less';

interface IProps {
  onPageSizeChange?: (pageSize: number) => void;
  onPageNoChange?: (pageNo: number) => void;
  onClickTotalBtn?: () => Promise<number | undefined>;
  paginationConfig: IResultConfig;
}
type IIconType = 'pre' | 'next' | 'first' | 'last';
export default function Pagination(props: IProps) {
  const { onPageNoChange, onPageSizeChange, paginationConfig } = props;
  const [inputValue, setInputValue] = useState<number | null>(1);
  const [totalLoading, setTotalLoading] = useState(false);

  useEffect(() => {
    setInputValue(paginationConfig?.pageNo ?? 1);
  }, [paginationConfig?.pageNo]);

  const onInputNumberChange = (value: number | null) => {
    setInputValue(value);
  };

  const onInputNumberBlur = () => {
    if (_.isNumber(inputValue)) {
      onPageNoChange && onPageNoChange(inputValue);
    } else {
      setInputValue(1);
      onPageNoChange && onPageNoChange(1);
    }
  };

  const handleClickTotalBtn = async () => {
    if (!props.onClickTotalBtn) return;
    setTotalLoading(true);

    const res = await props.onClickTotalBtn();
    setTotalLoading(false);
    return res;
  };

  const handleClickIcon = async (type: IIconType) => {
    if (!onPageNoChange || !paginationConfig) return;
    if (handleIsDisabled(type)) return;
    switch (type) {
      case 'first':
        onPageNoChange(1);
        break;
      case 'last':
        {
          const total = await handleClickTotalBtn();
          const { pageSize } = paginationConfig || {};
          if (_.isNumber(total) && _.isNumber(pageSize)) {
            props.onPageNoChange && props.onPageNoChange(Math.ceil(total / pageSize));
          }
        }
        break;
      case 'pre':
        onPageNoChange(paginationConfig?.pageNo - 1);
        break;
      case 'next':
        onPageNoChange(paginationConfig?.pageNo + 1);
        break;
      default:
        break;
    }
  };

  const handleIsDisabled = (type: IIconType) => {
    if (!paginationConfig) {
      return false;
    }
    if (type === 'first') {
      return paginationConfig?.pageNo === 1;
    }
    if (type === 'pre') {
      return paginationConfig?.pageNo === 1;
    }

    const isNumber = _.isNumber(paginationConfig.total);
    const totalShow = paginationConfig.pageNo * paginationConfig.pageSize;
    if (type === 'next' || type === 'last') {
      if (isNumber) {
        return totalShow > (paginationConfig.total as number);
      }
      return !paginationConfig?.hasNextPage;
    }

    // if (type === 'last') {
    //   if (isNumber) {
    //     return totalShow > (paginationConfig.total as number);
    //   }
    //   return return !paginationConfig?.hasNextPage;;
    // }

    return true;
  };

  return (
    <div className={styles.paginationWrapper}>
      <VerticalRightOutlined
        className={cs(styles['item-icon'], {
          [styles['item-icon-disabled']]: handleIsDisabled('first'),
        })}
        onClick={() => handleClickIcon('first')}
      />
      <LeftOutlined
        className={cs(styles['item-icon'], {
          [styles['item-icon-disabled']]: handleIsDisabled('pre'),
        })}
        onClick={() => handleClickIcon('pre')}
      />
      <InputNumber
        className={styles['input-number']}
        size="small"
        min={1}
        value={inputValue}
        controls={false}
        onPressEnter={onInputNumberBlur}
        onBlur={onInputNumberBlur}
        onChange={onInputNumberChange}
      />

      <RightOutlined
        className={cs(styles['item-icon'], {
          [styles['item-icon-disabled']]: handleIsDisabled('next'),
        })}
        onClick={() => handleClickIcon('next')}
      />
      <VerticalLeftOutlined
        className={cs(styles['item-icon'], {
          [styles['item-icon-disabled']]: handleIsDisabled('last'),
        })}
        onClick={() => handleClickIcon('last')}
      />

      <Select
        popupMatchSelectWidth={false}
        size="small"
        value={paginationConfig?.pageSize ?? 200}
        onChange={onPageSizeChange}
        options={[
          { label: 10, value: 10 },
          { label: 50, value: 50 },
          { label: 100, value: 100 },
          { label: 200, value: 200 },
          { label: 500, value: 500 },
          { label: 1000, value: 1000 },
        ]}
      />

      <Popover mouseEnterDelay={0.8} content={i18n('workspace.table.total.tip')}>
        <Button type="link" loading={totalLoading} onClick={handleClickTotalBtn}>
          {i18n('workspace.table.total')}：{paginationConfig?.total}
        </Button>
      </Popover>
    </div>
  );
}
