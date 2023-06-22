import { IToken } from '../lexer/token';

export class Scanner {
  private tokens: IToken[] = [];

  private index = 0;

  constructor(tokens: IToken[], index = 0) {
    // ignore whitespace, comment
    this.tokens = tokens.slice();
    this.index = index;
  }

  public read = () => {
    const token = this.tokens[this.index];
    if (token) {
      return token;
    }
    return false;
  };

  public next = () => {
    this.index += 1;
  };

  public isEnd = () => {
    return this.index >= this.tokens.length;
  };

  public getIndex = () => {
    return this.index;
  };

  public setIndex = (index: number) => {
    this.index = index;
    return index;
  };

  public getRestTokenCount = () => {
    return this.tokens.length - this.index - 1;
  };

  public getNextByToken = (token: IToken) => {
    const currentTokenIndex = this.tokens.findIndex(eachToken => {
      return eachToken === token;
    });
    if (currentTokenIndex > -1) {
      if (currentTokenIndex + 1 < this.tokens.length) {
        return this.tokens[currentTokenIndex + 1];
      }
      return null;
    }
    throw Error(`token ${token.value.toString()} not exist in scanner.`);
  };

  public getTokenByCharacterIndex = (characterIndex: number) => {
    if (characterIndex === null) {
      return null;
    }

    for (const token of this.tokens) {
      if (characterIndex >= token.position[0] && characterIndex - 1 <= token.position[1]) {
        return token;
      }
    }

    return null;
  };

  public getPrevTokenByCharacterIndex = (characterIndex: number) => {
    let prevToken: IToken = null;
    let prevTokenIndex: number = null;

    this.tokens.forEach((token, index) => {
      if (token.position[1] < characterIndex - 1) {
        prevToken = token;
        prevTokenIndex = index;
      }
    });

    return { prevToken, prevTokenIndex };
  };

  public getNextTokenFromCharacterIndex = (characterIndex: number) => {
    for (const token of this.tokens) {
      if (token.position[0] > characterIndex) {
        return token;
      }
    }

    return null;
  };

  public addToken = (token: IToken) => {
    const { prevToken, prevTokenIndex } = this.getPrevTokenByCharacterIndex(token.position[0]);

    if (prevToken) {
      // prevTokenIndex
      this.tokens.splice(prevTokenIndex + 1, 0, token);
    } else {
      this.tokens.unshift(token);
    }
  };
}
