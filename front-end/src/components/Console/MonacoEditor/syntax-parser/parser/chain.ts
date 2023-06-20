/* eslint-disable no-use-before-define */
import { defaults, uniq, uniqBy } from 'lodash';
import { Lexer } from '../lexer';
import { IToken } from '../lexer/token';
import {
  Chain,
  ChainFunction,
  ChainNode,
  ChainNodeFactory,
  CreateParserOptions,
  FirstOrFunctionSet,
  FunctionNode,
  IElement,
  IParseResult,
  MatchNode,
  MAX_VISITER_CALL,
  Node,
  ParentNode,
  Parser,
  parserMap,
  TreeNode,
  VisiterOption,
  VisiterStore,
} from './define';
import { match, matchFalse, matchTrue } from './match';
import { Scanner } from './scanner';
import { compareIgnoreLowerCaseWhenString, getPathByCursorIndexFromAst, tailCallOptimize } from './utils';

const createNodeByElement = (element: IElement, parentNode: ParentNode, parentIndex: number, parser: Parser): Node => {
  if (element instanceof Array) {
    const treeNode = new TreeNode(parentIndex);
    treeNode.parentNode = parentNode;
    treeNode.childs = element.map((eachElement, childIndex) => {
      return createNodeByElement(eachElement, treeNode, childIndex, parser);
    });
    return treeNode;
  }
  if (typeof element === 'string') {
    const matchNode = new MatchNode(
      match(element)(),
      {
        type: 'string',
        value: element,
      },
      parentIndex,
    );
    matchNode.parentNode = parentNode;
    return matchNode;
  }
  if (typeof element === 'boolean') {
    if (element) {
      const trueMatchNode = new MatchNode(
        matchTrue,
        {
          type: 'loose',
          value: true,
        },
        parentIndex,
      );
      trueMatchNode.parentNode = parentNode;
      return trueMatchNode;
    }
    const falseMatchNode = new MatchNode(
      matchFalse,
      {
        type: 'loose',
        value: false,
      },
      parentIndex,
    );
    falseMatchNode.parentNode = parentNode;
    return falseMatchNode;
  }
  if (typeof element === 'function') {
    if (element.parserName === 'match') {
      const matchNode = new MatchNode(
        element(),
        {
          type: 'special',
          value: element.displayName,
        },
        parentIndex,
      );
      matchNode.parentNode = parentNode;
      return matchNode;
    }
    if (element.parserName === 'chainNodeFactory') {
      const chainNode = element(parentNode, null, parentIndex, parser);
      return chainNode;
    }
    const functionNode = new FunctionNode(element as ChainFunction, parentIndex, parser);
    functionNode.parentNode = parentNode;
    return functionNode;
  }
  throw Error(`unknow element in chain ${element}`);
};

export const chain: Chain = (...elements) => {
  return (
    solveAst = args => {
      return args;
    },
  ) => {
    const chainNodeFactory: ChainNodeFactory = (parentNode, creatorFunction, parentIndex = 0, parser) => {
      const chainNode = new ChainNode(parentIndex);
      chainNode.parentNode = parentNode;
      chainNode.creatorFunction = creatorFunction;
      chainNode.solveAst = solveAst;

      chainNode.childs = elements.map((element, index) => {
        return createNodeByElement(element, chainNode, index, parser);
      });

      if (creatorFunction) {
        generateFirstSet(chainNode, parser);
      }

      return chainNode;
    };
    (chainNodeFactory as any).parserName = 'chainNodeFactory';

    return chainNodeFactory;
  };
};

function getParser(root: ChainFunction) {
  if (parserMap.has(root)) {
    return parserMap.get(root);
  }
  const parser = new Parser();
  parser.rootChainNode = root()(null, null, 0, parser);
  parserMap.set(root, parser);
  return parser;
}

function scannerAddCursorToken(scanner: Scanner, cursorIndex: number, options?: CreateParserOptions) {
  let finalCursorIndex = cursorIndex;

  if (cursorIndex === null) {
    return { scanner, finalCursorIndex };
  }

  // Find where token cursorIndex is in.
  const cursorToken = scanner.getTokenByCharacterIndex(cursorIndex);

  // Generate cursor token, if cursor position is not in a token.
  if (!cursorToken) {
    // If cursor not on token, add a match-all token.
    scanner.addToken({
      type: 'cursor',
      value: null,
      position: [cursorIndex, cursorIndex],
    });
  } else if (options.cursorTokenExcludes(cursorToken)) {
    // If cursor is exclude, add a token next!
    scanner.addToken({
      type: 'cursor',
      value: null,
      position: [cursorIndex + 1, cursorIndex + 1],
    });
    finalCursorIndex += 1;
  }

  return { scanner, finalCursorIndex };
}

