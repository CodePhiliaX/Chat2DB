import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import { monacoSqlAutocomplete } from './index';
import sqlService, { IForeignKeyVO } from '@/service/sql';
import { IBoundInfo } from '@/typings/workspace';
import { ICompletionItem, ITableInfo, IJoinTableInfo, ICursorInfo } from '../sql-parser/base/define';
import { getDialectCompletion } from './dialects';

export interface ISqlAutocompleteOptions {
  monaco: typeof monaco;
  editor: monaco.editor.IStandaloneCodeEditor;
  boundInfo: IBoundInfo;
  parserType?: 'mysql' | 'odps' | 'blink' | 'dsql' | 'grail' | 'emcsql';
}

export interface ISqlAutocompleteDisposable {
  dispose: () => void;
}

// 字段缓存
const fieldCache = new Map<string, any[]>();

const mapDatabaseTypeToParser = (databaseType: string): ISqlAutocompleteOptions['parserType'] => {
  const typeMap: Record<string, ISqlAutocompleteOptions['parserType']> = {
    MYSQL: 'mysql',
    POSTGRESQL: 'mysql',
    ORACLE: 'mysql',
    SQLSERVER: 'mysql',
    H2: 'mysql',
    MARIADB: 'mysql',
    TIDB: 'mysql',
    DAMENG: 'mysql',
    KINGBASE: 'mysql',
    VERTICAL: 'mysql',
    SQLITE: 'mysql',
    PRESTO: 'mysql',
    TRINO: 'mysql',
    CLICKHOUSE: 'mysql',
    DB2: 'mysql',
    SYBASE: 'mysql',
    INFLUXDB: 'mysql',
    MONGODB: 'mysql',
    REDIS: 'mysql',
    ELASTICSEARCH: 'mysql',
    HIVE: 'mysql',
    SPARK: 'mysql',
    IMPALA: 'mysql',
    KYLESE: 'mysql',
    OCEANBASE: 'mysql',
    GAUSS: 'mysql',
    TDENGINE: 'mysql',
    TABLESTORE: 'mysql',
  };
  return typeMap[databaseType?.toUpperCase()] || 'mysql';
};

