import { IToken } from '../lexer/token';
import { chain } from './chain';
import { IElements } from './define';
import { Scanner } from './scanner';

export interface IMatch {
  token?: IToken;
  match: boolean;
}

function equalWordOrIncludeWords(str: string, word: string | string[] | null) {
  if (typeof word === 'string') {
    return judgeMatch(str, word);
  }
  return word.some(eachWord => {
    return judgeMatch(str, eachWord);
  });
}

function judgeMatch(source: string, target: string) {
  if (source === null) {
    return false;
  }
  return (source && source.toLowerCase()) === (target && target.toLowerCase());
}

function matchToken(scanner: Scanner, compare: (token: IToken) => boolean, isCostToken?: boolean): IMatch {
  const token = scanner.read();
  if (!token) {
    return {
      token: null,
      match: false,
    };
  }
  if (compare(token)) {
    if (isCostToken) {
      scanner.next();
    }

    return {
      token,
      match: true,
    };
  }
  return {
    token,
    match: false,
  };
}

function createMatch<T>(fn: (scanner: Scanner, arg?: T, isCostToken?: boolean) => IMatch, specialName?: string) {
  return (arg?: T) => {
    function foo() {
      return (scanner: Scanner, isCostToken?: boolean) => {
        return fn(scanner, arg, isCostToken);
      };
    }

    foo.parserName = 'match';

    foo.displayName = specialName;
    return foo;
  };
}

export const match = createMatch((scanner, word: string | string[], isCostToken) => {
  return matchToken(
    scanner,
    token => {
      return equalWordOrIncludeWords(token.value, word);
    },
    isCostToken,
  );
});

interface IMatchTokenTypeOption {
  includes?: string[];
  excludes?: string[];
}

export const matchTokenType = (tokenType: string, opts: IMatchTokenTypeOption = {}) => {
  const options: IMatchTokenTypeOption = { includes: [], excludes: [], ...opts };

  return createMatch((scanner, word, isCostToken) => {
    return matchToken(
      scanner,
      token => {
        if (
          options.includes.some(includeValue => {
            return judgeMatch(includeValue, token.value);
          })
        ) {
          return true;
        }

        if (
          options.excludes.some(includeValue => {
            return judgeMatch(includeValue, token.value);
          })
        ) {
          return false;
        }

        if (token.type !== tokenType) {
          return false;
        }

        return true;
      },
      isCostToken,
    );
  }, tokenType)();
};

export const matchTrue = (): IMatch => {
  return {
    token: null,
    match: true,
  };
};

export const matchFalse = (): IMatch => {
  return {
    token: null,
    match: true,
  };
};

export const optional = (...elements: IElements) => {
  if (elements.length === 0) {
    throw Error('Must have arguments!');
  }

  return chain([
    chain(...elements)(ast => {
      return elements.length === 1 ? ast[0] : ast;
    }),
    true,
  ])(ast => {
    return ast[0];
  });
};

export const plus = (...elements: IElements) => {
  if (elements.length === 0) {
    throw Error('Must have arguments!');
  }

  const plusFunction = () => {
    return chain(
      chain(...elements)(ast => {
        return elements.length === 1 ? ast[0] : ast;
      }),
      optional(plusFunction),
    )(ast => {
      if (ast[1]) {
        return [ast[0]].concat(ast[1]);
      }
      return [ast[0]];
    });
  };
  return plusFunction;
};

export const many = (...elements: IElements) => {
  if (elements.length === 0) {
    throw Error('Must have arguments!');
  }
  return optional(plus(...elements));
};