export const createParser = <AST = {}>(root: ChainFunction, lexer: Lexer, options?: CreateParserOptions) => {
  return (text: string, cursorIndex: number = null): IParseResult => {
    // eslint-disable-next-line no-param-reassign
    options = defaults(options || {}, new CreateParserOptions());

    const startTime = new Date();
    const tokens = lexer(text);
    const lexerTime = new Date();
    const originScanner = new Scanner(tokens);
    const { scanner, finalCursorIndex } = scannerAddCursorToken(new Scanner(tokens), cursorIndex, options);
    // eslint-disable-next-line no-param-reassign
    cursorIndex = finalCursorIndex;
    const parser = getParser(root);

    const cursorPrevToken = scanner.getPrevTokenByCharacterIndex(cursorIndex).prevToken;

    // If cursorPrevToken is null, the cursor prev node is root.
    let cursorPrevNodes: Node[] = cursorPrevToken === null ? [parser.rootChainNode] : [];

    let success = false;
    let ast: AST = null;
    let callVisiterCount = 0;
    let callParentCount = 0;
    let lastMatchUnderShortestRestToken: {
      restTokenCount: number;
      matchNode: MatchNode;
      token: IToken;
    } = null;

    // Parse without cursor token
    newVisit({
      node: parser.rootChainNode,
      scanner: originScanner,
      visiterOption: {
        onCallVisiter: (node, store) => {
          callVisiterCount += 1;

          if (callVisiterCount > MAX_VISITER_CALL) {
            // eslint-disable-next-line no-param-reassign
            store.stop = true;
          }
        },
        onVisiterNextNode: (node, store) => {
          callParentCount += 1;
          if (callParentCount > MAX_VISITER_CALL) {
            // eslint-disable-next-line no-param-reassign
            store.stop = true;
          }
        },
        onMatchNode: (matchNode, store, currentVisiterOption) => {
          const matchResult = matchNode.run(store.scanner);

          if (!matchResult.match) {
            tryChances(matchNode, store, currentVisiterOption);
          } else {
            const restTokenCount = store.scanner.getRestTokenCount();
            // Last match at least token remaining, is the most readable reason for error.
            if (
              !lastMatchUnderShortestRestToken ||
              (lastMatchUnderShortestRestToken && lastMatchUnderShortestRestToken.restTokenCount > restTokenCount)
            ) {
              lastMatchUnderShortestRestToken = {
                matchNode,
                token: matchResult.token,
                restTokenCount,
              };
            }

            visitNextNodeFromParent(matchNode, store, currentVisiterOption, {
              token: true,
              ...matchResult.token,
            });
          }
        },
        onSuccess: () => {
          success = true;
        },
        onFail: node => {
          success = false;
        },
      },
      parser,
    });

    // Parse with curosr token
    newVisit({
      node: parser.rootChainNode,
      scanner,
      visiterOption: {
        onCallVisiter: (node, store) => {
          callVisiterCount += 1;

          if (callVisiterCount > MAX_VISITER_CALL) {
            // eslint-disable-next-line no-param-reassign
            store.stop = true;
          }
        },
        onVisiterNextNode: (node, store) => {
          callParentCount += 1;
          if (callParentCount > MAX_VISITER_CALL) {
            // eslint-disable-next-line no-param-reassign
            store.stop = true;
          }
        },
        onSuccess: () => {
          ast = parser.rootChainNode.solveAst
            ? parser.rootChainNode.solveAst(parser.rootChainNode.astResults)
            : parser.rootChainNode.astResults;
        },
        onMatchNode: (matchNode, store, currentVisiterOption) => {
          const matchResult = matchNode.run(store.scanner);

          if (!matchResult.match) {
            tryChances(matchNode, store, currentVisiterOption);
          } else {
            // If cursor prev token isn't null, it may a cursor prev node.
            if (cursorPrevToken !== null && matchResult.token === cursorPrevToken) {
              cursorPrevNodes.push(matchNode);
            }

            visitNextNodeFromParent(matchNode, store, currentVisiterOption, {
              token: true,
              ...matchResult.token,
            });
          }
        },
      },
      parser,
    });

    cursorPrevNodes = uniq(cursorPrevNodes);

    // Get next matchings
    let nextMatchNodes = cursorPrevNodes.reduce(
      (all, cursorPrevNode) => {
        return all.concat(findNextMatchNodes(cursorPrevNode, parser));
      },
      [] as MatchNode[],
    );

    nextMatchNodes = uniqBy(nextMatchNodes, each => {
      return each.matching.type + each.matching.value;
    });

    // If has next token, filter nextMatchNodes by cursorNextToken
    const cursorNextToken = scanner.getNextTokenFromCharacterIndex(cursorIndex);
    if (cursorNextToken) {
      nextMatchNodes = nextMatchNodes.filter(nextMatchNode => {
        return !compareIgnoreLowerCaseWhenString(nextMatchNode.matching.value, cursorNextToken.value);
      });
    }

    // Get error message
    let error: IParseResult['error'] = null;

    if (!success) {
      const suggestions = uniqBy(
        (lastMatchUnderShortestRestToken
          ? findNextMatchNodes(lastMatchUnderShortestRestToken.matchNode, parser)
          : findNextMatchNodes(parser.rootChainNode, parser)
        ).map(each => {
          return each.matching;
        }),
        each => {
          return each.type + each.value;
        },
      );

      const errorToken =
        lastMatchUnderShortestRestToken && scanner.getNextByToken(lastMatchUnderShortestRestToken.token);

      if (errorToken) {
        error = {
          suggestions,
          token: errorToken,
          reason: 'wrong',
        };
      } else {
        error = {
          suggestions,
          token: lastMatchUnderShortestRestToken ? lastMatchUnderShortestRestToken.token : null,
          reason: 'incomplete',
        };
      }
    }

    const parserTime = new Date();

    // Find cursor ast from whole ast.
    const cursorKeyPath = getPathByCursorIndexFromAst(ast, cursorIndex).split('.');

    return {
      success,
      ast,
      cursorKeyPath: cursorKeyPath[0] === '' ? [] : cursorKeyPath,
      nextMatchings: nextMatchNodes
        .reverse()
        .map(each => {
          return each.matching;
        })
        .filter(each => {
          return !!each.value;
        }),
      error,
      debugInfo: {
        tokens,
        callVisiterCount,
        costs: {
          lexer: lexerTime.getTime() - startTime.getTime(),
          parser: parserTime.getTime() - startTime.getTime(),
        },
      },
    };
  };
};

