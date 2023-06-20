/* eslint-disable no-use-before-define */
import { chain, ChainFunction, optional } from '../../..';

// Four operations ---------------------------------
export function createFourOperations(field: ChainFunction) {
  const addExpr = () => {
    return chain(term, exprTail)(ast => {
      return ast[0];
    });
  };

  const exprTail = () => {
    return chain(optional(addOp, term, exprTail))();
  };

  const term = () => {
    return chain(factor, termTail)(ast => {
      return ast[0];
    });
  };

  const termTail = () => {
    return chain(optional(mulOp, factor, termTail))();
  };

  const mulOp = () => {
    return chain(['*', '/', '%'])(ast => {
      return ast[0];
    });
  };

  const addOp = () => {
    return chain(['+', '-'])(ast => {
      return ast[0];
    });
  };

  const factor = () => {
    return chain([
      chain('(', addExpr, ')')(ast => {
        return ast[1];
      }),
      field,
    ])(ast => {
      return ast[0];
    });
  };

  return addExpr;
}