const cleanIdentifier = (name: string): string => {
  if (!name) return '';
  return name.replace(/^[`'"[\]]+|[`'"[\]]+$/g, '');
};

const matchesInputValue = (word: string, inputValue?: string) => {
  const trimmedInputValue = inputValue?.trim();
  if (!trimmedInputValue) {
    return true;
  }

  return word.toLowerCase().startsWith(trimmedInputValue.toLowerCase());
};

const noParenthesesFunctionNames = new Set([
  'CURRENT_DATE',
  'CURRENT_ROLE',
  'CURRENT_TIME',
  'CURRENT_TIMESTAMP',
  'CURRENT_USER',
  'LOCALTIME',
  'LOCALTIMESTAMP',
  'SESSION_USER',
  'SYSDATE',
  'SYSTEM_USER',
]);

const shouldAppendFunctionParentheses = (functionName: string) => {
  const normalizedFunctionName = functionName.toUpperCase();
  return /^[A-Z_][A-Z0-9_]*$/.test(normalizedFunctionName) && !noParenthesesFunctionNames.has(normalizedFunctionName);
};

const getFunctionInsertText = (functionName: string) => {
  if (!shouldAppendFunctionParentheses(functionName)) {
    return functionName;
  }

  return `${functionName}($0)`;
};

export const initSqlAutocomplete = (options: ISqlAutocompleteOptions): ISqlAutocompleteDisposable => {
  const { monaco, editor, boundInfo, parserType } = options;

  const effectiveParserType = parserType || mapDatabaseTypeToParser(boundInfo.databaseType || 'MYSQL');

  // Register completion provider and store the disposable
  const completionProviderDisposable = monacoSqlAutocomplete(monaco, editor, {
    language: 'sql',
    parserType: effectiveParserType,

    onSuggestTableNames: async (cursorInfo?: ICursorInfo<ITableInfo>) => {
      try {
        const data = await sqlService.getAllTableList({
          dataSourceId: boundInfo.dataSourceId,
          databaseName: boundInfo.databaseName,
          schemaName: boundInfo.schemaName,
        });

        const parentName = boundInfo.schemaName || boundInfo.databaseName || '';

        return data.map((table) => {
          const name = table.name;
          const label = parentName ? `${name} (${parentName})` : name;
          return {
            label,
            insertText: name,
            sortText: `Z${name}`,
            kind: monaco.languages.CompletionItemKind.Struct as any,
            detail: `(表) ${table.comment || ''}`,
            documentation: table.comment || `表: ${name}`,
          };
        });
      } catch (error) {
        console.error('[SQL 补全 - API] 获取表名失败:', error);
        return [];
      }
    },

    onSuggestJoinTables: async (cursorInfo?: ICursorInfo<IJoinTableInfo>) => {
      try {
        const joinInfo = cursorInfo?.joinTableInfo;
        if (!joinInfo || !joinInfo.currentTable) {
          console.warn('[SQL 补全 - JOIN] 无当前表信息，返回所有表');
          return await sqlService
            .getAllTableList({
              dataSourceId: boundInfo.dataSourceId,
              databaseName: boundInfo.databaseName,
              schemaName: boundInfo.schemaName,
            })
            .then((data) => {
              const parentName = boundInfo.schemaName || boundInfo.databaseName || '';
              return data.map((table) => {
                const name = table.name;
                const alias = name
                  .split(/[_\s]+/)
                  .map((word) => word.charAt(0).toLowerCase())
                  .join('');
                const label = parentName ? `${name} (${parentName})` : name;
                return {
                  label,
                  insertText: `${name} ${alias}`,
                  sortText: `Z${name}`,
                  kind: monaco.languages.CompletionItemKind.Struct as any,
                  detail: `(表) ${table.comment || ''}`,
                  documentation: table.comment || `表: ${name}`,
                };
              });
            });
        }

        const currentTableName = joinInfo.currentTable.tableName?.value;
        const currentTableAlias =
          joinInfo.currentTableAlias ||
          joinInfo.currentTable.tableName?.value
            .split(/[_\s]+/)
            .map((word) => word.charAt(0).toLowerCase())
            .join('');
        if (!currentTableName) {
          console.warn('[SQL 补全 - JOIN] 当前表名为空，返回所有表');
          return [];
        }

        console.log('[SQL 补全 - JOIN] 当前表:', currentTableName);
        console.log('[SQL 补全 - JOIN] 已关联表数量:', joinInfo.joinedTables?.length || 0);

        // 获取当前表的外键关系
        const foreignKeys = await sqlService.getForeignKeyList({
          dataSourceId: boundInfo.dataSourceId,
          databaseName: boundInfo.databaseName || '',
          schemaName: boundInfo.schemaName,
          tableName: currentTableName,
        });

        console.log('[SQL 补全 - JOIN] 外键数量:', foreignKeys.length);

        // 过滤掉已经在 JOIN 中使用的表
        const joinedTableNames = new Set((joinInfo.joinedTables || []).map((t) => t.tableName?.value).filter(Boolean));

        // 获取所有表用于填充未找到外键时的默认列表
        const allTables = await sqlService.getAllTableList({
          dataSourceId: boundInfo.dataSourceId,
          databaseName: boundInfo.databaseName,
          schemaName: boundInfo.schemaName,
        });

        const parentName = boundInfo.schemaName || boundInfo.databaseName || '';

        // 生成表别名：取首字母，如果冲突则加数字
        const generateAlias = (tableName: string, usedAliases: Set<string>): string => {
          let alias = tableName
            .split(/[_\s]+/)
            .map((word) => word.charAt(0).toLowerCase())
            .join('');
          if (usedAliases.has(alias)) {
            let i = 1;
            while (usedAliases.has(`${alias}${i}`)) {
              i++;
            }
            alias = `${alias}${i}`;
          }
          usedAliases.add(alias);
          return alias;
        };

        // 收集已使用的别名
        const usedAliases = new Set<string>();
        usedAliases.add(currentTableAlias);

        // 如果有外键，优先返回通过外键关联的表（带完整 ON 条件）
        if (foreignKeys.length > 0) {
          const relatedTableItems: any[] = [];
          const processedTables = new Set<string>();

          // 收集所有关联的表（通过外键引用的表）
          for (const fk of foreignKeys as IForeignKeyVO[]) {
            const refTable = fk.referencedTable;
            if (!refTable || joinedTableNames.has(refTable) || processedTables.has(refTable)) {
              continue;
            }

            processedTables.add(refTable);
            const alias = generateAlias(refTable, usedAliases);

            // 使用 snippet 格式：表名 别名 ON 当前表.外键列 = 关联表.主键列
            const snippetText =
              `${refTable} ${alias} ON ` +
              `${currentTableAlias}.${fk.columnName} = ${alias}.${fk.referencedColumnName}`;

            const label = parentName ? `${refTable} (${parentName})` : refTable;
            relatedTableItems.push({
              label,
              insertText: snippetText,
              insertTextRules: 4, // Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
              sortText: `A${refTable}`, // A 开头优先显示
              kind: monaco.languages.CompletionItemKind.Struct as any,
              detail: `(关联) ${fk.columnName} → ${fk.referencedColumnName}`,
              documentation: fk.comment || `通过 ${fk.columnName} 关联到 ${refTable}.${fk.referencedColumnName}`,
            });
          }

          console.log('[SQL 补全 - JOIN] 关联表数量:', relatedTableItems.length);

          // 然后返回其他未关联的表（Z 开头，排在后面）
          const unrelatedTableItems = allTables
            .filter((t) => !processedTables.has(t.name) && !joinedTableNames.has(t.name))
            .map((table) => {
              const name = table.name;
              const alias = generateAlias(name, usedAliases);
              const label = parentName ? `${name} (${parentName})` : name;
              return {
                label,
                insertText: `${name} ${alias}`,
                sortText: `Z${name}`,
                kind: monaco.languages.CompletionItemKind.Struct as any,
                detail: `(表) ${table.comment || ''}`,
                documentation: table.comment || `表: ${name}`,
              };
            });

          return [...relatedTableItems, ...unrelatedTableItems];
        }

        // 如果没有外键，返回所有表（排除已 JOIN 的）
        console.log('[SQL 补全 - JOIN] 无外键，返回所有表');
        return allTables
          .filter((t) => !joinedTableNames.has(t.name))
          .map((table) => {
            const name = table.name;
            const alias = generateAlias(name, usedAliases);
            const label = parentName ? `${name} (${parentName})` : name;
            return {
              label,
              insertText: `${name} ${alias}`,
              sortText: `Z${name}`,
              kind: monaco.languages.CompletionItemKind.Struct as any,
              detail: `(表) ${table.comment || ''}`,
              documentation: table.comment || `表: ${name}`,
            };
          });
      } catch (error) {
        console.error('[SQL 补全 - JOIN] 获取 JOIN 表失败:', error);
        return [];
      }
    },

    onSuggestTableFields: async (tableInfo?: ITableInfo, cursorValue?: string, rootStatement?: any) => {
      const rawTableName = tableInfo?.tableName?.value;
      const tableName = cleanIdentifier(rawTableName);

      const cacheKey = `${boundInfo.dataSourceId}_${boundInfo.databaseName || ''}_${
        boundInfo.schemaName || ''
      }_${tableName}`;
      if (fieldCache.has(cacheKey)) {
        const cachedData = fieldCache.get(cacheKey);
        return cachedData.map((column) => {
          const name = column.name;
          const label = tableName ? `${name} (${tableName})` : name;
          const dataType = column.columnType || column.dataType || '';
          return {
            label,
            insertText: name,
            sortText: `A${name}`,
            kind: monaco.languages.CompletionItemKind.Field as any,
            detail: `(字段) ${dataType}`,
            documentation: column.comment || `字段: ${name}, 类型: ${dataType}`,
          };
        });
      }

      try {
        if (!tableName) {
          console.warn('[SQL 补全 - API] 表名为空，返回空数组');
          return [];
        }

        const data = await sqlService.getColumnList({
          dataSourceId: boundInfo.dataSourceId,
          databaseName: boundInfo.databaseName || '',
          schemaName: boundInfo.schemaName,
          tableName,
        });

        fieldCache.set(cacheKey, data);

        return data.map((column) => {
          const name = column.name;
          const label = tableName ? `${name} (${tableName})` : name;
          const dataType = column.columnType || column.dataType || '';
          return {
            label,
            insertText: name,
            sortText: `A${name}`,
            kind: monaco.languages.CompletionItemKind.Field as any,
            detail: `(字段) ${dataType}`,
            documentation: column.comment || `字段: ${name}, 类型: ${dataType}`,
          };
        });
      } catch (error) {
        console.error('[SQL 补全 - API] 获取表字段失败:', error);
        return [];
      }
    },

    onSuggestFunctionName: async (inputValue?: string) => {
      const dialectCompletion = getDialectCompletion(boundInfo.databaseType);

      const keywordItems = dialectCompletion.keywords
        .filter((kw) => matchesInputValue(kw, inputValue))
        .map((kw) => ({
          label: kw,
          insertText: kw,
          sortText: `C${kw}`,
          kind: monaco.languages.CompletionItemKind.Keyword as any,
          detail: '(关键字)',
        }));

      const functionItems = dialectCompletion.functions
        .filter((func) => matchesInputValue(func, inputValue))
        .map((func) => ({
          label: func,
          insertText: getFunctionInsertText(func),
          insertTextRules: shouldAppendFunctionParentheses(func)
            ? monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet
            : undefined,
          sortText: `D${func}`,
          kind: monaco.languages.CompletionItemKind.Function as any,
          detail: '(函数)',
        }));

      const dataTypeItems = dialectCompletion.dataTypes
        .filter((dataType) => matchesInputValue(dataType, inputValue))
        .map((dataType) => ({
          label: dataType,
          insertText: dataType,
          sortText: `E${dataType}`,
          kind: monaco.languages.CompletionItemKind.TypeParameter as any,
          detail: '(数据类型)',
        }));

      return [...keywordItems, ...functionItems, ...dataTypeItems];
    },

    onSuggestFieldGroup: (tableNameOrAlias?: string) => {
      const label = tableNameOrAlias || '';
      return {
        label,
        insertText: label,
        sortText: `E${label}`,
        kind: monaco.languages.CompletionItemKind.Class as any,
        detail: '(表别名)',
      };
    },

    onHoverTableField: async (fieldName?: string, extra?: ICompletionItem) => {
      const docs: { value: string }[] = [];
      if (fieldName) {
        docs.push({ value: `**Field:** ${fieldName}` });
      }
      if (extra?.detail) {
        docs.push({ value: `**Type:** ${extra.detail}` });
      }
      if (extra?.documentation) {
        docs.push({ value: `**Comment:** ${extra.documentation}` });
      }
      return docs;
    },

    onHoverTableName: async (cursorInfo?: ICursorInfo) => {
      return [{ value: `**Table:** ${cursorInfo?.token?.value || ''}` }];
    },

    onHoverFunctionName: async (functionName?: string) => {
      return [{ value: `**Function:** ${functionName || ''}` }];
    },
  });

  return {
    dispose: () => {
      // Dispose the completion provider to avoid duplicate registrations
      if (completionProviderDisposable) {
        completionProviderDisposable.dispose();
      }
      // 清理缓存（可选，根据 boundInfo 清理或不清理）
      // fieldCache.clear();
    },
  };
};

// 缓存清理函数
export const clearFieldCache = () => {
  fieldCache.clear();
  console.log('[SQL 补全 - API] 缓存已清理');
};

export const disposeSqlAutocomplete = (disposable?: ISqlAutocompleteDisposable) => {
  if (disposable) {
    disposable.dispose();
  }
};
