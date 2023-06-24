/* eslint-disable no-use-before-define */
import { chain, many, optional, plus } from '../../..';
import { createFourOperations } from '../base/four-operations';
import {
  comparisonOperator,
  dataType,
  logicalOperator,
  normalOperator,
  notOperator,
  numberSym,
  selectSpec,
  setValueList,
  stringOrWord,
  stringOrWordOrNumber,
  stringSym,
  wordSym,
} from '../base/parser';
import { createTableName, flattenAll } from '../base/utils';

export const root = () => {
  return chain(statements, optional(';'))(ast => {
    return ast[0];
  });
};

const statements = () => {
  return chain(
    statement,
    many(
      chain(';', statement)(ast => {
        return ast[1];
      }),
    ),
  )(flattenAll);
};

const statement = () => {
  return chain([
    selectStatement,
    createTableStatement,
    insertStatement,
    createViewStatement,
    setStatement,
    createIndexStatement,
    createFunctionStatement,
    updateStatement,
  ])(ast => {
    return ast[0];
  });
};

// ----------------------------------- select statement -----------------------------------

const selectStatement = () => {
  return chain(
    'select',
    selectList,
    optional(fromClause),
    optional(orderByClause),
    optional(limitClause),
    optional(union, selectStatement),
  )(ast => {
    const result: any = {
      type: 'statement',
      variant: 'select',
      result: ast[1],
      from: ast[2],
    };

    if (ast[5]) {
      // eslint-disable-next-line prefer-destructuring
      result.union = ast[5];
    }

    return result;
  });
};

const union = () => {
  return chain('union', ['all', 'distinct'])();
};

const fromClause = () => {
  return chain('from', tableSources, optional(whereStatement), optional(groupByStatement), optional(havingStatement))(
    ast =>
      // TODO: Ignore where group having
      {
        return {
          sources: ast[1],
          where: ast[2],
          group: ast[3],
          having: ast[4],
        };
      },
  );
};

const selectList = () => {
  return chain(selectField, many(selectListTail))(flattenAll);
};

const selectListTail = () => {
  return chain(',', selectField)(ast => {
    return ast[1];
  });
};

// selectField
//         ::= not? field alias?
//         ::= not? ( field ) alias?
//           | *
const selectField = () => {
  return chain([
    chain(
      many('not'),
      [
        chain(field, optional(overClause))(ast =>
          // TODO: Ignore overClause
          {
            return ast[0];
          },
        ),
        chain('(', field, ')')(),
      ],
      optional(alias),
    )(ast => {
      return {
        type: 'identifier',
        variant: 'column',
        name: ast[1],
        alias: ast[2],
      };
    }),
    '*',
  ])(ast => {
    return ast[0];
  });
};

const whereStatement = () => {
  return chain('where', expression)(ast => {
    return ast[1];
  });
};

// fieldList
//       ::= field (, fieldList)?
const fieldList = () => {
  return chain(columnField, many(',', columnField))();
};

const tableSources = () => {
  return chain(
    tableSource,
    many(
      chain(',', tableSource)(ast => {
        return ast[1];
      }),
    ),
  )(flattenAll);
};

const tableSource = () => {
  return chain(tableSourceItem, many(joinPart))(ast => {
    return {
      source: ast[0],
      joins: ast[1],
      type: 'statement',
      variant: 'tableSource',
    };
  });
};

const tableSourceItem = () => {
  return chain([
    chain(tableName, optional(alias))(ast => {
      return {
        type: 'identifier',
        variant: 'table',
        name: ast[0],
        alias: ast[1],
      };
    }),
    chain(
      [
        selectStatement,
        chain('(', selectStatement, ')')(ast => {
          return ast[1];
        }),
      ],
      alias,
    )(ast => {
      return {
        ...ast[0],
        alias: ast[1],
      };
    }),
  ])(ast => {
    return ast[0];
  });
};

const joinPart = () => {
  return chain(
    [
      'join',
      'straight_join',
      chain(['inner', 'cross', 'full'], 'join')(),
      chain(['left', 'right'], optional('outer'), 'join')(),
      chain('natural', optional(['left', 'right'], optional('outer')), 'join')(),
    ],
    tableSourceItem,
    optional('on', expression),
  )(ast => {
    return {
      type: 'statement',
      variant: 'join',
      join: ast[1],
      conditions: ast[2],
    };
  });
};

// Alias ::= AS WordOrString
//         | WordOrString
const alias = () => {
  return chain([
    chain('as', stringOrWord)(ast => {
      return ast[1];
    }),
    stringOrWord,
  ])(ast => {
    return ast[0];
  });
};

// ----------------------------------- Create table statement -----------------------------------
const createTableStatement = () => {
  return chain('create', 'table', stringOrWord, '(', tableOptions, ')', optional(withStatement))();
};

