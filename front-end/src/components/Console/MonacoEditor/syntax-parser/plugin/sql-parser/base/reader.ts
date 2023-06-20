/* eslint-disable no-case-declarations */
import * as _ from 'lodash';
import { IToken } from '../../..';
import {
  ICompletionItem,
  ICursorInfo,
  IGetFieldsByTableName,
  ISelectStatement,
  ISource,
  IStatement,
  IStatements,
} from './define';

export async function getCursorInfo(rootStatement: IStatements, keyPath: string[]) {
  if (!rootStatement) {
    return null;
  }

  const cursorValue: IToken = _.get(rootStatement, keyPath);
  const cursorKey = keyPath.slice().pop();
  const parentStatement = _.get(rootStatement, keyPath.slice(0, keyPath.length - 1));

  if (!parentStatement) {
    return null;
  }

  return (await judgeStatement(parentStatement, async typePlusVariant => {
    switch (typePlusVariant) {
      case 'identifier.tableName':
        return {
          type: 'tableName',
          variant: cursorKey,
          token: cursorValue,
          tableInfo: parentStatement,
        };
      case 'identifier.column':
        if (cursorKey === 'name') {
          return {
            type: 'tableField',
            token: cursorValue,
          };
        }
        return null;

      case 'identifier.columnAfterGroup':
        return {
          type: 'tableFieldAfterGroup',
          token: cursorValue,
          groupName: parentStatement.groupName.value,
        };
      case 'function':
        return {
          type: 'functionName',
          token: cursorValue,
        };
      default:
    }
  })) as ICursorInfo;
}

export function findNearestStatement(
  rootStatement: IStatements,
  keyPath: string[],
  callback?: (value?: any) => boolean,
): ISelectStatement {
  if (!rootStatement) {
    return null;
  }

  if (keyPath.length === 0) {
    return null;
  }

  const value = _.get(rootStatement, keyPath);

  if (!value) {
    throw Error('Path not found from ast!');
  }

  if (!value.token && value.type === 'statement') {
    if (callback) {
      if (callback(value) === true) {
        return value;
      }
    } else {
      return value;
    }
  }

  if (keyPath.length > 1) {
    return findNearestStatement(rootStatement, keyPath.slice(0, keyPath.length - 1), callback);
  }
  return null;
}

export async function getFieldsFromStatement(
  rootStatement: IStatements,
  cursorKeyPath: string[],
  getFieldsByTableName: IGetFieldsByTableName,
) {
  const cursorInfo = await getCursorInfo(rootStatement, cursorKeyPath);
  const cursorRootStatement = findNearestStatement(rootStatement, cursorKeyPath);

  if (!cursorRootStatement) {
    return [];
  }

  switch (cursorRootStatement.variant) {
    // Select statement
    case 'select':
      return getFieldsByFromClauses(
        cursorRootStatement,
        _.get(cursorRootStatement, 'from.sources', []),
        cursorInfo,
        getFieldsByTableName,
      );
    // Join statement
    // 字段是 source 表的（自带 + join 的表）
    case 'join':
      const parentCursorKeyPath = cursorKeyPath.slice();
      parentCursorKeyPath.pop();

      const parentSelectStatement = findNearestStatement(rootStatement, parentCursorKeyPath, eachStatement => {
        return eachStatement.variant === 'select';
      });

      return getFieldsByFromClauses(
        parentSelectStatement,
        _.get(parentSelectStatement, 'from.sources', []),
        cursorInfo,
        getFieldsByTableName,
      );
    default:
  }

  return [];
}

async function getFieldsByFromClauses(
  rootStatement: IStatement,
  fromStatements: IStatement[],
  cursorInfo: ICursorInfo,
  getFieldsByTableName: IGetFieldsByTableName,
): Promise<ICompletionItem[]> {
  const fields = await Promise.all(
    fromStatements.map(fromStatement => {
      return getFieldsByFromClause(rootStatement, fromStatement, cursorInfo, getFieldsByTableName);
    }),
  );

  return _.flatten(fields).filter(item => {
    return !!item;
  });
}

