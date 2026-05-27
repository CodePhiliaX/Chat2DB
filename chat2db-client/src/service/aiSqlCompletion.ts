import { EventSourcePolyfill } from 'event-source-polyfill';
import { v4 as uuidv4 } from 'uuid';
import { formatParams } from '@/utils/url';
import { IBoundInfo } from '@/typings';

export interface IAiSqlCompletionParams {
  boundInfo: IBoundInfo;
  message: string;
  ext: string;
}

export interface IAiSqlCompletionTask {
  promise: Promise<string>;
  cancel: () => void;
}

const getSSEBaseUrl = (): string => {
  const storedBaseURL = localStorage.getItem('_BaseURL');
  if (storedBaseURL) {
    return storedBaseURL;
  }
  if (location.href.indexOf('dist/index.html') > -1) {
    return `http://127.0.0.1:${__APP_PORT__ || '10824'}`;
  }
  const isDev = process.env.NODE_ENV === 'development';
  if (isDev) {
    return 'http://127.0.0.1:10821';
  }
  return location.origin;
};

const extractSqlFromContent = (content: string): string => {
  const trimmedContent = content.trim();
  const sqlBlockMatch = trimmedContent.match(/```sql\s*([\s\S]*?)```/i);
  if (sqlBlockMatch?.[1]) {
    return sqlBlockMatch[1].trim();
  }

  const codeBlockMatch = trimmedContent.match(/```\s*([\s\S]*?)```/);
  if (codeBlockMatch?.[1]) {
    return codeBlockMatch[1].trim();
  }

  return trimmedContent
    .replace(/^SQL\s*[:：]\s*/i, '')
    .replace(/^完整\s*SQL\s*[:：]\s*/i, '')
    .trim();
};

const cancelCompletionSession = async (sessionId: string) => {
  const DBHUB = localStorage.getItem('DBHUB');
  await fetch(`${getSSEBaseUrl()}/api/ai/chat/${sessionId}`, {
    method: 'DELETE',
    headers: {
      DBHUB: DBHUB || '',
    },
  });
};

export const requestAiSqlCompletion = (params: IAiSqlCompletionParams): IAiSqlCompletionTask => {
  const sessionId = uuidv4();
  const DBHUB = localStorage.getItem('DBHUB');
  const query = formatParams({
    message: params.message,
    promptType: 'SQL_COMPLETION',
    dataSourceId: params.boundInfo.dataSourceId,
    databaseName: params.boundInfo.databaseName,
    schemaName: params.boundInfo.schemaName,
    tableNames: (params.boundInfo as any).tableNames,
    ext: params.ext,
  });
  const eventSource = new EventSourcePolyfill(`${getSSEBaseUrl()}/api/ai/chat?${query}`, {
    headers: {
      uid: sessionId,
      DBHUB: DBHUB || '',
    },
    heartbeatTimeout: 12000000,
  });

  let settled = false;
  let content = '';
  let rejectPromise: ((reason?: any) => void) | null = null;

  const cleanup = () => {
    eventSource.close();
  };

  const promise = new Promise<string>((resolve, reject) => {
    rejectPromise = reject;

    eventSource.addEventListener('message', (event: any) => {
      const data = event.data;
      if (data === '[DONE]') {
        settled = true;
        cleanup();
        resolve(extractSqlFromContent(content));
        return;
      }

      try {
        const parsed = JSON.parse(data);
        if (parsed.content) {
          content += parsed.content;
        }
      } catch {
        content += data;
      }
    });

    eventSource.addEventListener('error', () => {
      if (settled) {
        return;
      }
      settled = true;
      cleanup();
      reject(new Error('AI SQL completion failed'));
    });
  });

  return {
    promise,
    cancel: () => {
      if (settled) {
        return;
      }
      settled = true;
      cleanup();
      rejectPromise?.(new Error('AI SQL completion cancelled'));
      cancelCompletionSession(sessionId);
    },
  };
};