const withStatement = () => {
  return chain('with', '(', withStatements, ')')();
};

const withStatements = () => {
  return chain(withStatementsTail, many(',', withStatementsTail))();
};

const withStatementsTail = () => {
  return chain(wordSym, '=', stringSym)();
};

const tableOptions = () => {
  return chain(tableOption, many(',', tableOption))();
};

const tableOption = () => {
  return chain(stringOrWord, dataType)();
};

const primaryKeyList = () => {
  return chain(wordSym, optional(',', primaryKeyList))();
};

const tableName = () => {
  return chain([
    chain(stringOrWord)(),
    chain(stringOrWord, '.', stringOrWord)(ast => {
      return [ast[0], ast[2]];
    }),
  ])(ast => {
    if (ast[0].length === 1) {
      return createTableName({
        namespace: null,
        tableName: ast[0][0],
      });
    }
    if (ast[0].length === 2) {
      return createTableName({
        namespace: ast[0][0],
        tableName: ast[0][1],
      });
    }
  });
};

// ----------------------------------- Having --------------------------------------------------
const havingStatement = () => {
  return chain('having', expression)();
};

// ----------------------------------- Create view statement -----------------------------------
const createViewStatement = () => {
  return chain('create', 'view', wordSym, 'as', selectStatement)();
};

// ----------------------------------- Insert statement -----------------------------------
const insertStatement = () => {
  return chain('insert', optional('ignore'), 'into', tableName, optional(selectFieldsInfo), [selectStatement])(ast => {
    return {
      type: 'statement',
      variant: 'insert',
      into: {
        type: 'indentifier',
        variant: 'table',
        name: ast[3],
      },
      result: ast[5],
    };
  });
};

const selectFieldsInfo = () => {
  return chain('(', selectFields, ')')();
};

const selectFields = () => {
  return chain(wordSym, many(',', wordSym))();
};

// ----------------------------------- groupBy -----------------------------------
const groupByStatement = () => {
  return chain('group', 'by', fieldList)();
};

// ----------------------------------- orderBy -----------------------------------
const orderByClause = () => {
  return chain('order', 'by', orderByExpressionList)();
};

const orderByExpressionList = () => {
  return chain(orderByExpression, many(',', orderByExpression))();
};

const orderByExpression = () => {
  return chain(expression, optional(['asc', 'desc']))();
};

/*
<PARTITION BY clause> ::=  
PARTITION BY value_expression , ... [ n ] 
*/

const partitionByClause = () => {
  return chain([wordSym, chain('partition', 'by', expression)()])(ast => {
    return ast;
  });
};

/*
OVER (   
       [ <PARTITION BY clause> ]  
       [ <ORDER BY clause> ]   
       [ <ROW or RANGE clause> ]  
      )  
*/
const overClause = () => {
  return chain('over', '(', overTailExpression, ')')();
};

const overTailExpression = () => {
  return chain([partitionByClause, chain(field, orderByClause)()], many(',', overTailExpression))();
};

// ----------------------------------- limit -----------------------------------
const limitClause = () => {
  return chain('limit', [numberSym, chain(numberSym, ',', numberSym)(), chain(numberSym, 'offset', numberSym)()])();
};

// ----------------------------------- Function -----------------------------------
const functionChain = () => {
  return chain([castFunction, normalFunction, ifFunction])(ast => {
    return ast[0];
  });
};

const ifFunction = () => {
  return chain('if', '(', predicate, ',', field, ',', field, ')')(ast => {
    return {
      type: 'function',
      name: 'if',
      args: [ast[2], ast[4], ast[6]],
    };
  });
};

const castFunction = () => {
  return chain('cast', '(', field, 'as', dataType, ')')(ast => {
    return {
      type: 'function',
      name: 'cast',
      args: [ast[2], ast[4]],
    };
  });
};

const normalFunction = () => {
  return chain(wordSym, '(', optional(functionFields), ')', optional('filter', '(', whereStatement, ')'))(ast => {
    return {
      type: 'function',
      name: ast[0],
      args: ast[2],
    };
  });
};

const functionFields = () => {
  return chain(functionFieldItem, many(',', functionFieldItem))();
};

const functionFieldItem = () => {
  return chain(many(selectSpec), [columnField, caseStatement])(ast => {
    return ast;
  });
};

// ----------------------------------- Case -----------------------------------
const caseStatement = () => {
  return chain('case', plus(caseAlternative), optional('else', [columnField, 'null']), [
    'end',
    chain('end', 'as', wordSym)(),
  ])();
};

const caseAlternative = () => {
  return chain('when', expression, 'then', fieldItem)();
};

// ----------------------------------- set statement -----------------------------------

const setStatement = () => {
  return chain('set', variableAssignments)();
};

const variableAssignments = () => {
  return chain(variableAssignment, many(',', variableAssignment))();
};

