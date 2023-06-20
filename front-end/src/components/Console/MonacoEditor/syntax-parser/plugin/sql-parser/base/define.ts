import { IToken } from '../../..';

export type IStatements = IStatement[];

export interface IStatement {
  type: 'statement' | 'identifier';
  variant: string;
}

export interface ISelectStatement extends IStatement {
  from: IFrom;
  result: IResult[];
}

export interface IResult extends IStatement {
  name: IToken;
  alias: IToken;
}

export interface IFrom extends IStatement {
  sources: ISource[];
  where?: any;
  group?: any;
  having?: any;
}

export interface ISource extends IStatement {
  name: ITableInfo & IStatement;
  alias: IToken;
}

export interface ITableInfo {
  tableName: IToken;
  namespace: IToken;
}

export interface ICompletionItem {
  label: string;
  kind?: string;
  sortText?: string;
  tableInfo?: ITableInfo;
  groupPickerName?: string;
  originFieldName?: string;
  detail?: string;
  documentation?: string;
}

export type CursorType =
  | 'tableField'
  | 'tableName'
  | 'namespace'
  | 'namespaceOne'
  | 'functionName'
  | 'tableFieldAfterGroup';

export type ICursorInfo<T = {}> = {
  token: IToken;
  type: CursorType;
} & T;

export type IGetFieldsByTableName = (
  tableName: ITableInfo,
  inputValue: string,
  rootStatement: IStatement,
) => Promise<ICompletionItem[]>;
