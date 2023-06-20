import { createLexer } from '../../..';

export const sqlTokenizer = createLexer([
  {
    type: 'whitespace',
    regexes: [/^(\s+)/],
    ignore: true,
  },
  {
    type: 'comment',
    regexes: [
      /^((?:#|--).*?(?:\n|$))/, // # --
      /^(\/\*[^]*?(?:\*\/|$))/, // /* */
    ],
    ignore: true,
  },
  {
    type: 'number',
    regexes: [/^([0-9]+(\.[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+)\b/],
  },
  {
    type: 'word',
    regexes: [
      /^([a-zA-Z0-9_]+)/, // word
      /^(\$\{[a-zA-Z0-9_]+\})/, // ${word}
    ],
  },
  {
    type: 'string',
    regexes: [
      /^((?=")(?:"[^"\\]*(?:\\[\s\S][^"\\]*)*"))/, // ""
      /^((?=')(?:'[^'\\]*(?:\\[\s\S][^'\\]*)*'))/, // ''
      /^((?=`)(?:`[^`\\]*(?:\\[\s\S][^`\\]*)*`))/, // ``
    ],
  },
  {
    type: 'special',
    regexes: [
      /^(\(|\))/, // '(' ')'.
      /^(!=|<>|==|<=|>=|!<|!>|\|\||::|->>|->|~~\*|~~|!~~\*|!~~|~\*|!~\*|!~|.)/, // operators.
    ],
  },
]);
