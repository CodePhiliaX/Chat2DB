import React, { useEffect, useRef, useState, useContext } from 'react';
import { Input, Button, message } from 'antd';
import classnames from 'classnames';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import mila from 'markdown-it-link-attributes';
import mdKatex from '@traptitech/markdown-it-katex';
import { EventSourcePolyfill } from 'event-source-polyfill';
import 'katex/dist/katex.min.css';
import styles from './index.less';
import { uuid } from '@/utils/common';
import './hljs.css';
import { DatabaseContext } from '@/context/database';

const md = new MarkdownIt({
  linkify: true,
  breaks: true,
  highlight: (str: string, lang: string, attrs: string): string => {
    let content = str;
    if (lang && hljs.getLanguage(lang)) {
      try {
        content = hljs.highlight(str, {
          language: lang,
          ignoreIllegals: true,
        }).value;
      } catch (e) {
        console.log(e);
        return str;
      }
    } else {
      content = md.utils.escapeHtml(str);
    }
    return `<pre class="hljs" style="max-width: 50vw; overflow: auto"><code>${content}</code></pre>`;
  },
});

md.use(mdKatex, {
  blockClass: 'katexmath-block rounded-md p-[10px]',
  errorColor: ' #cc0000',
});
md.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });

interface IChatAIProps {
  /** consoleId */
  consoleId: number;
  /** 数据源id */
  dataSourceId: number;
  /** DB名称 */
  databaseName: string;
  /** 被使用形式 */
  type: 'page' | 'embed';
  classNames: string;

  onClickImport?: (answer: string) => void;
}

function formatParams(obj: { [key: string]: any }) {
  let params = '';
  for (let key in obj) {
    if (obj[key]) {
      params += `${key}=${obj[key]}&`;
    }
  }
  return params;
}

function ChatAI(props: IChatAIProps) {
  const { dataSourceId, databaseName, consoleId, type = 'page' } = props;
  const [messages, setMessages] = useState<
    Array<{ key: string; question: string; answer: string }>
  >([]);
  const [inputValue, setInputValue] = useState<string>('');
  const [question, setQuestion] = useState('');
  const [autoScroll, setAutoScroll] = useState(false);
  const [isChatting, setIsChatting] = useState(false);
  const flowRef = useRef<HTMLDivElement>(null);
  const curSourceTarget = useRef<EventTarget>(null);
  const uid = useRef<string>('');
  const curMessageIndex = useRef<number>(0);
  const { setAiImportSql } = useContext(DatabaseContext);

  useEffect(() => {
    if (flowRef?.current && autoScroll) {
      flowRef?.current?.scrollTo(0, flowRef?.current?.scrollHeight);
    }
  }, [flowRef?.current?.scrollHeight]);

  useEffect(() => {
    uid.current = uuid();
  }, [consoleId]);

  useEffect(() => {
    const question = messages[curMessageIndex.current]?.question;
    if (!question) return;

    let text = '';
    const params = formatParams({
      dataSourceId,
      databaseName,
      message: question,
    });
    const url =
      type === 'page'
        ? `${window._BaseURL}/api/ai/chat1`
        : `${window._BaseURL}/api/ai/chat`;
    const eventSource = new EventSourcePolyfill(`${url}?${params}`, {
      headers: {
        uid: uid.current,
        DBHUB: localStorage.getItem('DBHUB') || '',
      },
    });

    eventSource.onopen = (event) => {
      curSourceTarget.current = event.target;
    };

    eventSource.onmessage = (event) => {
      if (event.data == '[DONE]') {
        setIsChatting(false);
        console.log('xx=>', messages[curMessageIndex.current]?.answer);
        curMessageIndex.current++;
        if (curSourceTarget?.current) {
          curSourceTarget?.current?.close();
        }
        return;
      }
      let json_data = JSON.parse(event.data);
      if (json_data.content == null || json_data.content == 'null') {
        text = '';
        return;
      }
      text = text + json_data.content;

      const message = messages[curMessageIndex.current];
      if (message) {
        message.answer = text;
      }
      setMessages([...messages]);
    };
    eventSource.onerror = (event) => {
      console.log('onerror', event);
      curMessageIndex.current++;
      message.warning('服务异常请重试并联系开发者！');
      if (event.readyState === EventSource.CLOSED) {
        console.log('connection is closed');
      } else {
        console.log('Error occured', event);
      }
      event.target.close();
    };
    eventSource.addEventListener('customEventName', (event) => {
      console.log('Message id is ' + event.lastEventId);
    });
    return () => {
      eventSource.removeEventListener('customEventName', (event) => {
        console.log('remove eventSource');
      });
    };
  }, [messages.length, props.dataSourceId, props.databaseName]);

  const handleInputChange = (event) => {
    setInputValue(event?.target?.value);
  };

  const handleSendMessage = (event) => {
    if (event.nativeEvent.isComposing && event.key === 'Enter') {
      event.preventDefault();
      return;
    }

    if (inputValue.trim() !== '') {
      setInputValue('');
      setQuestion(inputValue);
      setAutoScroll(true);
      setIsChatting(true);
      const key = uuid();
      setMessages([
        ...messages,
        {
          key,
          question: inputValue,
          answer: '',
        },
      ]);
    }
  };

  return (
    <div className={classnames(props.classNames, styles.chatAI)}>
      <div className={styles.chatFlow} ref={flowRef}>
        {(messages || []).map((item) => (
          <div className={styles.chatItem} key={item.key}>
            <div className={styles.title}>问题：{item.question}</div>
            <div className={styles.contentWrapper}>
              <div
                className={styles.content}
                dangerouslySetInnerHTML={{ __html: md.render(item.answer) }}
              ></div>
              {!isChatting && type === 'embed' ? (
                <div
                  onClick={() => setAiImportSql(item.answer)}
                  className={styles.importBtn}
                >
                  一键导入
                </div>
              ) : null}
            </div>
          </div>
        ))}
      </div>

      <div className={styles.chatBtnBlock}>
        {!isChatting ? (
          <Input
            className={styles.chatBtn}
            placeholder="请输入"
            value={inputValue}
            onChange={handleInputChange}
            onPressEnter={handleSendMessage}
            suffix={
              <Button type="text" onClick={handleSendMessage}>
                发送
              </Button>
            }
          />
        ) : (
          <Button
            type="text"
            size="large"
            style={{ marginLeft: '20px' }}
            onClick={() => {
              curSourceTarget?.current?.close();
              setIsChatting(false);
              curMessageIndex.current++;
            }}
          >
            停止
          </Button>
        )}
      </div>
    </div>
  );
}

export default ChatAI;