const variableAssignment = () => {
  return chain(variableLeftValue, '=', ['true', 'false', stringSym, numberSym])();
};

const variableLeftValue = () => {
  return chain(wordSym, many('.', wordSym))();
};

// ----------------------------------- Expression -----------------------------------

/*
 * expr:
 *   expr OR expr
 * | expr || expr
 * | expr XOR expr
 * | expr AND expr
 * | expr && expr
 * | NOT expr
 * | ! expr
 * | boolean_primary IS [NOT] {TRUE | FALSE | UNKNOWN}
 * | boolean_primary
 * */

const expression = () => {
  return chain(expressionHead, many(logicalOperator, expression))(flattenAll);
};

const expressionHead = () => {
  return chain(
    [chain('(', expression, ')')(), chain(notOperator, expression)(), chain(booleanPrimary)],
    optional(chain('is', optional('not'), ['true', 'false', 'unknown'])()),
  )(ast => {
    return ast[0];
  });
};

// /*
//  *boolean_primary:
//  *   boolean_primary IS [NOT] NULL
//  * | boolean_primary <=> predicate
//  * | boolean_primary comparison_operator predicate
//  * | boolean_primary comparison_operator {ALL | ANY} (subquery)
//  * | predicate
// **/
const booleanPrimary = () => {
  return chain(predicate, many(['isnull', chain([chain('is', 'not')(), 'is', 'not'], ['null', columnField])()]))(
    flattenAll,
  );
};

/*
 * predicate:
 *    field SOUNDS LIKE field
 *  | field [NOT] IN (subquery)
 *  | field [NOT] IN (expr [, expr] ...)
 *  | field [NOT] BETWEEN field AND predicate
 *  | field [NOT] LIKE simple_expr [ESCAPE simple_expr]
 *  | field [NOT] REGEXP field
 *  | field
 * */
const predicate = () => {
  return chain([
    chain(columnField, predicateAddonComparison)(),
    chain('(', predicate, ')', predicateAddonComparison)(),
  ])();
};

const predicateAddonComparison = () => {
  return chain(
    optional([chain(comparisonOperator, columnField)(), chain('sounds', 'like', columnField)(), isOrNotExpression]),
    optional(['or', predicate]),
  )();
};

const columnField = () => {
  return chain(field)(ast => {
    return {
      type: 'identifier',
      variant: 'column',
      name: ast[0],
    };
  });
};

const isOrNotExpression = () => {
  return chain(optional('is'), optional('not'), [
    chain('in', '(', [selectStatement, fieldList], ')')(),
    chain('between', field, 'and', predicate)(),
    chain('like', field, optional('escape', field))(),
    chain('regexp', field)(),
    'null',
  ])();
};

const fieldItem = () => {
  return chain(fieldItemDetail, many(normalOperator, fieldItemDetail))(ast => {
    if (!ast[1]) {
      return ast[0];
    }
    return [ast[0], ast[1]];
  });
};

const fieldItemDetail = () => {
  return chain([
    functionChain,
    caseStatement,
    chain(
      stringOrWordOrNumber,
      optional([
        chain('.', '*')(ast => {
          return {
            type: 'identifier',
            variant: 'groupAll',
          };
        }),
        chain(':', normalFunction)(),
        dotStringOrWordOrNumber,
      ]),
    )(ast => {
      if (!ast[1]) {
        return ast[0];
      }

      return {
        ...ast[1],
        groupName: ast[0],
      };
    }),
    '*',
  ])(ast => {
    return ast[0];
  });
};

const dotStringOrWordOrNumber = () => {
  return chain('.', [
    stringSym,
    numberSym,
    chain(wordSym)(ast => {
      return {
        type: 'identifier',
        variant: 'columnAfterGroup',
        name: ast[0],
      };
    }),
  ])(ast => {
    return ast[1];
  });
};

const field = () => {
  return createFourOperations(fieldItem)();
};

// ----------------------------------- create index expression -----------------------------------
const createIndexStatement = () => {
  return chain('create', 'index', indexItem, onStatement, whereStatement)();
};

const indexItem = () => {
  return chain(stringSym, many('.', stringSym))();
};

const onStatement = () => {
  return chain('ON', stringSym, '(', fieldForIndexList, ')')();
};

const fieldForIndex = () => {
  return chain(stringSym, optional(['ASC', 'DESC']))();
};

const fieldForIndexList = () => {
  return chain(fieldForIndex, many(',', fieldForIndex))();
};

// ----------------------------------- create function expression -----------------------------------
const createFunctionStatement = () => {
  return chain('create', 'function', wordSym, 'as', stringSym)();
};

// ----------------------------------- update statement -----------------------------------
const updateStatement = () => {
  return chain('UPDATE', tableSourceItem, 'SET', setValueList, optional(whereStatement))();
};
