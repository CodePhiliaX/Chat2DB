import assert from 'node:assert/strict';
import type { IViewTableParams } from '@/typings';
import { getViewTableTargetKey } from './viewTableTarget';

const params: IViewTableParams = {
  dataSourceId: 1,
  databaseName: 'chat2db',
  schemaName: 'public',
  databaseType: 'POSTGRESQL',
  tableName: 'orders',
};

// The workspace tab layer rebuilds the params object on unrelated renders, so a
// new object with the same table identity must keep the same key. Otherwise the
// browse request is re-issued on every tab switch.
assert.equal(
  getViewTableTargetKey({ ...params }),
  getViewTableTargetKey(params),
  'a rebuilt params object with the same table identity must keep the same target key',
);

// Pagination is not part of the table identity: paging must not look like a new table.
assert.equal(
  getViewTableTargetKey({ ...params, pageNo: 3, pageSize: 100 }),
  getViewTableTargetKey(params),
  'pagination fields must not change the target key',
);

assert.notEqual(
  getViewTableTargetKey({ ...params, dataSourceId: 2 }),
  getViewTableTargetKey(params),
  'a different datasource must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, databaseName: 'other' }),
  getViewTableTargetKey(params),
  'a different database must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, schemaName: 'other' }),
  getViewTableTargetKey(params),
  'a different schema must produce a different target key',
);
assert.notEqual(
  getViewTableTargetKey({ ...params, tableName: 'geo_test' }),
  getViewTableTargetKey(params),
  'a different table must produce a different target key',
);

// The database type only decides how a result is rendered, not which table is
// browsed, so it must not invalidate the loaded page.
assert.equal(
  getViewTableTargetKey({ ...params, databaseType: 'MYSQL' }),
  getViewTableTargetKey(params),
  'the database type must not change the target key',
);

// Adjacent fields keep their position because every key joins the same four
// fields, so a name containing the separator cannot shift into the next field.
assert.notEqual(
  getViewTableTargetKey({ dataSourceId: 1, databaseName: 'a', schemaName: 'b', tableName: 'c' }),
  getViewTableTargetKey({ dataSourceId: 1, databaseName: 'a\u0000b', tableName: 'c' }),
  'identity fields must not shift into each other',
);

// A missing field must occupy the same slot as an empty one, which the
// import-target refresh listener relies on when it compares event details.
assert.equal(
  getViewTableTargetKey({ dataSourceId: 1, tableName: 'orders' }),
  getViewTableTargetKey({ dataSourceId: 1, databaseName: '', schemaName: '', tableName: 'orders' }),
  'missing and empty identity fields must produce the same target key',
);

assert.equal(getViewTableTargetKey(undefined), '', 'a missing params object has no target key');
assert.equal(getViewTableTargetKey(null), '', 'a null params object has no target key');

console.log('view table target tests passed');
