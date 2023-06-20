/* eslint-disable no-use-before-define */
import { chain, many, matchTokenType, optional } from '../../..';
import { reserveKeys } from './reserve-keys';

// ----------------------------------- Utils -----------------------------------

// TODO: https://github.com/antlr/grammars-v4/blob/master/mysql/MySqlParser.g4#L1963
export const dataType = () => {
  return chain([
    chain(['char', 'varchar', 'tinytext', 'text', 'mediumtext', 'longtext'])(ast => {
      return ast[0];
    }),
    chain(['tinyint', 'smallint', 'mediumint', 'int', 'integer', 'bigint'])(ast => {
      return ast[0];
    }),
    chain(['real', 'double', 'float'])(ast => {
      return ast[0];
    }),
    chain(['decimal', 'numberic'])(ast => {
      return ast[0];
    }),
    chain(['date', 'tinyblob', 'blob', 'mediumblob', 'longblob', 'bool', 'boolean'])(ast => {
      return ast[0];
    }),
    chain(['bit', 'time', 'timestamp', 'datetime', 'binary', 'varbinary', 'year'])(ast => {
      return ast[0];
    }),
    chain(['enum', 'set'])(ast => {
      return ast[0];
    }),
    chain('geometrycollection', 'linestring', 'multilinestring', 'multipoint', 'multipolygon', 'point', 'polygon')(
      ast => {
        return ast[0];
      },
    ),
  ])(ast => {
    return ast[0];
  });
};

export const setValue = () => {
  return chain(wordSym, '=', [stringSym, numberSym])();
};

export const setValueList = () => {
  return chain(setValue, many(',', setValue))();
};

// ----------------------------------- others -----------------------------------

export const wordSym = () => {
  return chain([matchTokenType('cursor'), matchTokenType('word', { excludes: reserveKeys })])(ast => {
    return ast[0];
  });
};

export const stringSym = () => {
  return chain(matchTokenType('string'))(ast => {
    return ast[0];
  });
};

export const numberSym = () => {
  return chain(matchTokenType('number'))(ast => {
    return ast[0];
  });
};

export const stringOrWord = () => {
  return chain([wordSym, stringSym])(ast => {
    return ast[0];
  });
};

export const stringOrWordOrNumber = () => {
  return chain([wordSym, stringSym, numberChain])(ast => {
    return ast[0];
  });
};

export const numberChain = () => {
  return chain(optional(['-', '+']), numberSym)();
};

export const logicalOperator = () => {
  return chain(['and', '&&', 'xor', 'or', '||'])(ast => {
    return ast[0];
  });
};

export const normalOperator = () => {
  return chain(['&&', '||'])(ast => {
    return ast[0];
  });
};

export const comparisonOperator = () => {
  return chain(['=', '>', '<', '<=', '>=', '<>', '!=', '<=>'])(ast => {
    return ast[0];
  });
};

export const notOperator = () => {
  return chain(['not', '!'])(ast => {
    return ast[0];
  });
};

export const selectSpec = () => {
  return chain([
    'all',
    'distinct',
    'distinctrow',
    'high_priority',
    'straight_join',
    'sql_small_result',
    'sql_big_result',
    'sql_buffer_result',
    'sql_cache',
    'sql_no_cache',
    'sql_calc_found_rows',
  ])(ast => {
    return ast[0];
  });
};