function newVisit({
  node,
  scanner,
  visiterOption,
  parser,
}: {
  node: Node;
  scanner: Scanner;
  visiterOption: VisiterOption;
  parser: Parser;
}) {
  const defaultVisiterOption = new VisiterOption();
  defaults(visiterOption, defaultVisiterOption);

  const newStore = new VisiterStore(scanner, parser);
  visit({ node, store: newStore, visiterOption, childIndex: 0 });
}

const visit = tailCallOptimize(
  ({
    node,
    store,
    visiterOption,
    childIndex,
  }: {
    node: Node;
    store: VisiterStore;
    visiterOption: VisiterOption;
    childIndex: number;
  }) => {
    if (store.stop) {
      fail(node, store, visiterOption);
      return;
    }

    if (!node) {
      throw Error('no node!');
    }

    if (visiterOption.onCallVisiter) {
      visiterOption.onCallVisiter(node, store);
    }

    if (node instanceof ChainNode) {
      if (firstSetUnMatch(node, store, visiterOption, childIndex)) {
        return; // If unmatch, stop!
      }

      visitChildNode({ node, store, visiterOption, childIndex });
    } else if (node instanceof TreeNode) {
      visitChildNode({ node, store, visiterOption, childIndex });
    } else if (node instanceof MatchNode) {
      if (node.matching.type === 'loose') {
        if (node.matching.value === true) {
          visitNextNodeFromParent(node, store, visiterOption, null);
        } else {
          throw Error('Not support loose false!');
        }
      } else {
        visiterOption.onMatchNode(node, store, visiterOption);
      }
    } else if (node instanceof FunctionNode) {
      const functionName = node.chainFunction.name;
      const replacedNode = node.run();
      replacedNode.functionName = functionName;

      // eslint-disable-next-line no-param-reassign
      node.parentNode.childs[node.parentIndex] = replacedNode;
      visit({ node: replacedNode, store, visiterOption, childIndex: 0 });
    } else {
      throw Error(`Unexpected node type: ${node}`);
    }
  },
);

