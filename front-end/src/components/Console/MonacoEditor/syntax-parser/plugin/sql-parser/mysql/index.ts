import { createParser } from '../../..';
import { IStatements } from '../base/define';
import { sqlTokenizer } from './lexer';
import { root } from './parser';

export const mysqlParser = createParser<IStatements>(root, sqlTokenizer, {
  cursorTokenExcludes: token => {
    return token.value === '.' || token.value === ':';
  },
});
