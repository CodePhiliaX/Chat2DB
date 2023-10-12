import { DatabaseTypeCode } from '../common';

export default {
  type: DatabaseTypeCode.MYSQL,
  keywords: [
    'SELECT',
    'FROM',
    'WHERE',
    'LIMIT',
    'AND',
    'OR',
    'NOT',
    'BETWEEN',
    'LIKE',
    'IN',
    'IS',
    'NULL',
    'ORDER BY',
    'GROUP BY',
    'HAVING',
    'LIMIT',
    'ASC',
    'DESC',
  ],
  functions: ['AVG', 'COUNT', 'FIRST', 'LAST', 'MAX', 'MIN', 'SUM', 'MID', 'LEN', 'ROUND', 'NOW', 'FORMAT'],
};