function visitChildNode({
  node,
  store,
  visiterOption,
  childIndex,
}: {
  node: ParentNode;
  store: VisiterStore;
  visiterOption: VisiterOption;
  childIndex: number;
}) {
  if (node instanceof ChainNode) {
    const child = node.childs[childIndex];
    if (child) {
      visit({ node: child, store, visiterOption, childIndex: 0 });
    } else {
      visitNextNodeFromParent(
        node,
        store,
        visiterOption,
        visiterOption.generateAst ? node.solveAst(node.astResults) : null,
      );
    }
  } else {
    // This case, Node === TreeNode
    const child = node.childs[childIndex];
    if (childIndex + 1 < node.childs.length) {
      addChances({
        node,
        store,
        visiterOption,
        tokenIndex: store.scanner.getIndex(),
        childIndex: childIndex + 1,
        addToNextMatchNodeFinders: true,
      });
    }
    if (child) {
      visit({ node: child, store, visiterOption, childIndex: 0 });
    } else {
      throw Error('tree node unexpect end');
    }
  }
}

const visitNextNodeFromParent = tailCallOptimize(
  (node: Node, store: VisiterStore, visiterOption: VisiterOption, astValue: any) => {
    if (store.stop) {
      fail(node, store, visiterOption);
      return;
    }

    if (visiterOption.onVisiterNextNode) {
      visiterOption.onVisiterNextNode(node, store);
    }

    if (!node.parentNode) {
      return noNextNode(node, store, visiterOption);
    }

    if (node.parentNode instanceof ChainNode) {
      if (visiterOption.generateAst) {
        // eslint-disable-next-line no-param-reassign
        node.parentNode.astResults[node.parentIndex] = astValue;
      }

      // A       B <- next node      C
      // └── node <- current node
      visit({ node: node.parentNode, store, visiterOption, childIndex: node.parentIndex + 1 });
    } else if (node.parentNode instanceof TreeNode) {
      visitNextNodeFromParent(node.parentNode, store, visiterOption, astValue);
    } else {
      throw Error(`Unexpected parent node type: ${node.parentNode}`);
    }
  },
);

function noNextNode(node: Node, store: VisiterStore, visiterOption: VisiterOption) {
  if (store.scanner.isEnd()) {
    if (visiterOption.onSuccess) {
      visiterOption.onSuccess();
    }
  } else {
    tryChances(node, store, visiterOption);
  }
}

function addChances({
  node,
  store,
  visiterOption,
  tokenIndex,
  childIndex,
  addToNextMatchNodeFinders,
}: {
  node: ParentNode;
  store: VisiterStore;
  visiterOption: VisiterOption;
  tokenIndex: number;
  childIndex: number;
  addToNextMatchNodeFinders: boolean;
}) {
  const chance = {
    node,
    tokenIndex,
    childIndex,
  };

  store.restChances.push(chance);
}

function hasParentNodeByFunctionName(node: Node, functionName: string): boolean {
  if (node instanceof ChainNode && node.functionName === functionName) {
    return true;
  }

  if (node.parentNode) {
    return hasParentNodeByFunctionName(node.parentNode, functionName);
  }

  return false;
}

function tryChances(node: Node, store: VisiterStore, visiterOption: VisiterOption) {
  if (store.restChances.length === 0) {
    fail(node, store, visiterOption);
    return;
  }

  const nextChance = store.restChances.pop();

  // reset scanner index
  store.scanner.setIndex(nextChance.tokenIndex);

  visit({ node: nextChance.node, store, visiterOption, childIndex: nextChance.childIndex });
}

function fail(node: Node, store: VisiterStore, visiterOption: VisiterOption) {
  if (visiterOption.onFail) {
    visiterOption.onFail(node);
  }
}

