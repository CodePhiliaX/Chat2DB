/* eslint-disable no-restricted-globals */
/* eslint-disable no-case-declarations */
/* eslint-disable no-use-before-define */
/* eslint-disable no-plusplus */
/* eslint-disable no-param-reassign */
import * as _ from 'lodash';
import { IParseResult } from '../..';
import { DefaultOpts, IMonacoVersion, IParserType } from './default-opts';
import { createParserWorker } from './worker-factory';
import { mysqlParser } from '../sql-parser';
import {
  ICompletionItem,
  ITableInfo,
  IJoinTableInfo,
  reader,
  ICursorInfo,
} from '../sql-parser';

const supportedMonacoEditorVersion = ['0.13.2', '0.15.6'];

export function monacoSqlAutocomplete(
  monaco: any,
  editor: any,
  opts?: Partial<DefaultOpts>,
) {
  opts = _.defaults(opts || {}, new DefaultOpts(monaco));

  if (supportedMonacoEditorVersion.indexOf(opts.monacoEditorVersion) === -1) {
    throw Error(
      `monaco-editor version ${
        opts.monacoEditorVersion
      } is not allowed, only support ${supportedMonacoEditorVersion.join(' ')}`,
    );
  }

  // Register completion provider
  // Get parser info and show error.
  let currentParserPromise: any = null;
  let editVersion = 0;
  
  // Initialize parser promise with current editor content
  const initParserPromise = () => {
    const model = editor.getModel();
    if (model) {
      currentParserPromise = asyncParser(
        editor.getValue(),
        model.getOffsetAt(editor.getPosition()),
        opts.parserType,
      );
    }
  };
  
  // Initialize on first load
  initParserPromise();
  
  const completionProvider = monaco.languages.registerCompletionItemProvider(opts.language, {
    triggerCharacters:
      ' $.:{}=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.split(''),
    provideCompletionItems: async () => {
      const currentEditVersion = editVersion;
      
      // If currentParserPromise is null, initialize it
      if (!currentParserPromise) {
        initParserPromise();
      }
      
      const parseResult: IParseResult = await currentParserPromise;

      if (!parseResult) {
        return returnCompletionItemsByVersion([], opts.monacoEditorVersion);
      }

      if (currentEditVersion !== editVersion) {
        return returnCompletionItemsByVersion([], opts.monacoEditorVersion);
      }

      const cursorInfo = await reader.getCursorInfo(
        parseResult.ast,
        parseResult.cursorKeyPath,
      );

      const parserSuggestion = opts.pipeKeywords(parseResult.nextMatchings);

      // 当 cursorInfo 为 null 但解析失败时，尝试从 SELECT 语句获取字段
      if (!cursorInfo && parseResult.ast && parseResult.error) {
        const fallbackFields = await reader.getFieldsFromStatement(
          parseResult.ast,
          [],  // 空的 cursorKeyPath
          opts.onSuggestTableFields,
        );
        
        if (fallbackFields && fallbackFields.length > 0) {
          const uniqueFallbackFields = _.uniqBy(fallbackFields, 'label');
          const functionNames = await opts.onSuggestFunctionName('');
          const result = uniqueFallbackFields.concat(functionNames).concat(parserSuggestion);
          return returnCompletionItemsByVersion(result, opts.monacoEditorVersion);
        }
        
        return returnCompletionItemsByVersion(
          parserSuggestion,
          opts.monacoEditorVersion,
        );
      }

      if (!cursorInfo) {
        return returnCompletionItemsByVersion(
          parserSuggestion,
          opts.monacoEditorVersion,
        );
      }

      switch (cursorInfo.type) {
        case 'tableField':
          const cursorRootStatementFields = await reader.getFieldsFromStatement(
            parseResult.ast,
            parseResult.cursorKeyPath,
            opts.onSuggestTableFields,
          );

          // 去重字段（避免重复）
          const uniqueFields = _.uniqBy(cursorRootStatementFields, 'label');

          // group.fieldName
          const groups = _.groupBy(
            uniqueFields.filter((cursorRootStatementField) => {
              return cursorRootStatementField.groupPickerName !== null;
            }),
            'groupPickerName',
          );

          const functionNames = await opts.onSuggestFunctionName(
            cursorInfo.token.value,
          );

          const result = uniqueFields
            .concat(functionNames)  // 函数名
            .concat(parserSuggestion)  // SQL 关键字
            .concat(
              groups
                ? Object.keys(groups).map((groupName) => {
                    return opts.onSuggestFieldGroup(groupName);
                  })
                : [],
            );

          return returnCompletionItemsByVersion(
            result,
            opts.monacoEditorVersion,
          );
          
        case 'tableFieldAfterGroup':
          // 字段 . 后面的部分
          const cursorRootStatementFieldsAfter =
            await reader.getFieldsFromStatement(
              parseResult.ast,
              parseResult.cursorKeyPath as any,
              opts.onSuggestTableFields,
            );

          // 去重并过滤
          const uniqueFieldsAfter = _.uniqBy(cursorRootStatementFieldsAfter, 'label');
          const filteredFields = uniqueFieldsAfter
            .filter((cursorRootStatementField: any) => {
              return (
                cursorRootStatementField.groupPickerName ===
                (cursorInfo as ICursorInfo<{ groupName: string }>).groupName
              );
            })
            .filter((field: any) => {
              // 过滤掉无效的补全项
              return field && field.label && field.label.trim() !== '' && field.insertText && field.insertText.trim() !== '';
            });

          // 字段排在最前面，关键字排在后面
          const sortedFields = [
            ...filteredFields,  // 字段（sortText: B*）
            ...parserSuggestion.filter(item => item.insertText && item.insertText.trim() !== ''),  // SQL 关键字（sortText: W*）
          ];

          return returnCompletionItemsByVersion(
            sortedFields,
            opts.monacoEditorVersion,
          );
          
        case 'joinTable':
          const joinTableNames = await opts.onSuggestJoinTables(
            cursorInfo as ICursorInfo<IJoinTableInfo>,
          );

          return returnCompletionItemsByVersion(
            joinTableNames.concat(parserSuggestion),
            opts.monacoEditorVersion,
          );

        case 'tableName':
          const tableNames = await opts.onSuggestTableNames(
            cursorInfo as ICursorInfo<ITableInfo>,
          );

          return returnCompletionItemsByVersion(
            tableNames.concat(parserSuggestion),
            opts.monacoEditorVersion,
          );
        case 'functionName':
          return opts.onSuggestFunctionName(cursorInfo.token.value);
        default:
          return returnCompletionItemsByVersion(
            parserSuggestion,
            opts.monacoEditorVersion,
          );
}
    },
  });
  
  // Listen for content changes and update parser promise
  editor.onDidChangeModelContent((event: any) => {
    editVersion++;
    const currentEditVersion = editVersion;

    currentParserPromise = new Promise((resolve) => {
      setTimeout(() => {
        const model = editor.getModel();

        asyncParser(
          editor.getValue(),
          model.getOffsetAt(editor.getPosition()),
          opts.parserType,
        ).then((parseResult) => {
          resolve(parseResult);

          if (currentEditVersion !== editVersion) {
            return;
          }

          opts.onParse(parseResult);

          if (parseResult.error) {
            // Check if input only contains comments and whitespace
            // Handle both \n and \r\n line endings, and use [\s\S] to match all characters including \r
            const textWithoutComments = editor.getValue().replace(/((?:#|--)[\s\S]*?(?:\r?\n|$)|\/\*[\s\S]*?(?:\*\/|$))/g, '').trim();
            
            // Only show error if there's actual SQL content (not just comments)
            if (textWithoutComments.length > 0) {
              const newReason =
                parseResult.error.reason === 'incomplete'
                  ? `Incomplete, expect next input: \n${parseResult.error.suggestions
                      .map((each: any) => {
                        return each.value;
                      })
                      .join('\n')}`
                  : `Wrong input, expect: \n${parseResult.error.suggestions
                      .map((each: any) => {
                        return each.value;
                      })
                      .join('\n')}`;

              const errorPosition = parseResult.error.token
                ? {
                    startLineNumber: model.getPositionAt(
                      parseResult.error.token.position[0],
                    ).lineNumber,
                    startColumn: model.getPositionAt(
                      parseResult.error.token.position[0],
                    ).column,
                    endLineNumber: model.getPositionAt(
                      parseResult.error.token.position[1],
                    ).lineNumber,
                    endColumn:
                      model.getPositionAt(parseResult.error.token.position[1])
                        .column + 1,
                  }
                : {
                    startLineNumber: 0,
                    startColumn: 0,
                    endLineNumber: 0,
                    endColumn: 0,
                  };

              model.getPositionAt(parseResult.error.token);

              monaco.editor.setModelMarkers(model, opts.language, [
                {
                  ...errorPosition,
                  message: newReason,
                  severity: getSeverityByVersion(
                    monaco,
                    opts.monacoEditorVersion,
                  ),
                },
              ]);
            } else {
              // Clear error markers for comment-only input
              monaco.editor.setModelMarkers(editor.getModel(), opts.language, []);
            }
          } else {
            monaco.editor.setModelMarkers(editor.getModel(), opts.language, []);
          }
        });
      });
    });
  });
  
  // Register hover provider and store it for disposal
  const hoverProviderDisposable = monaco.languages.registerHoverProvider(opts.language, {
    provideHover: async (model: any, position: any) => {
      const parseResult: IParseResult = await asyncParser(
        editor.getValue(),
        model.getOffsetAt(position),
        opts.parserType,
      );

      const cursorInfo = await reader.getCursorInfo(
        parseResult.ast,
        parseResult.cursorKeyPath,
      );

      if (!cursorInfo) {
        return null as any;
      }

      let contents: any = [];

      switch (cursorInfo.type) {
        case 'tableField':
          const extra = await reader.findFieldExtraInfo(
            parseResult.ast,
            cursorInfo,
            opts.onSuggestTableFields,
            parseResult.cursorKeyPath,
          );
          contents = await opts.onHoverTableField(
            cursorInfo.token.value,
            extra,
          );
          break;
        case 'tableFieldAfterGroup':
          const extraAfter = await reader.findFieldExtraInfo(
            parseResult.ast,
            cursorInfo,
            opts.onSuggestTableFields,
            parseResult.cursorKeyPath,
          );
          contents = await opts.onHoverTableField(
            cursorInfo.token.value,
            extraAfter,
          );
          break;
        case 'tableName':
          contents = await opts.onHoverTableName(cursorInfo as ICursorInfo);
          break;
        case 'functionName':
          contents = await opts.onHoverFunctionName(cursorInfo.token.value);
          break;
        default:
      }

      return {
        range: monaco.Range.fromPositions(
          model.getPositionAt(cursorInfo.token.position[0]),
          model.getPositionAt(cursorInfo.token.position[1] + 1),
        ),
        contents,
      };
    },
  });

  // Return disposable object
  return {
    dispose: () => {
      completionProvider.dispose();
      hoverProviderDisposable.dispose();
    },
  };
}

// 实例化一个 worker
// 注意：开发环境下使用同步解析避免 HMR 问题
// let worker: Worker | null = null;
// try {
//   worker = createParserWorker();
// } catch (error) {
//   console.warn('Failed to create worker, will use fallback:', error);
// }
const worker: Worker | null = null; // 暂时禁用 worker

const parserIndex = 0;

// 防抖：记录上一次补全时间，避免频繁触发
const lastCompletionTime = 0;
const COMPLETION_DEBOUNCE = 200; // 200ms 防抖

const asyncParser = async (
  text: string,
  index: number,
  parserType: IParserType,
): Promise<IParseResult> => {
  // 开发环境直接使用同步解析
  try {
    const result = mysqlParser(text, index);
    return Promise.resolve(result);
  } catch (error) {
    console.error('[Parser] 解析错误:', error);
    return Promise.reject(error);
  }
};

// const asyncParser = async (
//   text: string,
//   index: number,
//   parserType: IParserType,
// ): Promise<IParseResult> => {
//   // 如果 worker 可用，使用异步解析
//   if (worker) {
//     parserIndex++;
//     const currentParserIndex = parserIndex;
//
//     return new Promise((resolve, reject) => {
//       worker!.postMessage({ text, index, parserType });
//
//       worker!.onmessage = (event) => {
//         if (currentParserIndex === parserIndex) {
//           resolve(event.data);
//         } else {
//           reject();
//         }
//       };
//     });
//   } else {
//     // 回退方案：同步解析
//     try {
//       const result = mysqlParser(text, index);
//       return Promise.resolve(result);
//     } catch (error) {
//       console.error('Parser error:', error);
//       return Promise.reject(error);
//     }
//   }
// };

function returnCompletionItemsByVersion(
  value: ICompletionItem[],
  monacoVersion: IMonacoVersion,
) {
  switch (monacoVersion) {
    case '0.13.2':
      return value;
    case '0.15.6':
      return {
        suggestions: value,
      };
    default:
      throw Error('Not supported version');
  }
}

function getSeverityByVersion(monaco: any, monacoVersion: IMonacoVersion) {
  switch (monacoVersion) {
    case '0.13.2':
      return monaco.Severity.Error;
    case '0.15.6':
      return monaco.MarkerSeverity.Error;
    default:
      throw Error('Not supported version');
  }
}
