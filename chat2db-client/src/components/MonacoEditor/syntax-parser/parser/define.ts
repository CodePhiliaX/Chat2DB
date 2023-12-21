import { IToken } from '../lexer/token';
import { IMatch } from './match';
import { Scanner } from './scanner';

// tslint:disable:max-classes-per-file

export interface IParseResult {
  success: boolean;
  ast: IAst;
  cursorKeyPath: string[];
  nextMatchings: IMatching[];
  error: {
    token: IToken;
    reason: 'wrong' | 'incomplete';
    suggestions: IMatching[];
  };
  debugInfo: {
    tokens: IToken[];
    callVisiterCount: number;
    costs: {
      lexer: number;
      parser: number;
    };
  };
}

export type FirstOrFunctionSet = MatchNode | ChainFunction;

export type IMatchFn = (scanner: Scanner, isCostToken: boolean) => IMatch;

// IToken | Array<IToken> | any return object from resolveAst().
export type IAst = IToken | any;

export type Node = MatchNode | FunctionNode | TreeNode | ChainNode;

export type ParentNode = TreeNode | ChainNode;

export interface IMatching {
  // loose not cost token, and result is fixed true of false.
  type: 'string' | 'loose' | 'special';
  value: string | boolean;
}

export type SingleElement = string | any;

export type IElement = SingleElement | SingleElement[];

export type IElements = IElement[];

export type ISolveAst = (astResult: IAst[]) => IAst;

export type Chain = (...elements: IElements) => (solveAst?: ISolveAst) => ChainNodeFactory;

export type ChainNodeFactory = (
  parentNode?: ParentNode,
  // If parent node is a function, here will get it's name.
  creatorFunction?: ChainFunction,
  parentIndex?: number,
  parser?: Parser,
) => ChainNode;

export type ChainFunction = () => ChainNodeFactory;

export interface IChance {
  node: ParentNode;
  childIndex: number;
  tokenIndex: number;
}

// ////////////////////////////////////// Const or Variables

export const parserMap = new Map<ChainFunction, Parser>();
export const MAX_VISITER_CALL = 1000000;

export class Parser {
  public rootChainNode: ChainNode = null;

  public firstSet = new Map<ChainFunction, MatchNode[]>();

  public firstOrFunctionSet = new Map<ChainFunction, FirstOrFunctionSet[]>();

  public relatedSet = new Map<ChainFunction, Set<ChainFunction>>();
}

export class VisiterStore {
  public restChances: IChance[] = [];

  public stop = false;

  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(public scanner: Scanner, public parser: Parser) {
    //
  }
}

export class VisiterOption {
  public onCallVisiter?: (node?: Node, store?: VisiterStore) => void;

  public onVisiterNextNode?: (node?: Node, store?: VisiterStore) => void;

  public onSuccess?: () => void;

  public onFail?: (lastNode?: Node) => void;

  public onMatchNode: (matchNode: MatchNode, store: VisiterStore, visiterOption: VisiterOption) => void;

  public generateAst?: boolean = true;

  public enableFirstSet?: boolean = true;
}

export class ChainNode {
  public parentNode: ParentNode;

  public childs: Node[] = [];

  public astResults?: IAst[] = [];

  // Eg: const foo = chain => chain()(), so the chain creatorFunction is 'foo'.
  public creatorFunction: ChainFunction = null;

  // Only user function can have functionName.
  public functionName: string;

  public solveAst: ISolveAst = null;

  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(public parentIndex: number) {
    //
  }
}

export class TreeNode {
  public parentNode: ParentNode;

  public childs: Node[] = [];

  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(public parentIndex: number) {
    //
  }
}

export class FunctionNode {
  public parentNode: ParentNode;

  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(public chainFunction: ChainFunction, public parentIndex: number, public parser: Parser) {
    //
  }

  public run = () => {
    return this.chainFunction()(this.parentNode, this.chainFunction, this.parentIndex, this.parser);
  };
}

export class MatchNode {
  public parentNode: ParentNode;

  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(private matchFunction: IMatchFn, public matching: IMatching, public parentIndex: number) {
    //
  }

  public run = (scanner: Scanner, isCostToken = true) => {
    return this.matchFunction(scanner, isCostToken);
  };
}

export class CreateParserOptions {
  public cursorTokenExcludes?: (token?: IToken) => boolean = () => {
    return false;
  };
}
