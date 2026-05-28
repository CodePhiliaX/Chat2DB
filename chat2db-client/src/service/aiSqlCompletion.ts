import { v4 as uuidv4 } from 'uuid';
import { cancelSSESession, createSSEConnection } from '@/utils/sse';
import { formatParams } from '@/utils/url';
import { IBoundInfo } from '@/typings';

export interface IAiSqlCompletionParams {
  boundInfo: IBoundInfo;
  message: string;
  ext?: string;
}

export interface IAiSqlCompletionTask {
  promise: Promise<string>;
  cancel: () => void;
}

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

export const requestAiSqlCompletion = (params: IAiSqlCompletionParams): IAiSqlCompletionTask => {
  const sessionId = uuidv4();
  const query = formatParams({
    message: params.message,
    promptType: 'SQL_COMPLETION',
    dataSourceId: params.boundInfo.dataSourceId,
    databaseName: params.boundInfo.databaseName,
    schemaName: params.boundInfo.schemaName,
    tableNames: (params.boundInfo as any).tableNames,
    ext: params.ext,
  });
  const eventSource = createSSEConnection({ url: `/api/ai/chat?${query}`, uid: sessionId });

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
      cancelSSESession(sessionId);
    },
  };
};