// Find all tokens that may appear next
function findNextMatchNodes(node: Node, parser: Parser): MatchNode[] {
  const nextMatchNodes: MatchNode[] = [];

  let passCurrentNode = false;

  const visiterOption: VisiterOption = {
    generateAst: false,
    enableFirstSet: false,
    onMatchNode: (matchNode, store, currentVisiterOption) => {
      if (matchNode === node && passCurrentNode === false) {
        passCurrentNode = true;
        visitNextNodeFromParent(matchNode, store, currentVisiterOption, null);
      } else {
        nextMatchNodes.push(matchNode);
      }

      // Suppose the match failed, so we can find another possible match chance!
      tryChances(matchNode, store, currentVisiterOption);
    },
  };

  newVisit({ node, scanner: new Scanner([]), visiterOption, parser });

  return nextMatchNodes;
}

// First Set -----------------------------------------------------------------

function firstSetUnMatch(node: ChainNode, store: VisiterStore, visiterOption: VisiterOption, childIndex: number) {
  if (
    visiterOption.enableFirstSet &&
    node.creatorFunction &&
    childIndex === 0 &&
    store.parser.firstSet.has(node.creatorFunction)
  ) {
    const firstMatchNodes = store.parser.firstSet.get(node.creatorFunction);

    // If not match any first match node, try chances
    if (
      !firstMatchNodes.some(firstMatchNode => {
        return firstMatchNode.run(store.scanner, false).match;
      })
    ) {
      tryChances(node, store, visiterOption);
      return true; // Yes, unMatch.
    }
    return false; // No, Match.
  }
}

function generateFirstSet(node: ChainNode, parser: Parser) {
  if (parser.firstSet.has(node.creatorFunction)) {
    return;
  }

  const firstMatchNodes = getFirstOrFunctionSet(node, node.creatorFunction, parser);
  parser.firstOrFunctionSet.set(node.creatorFunction, firstMatchNodes);

  solveFirstSet(node.creatorFunction, parser);
}

function getFirstOrFunctionSet(node: Node, creatorFunction: ChainFunction, parser: Parser): FirstOrFunctionSet[] {
  if (node instanceof ChainNode) {
    if (node.childs[0]) {
      return getFirstOrFunctionSet(node.childs[0], creatorFunction, parser);
    }
  } else if (node instanceof TreeNode) {
    return node.childs.reduce((all, next) => {
      return all.concat(getFirstOrFunctionSet(next, creatorFunction, parser));
    }, []);
  } else if (node instanceof MatchNode) {
    return [node];
  } else if (node instanceof FunctionNode) {
    if (parser.relatedSet.has(node.chainFunction)) {
      parser.relatedSet.get(node.chainFunction).add(creatorFunction);
    } else {
      parser.relatedSet.set(node.chainFunction, new Set([creatorFunction]));
    }

    return [node.chainFunction];
  } else {
    throw Error(`Unexpected node: ${node}`);
  }
}

function solveFirstSet(creatorFunction: ChainFunction, parser: Parser) {
  if (parser.firstSet.has(creatorFunction)) {
    return;
  }

  const firstMatchNodes = parser.firstOrFunctionSet.get(creatorFunction);

  // Try if relate functionName has done first set.
  const newFirstMatchNodes = firstMatchNodes.reduce(
    (all, firstMatchNode) => {
      if (typeof firstMatchNode === 'string') {
        if (parser.firstSet.has(firstMatchNode)) {
          // eslint-disable-next-line no-param-reassign
          all = all.concat(parser.firstSet.get(firstMatchNode));
        } else {
          all.push(firstMatchNode);
        }
      } else {
        all.push(firstMatchNode);
      }

      return all;
    },
    [] as FirstOrFunctionSet[],
  );

  parser.firstOrFunctionSet.set(creatorFunction, newFirstMatchNodes);

  // If all set hasn't function node, we can solve it's relative set.
  if (
    newFirstMatchNodes.every(firstMatchNode => {
      return firstMatchNode instanceof MatchNode;
    })
  ) {
    parser.firstSet.set(creatorFunction, newFirstMatchNodes as MatchNode[]);

    // If this functionName has related functionNames, solve them
    if (parser.relatedSet.has(creatorFunction)) {
      const relatedFunctionNames = parser.relatedSet.get(creatorFunction);
      relatedFunctionNames.forEach(relatedFunctionName => {
        return solveFirstSet;
      });
    }
  }
}

// First set /////////////////////////////////////////////////////////////////