async function getFieldsByFromClause(
  rootStatement: IStatement,
  fromStatement: IStatement,
  cursorInfo: ICursorInfo,
  getFieldsByTableName: IGetFieldsByTableName,
): Promise<ICompletionItem[]> {
  return judgeStatement(fromStatement, async typePlusVariant => {
    switch (typePlusVariant) {
      case 'statement.tableSource':
        // ignore joins
        const tableSourceFields = await getFieldsByFromClause(
          rootStatement,
          (fromStatement as any).source,
          cursorInfo,
          getFieldsByTableName,
        );
        const joinsFields = _.isArray((fromStatement as any).joins)
          ? await getFieldsByFromClauses(
              rootStatement,
              _.get(fromStatement, 'joins', []),
              cursorInfo,
              getFieldsByTableName,
            )
          : [];
        return tableSourceFields.concat(joinsFields);
      case 'statement.join':
        return getFieldsByFromClause(rootStatement, (fromStatement as any).join, cursorInfo, getFieldsByTableName);
      case 'identifier.table':
        const itFromStatement = fromStatement as ISource;

        let originFields = await getFieldsByTableName(itFromStatement.name, cursorInfo.token.value, rootStatement);
        const tableNames: string[] = _.get(itFromStatement, 'name.tableNames', []);

        let groupPickerName: string = null;
        const tableNameAlias: string = _.get(itFromStatement, 'alias.value');

        // 如果有 alias,直接作为 groupPickerName
        if (tableNameAlias) {
          groupPickerName = tableNameAlias;
        } else {
          // 实现的 tableNames 数量
          let existKeyCount = 0;
          tableNames.forEach(tableName => {
            const eachTableName = _.get(itFromStatement, `name.${tableName}.value`);
            if (eachTableName) {
              // eslint-disable-next-line no-plusplus
              existKeyCount++;
              groupPickerName = eachTableName;
            }
          });

          // 如果 existKeyCount 大于 1，则不提供 groupPickerName
          if (existKeyCount > 1) {
            groupPickerName = null;
          }
        }

        originFields = originFields.map(originField => {
          return {
            ...originField,
            tableInfo: itFromStatement.name,
            // 如果仅有一个 tableNames 有值，就用那个作为 groupPickerName，否则没有
            groupPickerName,
            // existKeyCount
            //     ? null
            //     : _.get(itFromStatement, 'alias.value') || _.get(itFromStatement, 'name.tableName.value') || null,
            originFieldName: originField.label,
          };
        });
        return originFields;
      case 'statement.select':
        const ssFromStatement = fromStatement as ISelectStatement;

        let statementSelectFields: ICompletionItem[] = [];

        const fields = await getFieldsByFromClauses(
          ssFromStatement,
          ssFromStatement.from.sources,
          cursorInfo,
          getFieldsByTableName,
        );

        // If select *, return all fields
        if (ssFromStatement.result.length === 1 && ssFromStatement.result[0].name.value === '*') {
          statementSelectFields = fields.slice();
        } else {
          statementSelectFields = fields
            .map(field => {
              const selectedField = ssFromStatement.result.find(result => {
                if (_.get(result.name, 'token') === true) {
                  return result.name.value === field.label;
                }

                // Consider ${group}.${field}
                if (
                  _.get(result.name, 'type') === 'identifier' &&
                  _.get(result.name, 'variant') === 'columnAfterGroup'
                ) {
                  return _.get(result.name, 'name.value') === field.label;
                }

                // Consider ${group}.*
                if (_.get(result.name, 'type') === 'identifier' && _.get(result.name, 'variant') === 'groupAll') {
                  return _.get(result.name, 'groupName.value') === field.groupPickerName;
                }

                return false;
              });
              if (!selectedField) {
                return null;
              }

              if (selectedField.alias) {
                return {
                  ...field,
                  label: selectedField.alias.value,
                };
              }
              return field;
            })
            .filter(field => {
              return field !== null;
            })
            .slice();
        }

        // If has alias, change
        if (_.has(ssFromStatement, 'alias.value')) {
          statementSelectFields = statementSelectFields.map(statementSelectField => {
            return {
              ...statementSelectField,
              groupPickerName: _.get(ssFromStatement, 'alias.value'),
            };
          });
        }

        return statementSelectFields;
      default:
        return null;
    }
  });
}

async function judgeStatement<T>(
  statement: IStatement,
  callback: (typePlusVariant?: string) => Promise<T>,
): Promise<T> {
  if (!statement) {
    return null;
  }

  if (statement.variant) {
    return callback(`${statement.type}.${statement.variant}`);
  }
  return callback(statement.type);
}

export async function findFieldExtraInfo(
  rootStatement: IStatements,
  cursorInfo: ICursorInfo,
  getFieldsByTableName: IGetFieldsByTableName,
  fieldKeyPath: string[],
): Promise<ICompletionItem> {
  const fields = await getFieldsFromStatement(rootStatement, fieldKeyPath, getFieldsByTableName);
  const field = fields.find(eachField => {
    return eachField.label === cursorInfo.token.value;
  });

  if (!field) {
    return null;
  }

  return field;
}
