import { IToken } from './token';

export { IToken };

interface ILexerConfig {
  type: string;
  regexes: RegExp[];
  /**
   * Will match, by not add to token list.
   */
  ignore?: boolean;
}

class Tokenizer {
  // eslint-disable-next-line no-useless-constructor, @typescript-eslint/no-parameter-properties
  constructor(public lexerConfig: ILexerConfig[]) {
    //
  }

  public tokenize(input: string) {
    const tokens = [];
    let token: IToken;
    let lastPosition = 0;

    // Keep processing the string until it is empty
    while (input.length) {
      // Get the next token and the token type
      const result = this.getNextToken(input);

      if (!result || !result.token) {
        throw Error(`Lexer: Unexpected string "${input}".`);
      }

      // eslint-disable-next-line prefer-destructuring
      token = result.token;

      if (!token.value) {
        throw Error(`Lexer: Regex parse error, please check your lexer config.`);
      }

      token.position = [lastPosition, lastPosition + token.value.length - 1];
      lastPosition += token.value.length;

      // Advance the string
      // eslint-disable-next-line no-param-reassign
      input = input.substring(token.value.length);

      if (!result.config.ignore) {
        tokens.push(token);
      }
    }
    return tokens;
  }

  private getNextToken(input: string) {
    for (const eachLexer of this.lexerConfig) {
      for (const regex of eachLexer.regexes) {
        const token = this.getTokenOnFirstMatch({ input, type: eachLexer.type, regex });
        if (token) {
          return {
            token,
            config: eachLexer,
          };
        }
      }
    }

    return null;
  }

  private getTokenOnFirstMatch({ input, type, regex }: { input: string; type: string; regex: RegExp }) {
    const matches = input.match(regex);

    if (matches) {
      // eslint-disable-next-line @typescript-eslint/no-object-literal-type-assertion
      return { type, value: matches[1] } as IToken;
    }
  }
}

export type Lexer = (text: string) => IToken[];

export const createLexer = (lexerConfig: ILexerConfig[]): Lexer => {
  return (text: string) => {
    return new Tokenizer(lexerConfig).tokenize(text);
  };
};
