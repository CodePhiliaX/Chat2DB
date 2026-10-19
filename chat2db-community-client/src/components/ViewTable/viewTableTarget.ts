import type { IViewTableParams } from '@/typings';

// The four fields below are always joined with fixed arity, so no field can
// shift into another one; the separator only has to be a character that cannot
// appear inside a datasource, database, schema, or table name.
const TARGET_KEY_SEPARATOR = '\u0000';

/**
 * Identity of the table a browse request targets.
 *
 * The workspace tab layer keeps every open tab mounted and rebuilds the tab
 * bodies whenever unrelated workspace state changes (active tab, datasource
 * list, tab list), so `IViewTableParams` is a new object on most parent
 * renders. Loading must be keyed on this identity instead of the params object
 * reference, otherwise every tab switch re-issues the table browse request.
 *
 * Paging fields are deliberately excluded: an open tab drives paging through
 * `onResultPagingChange`. A caller that wants `viewTableParams.pageNo` itself to
 * trigger a new load has to supply its own dependency.
 */
export function getViewTableTargetKey(params?: IViewTableParams | null): string {
  if (!params) {
    return '';
  }
  return [
    params.dataSourceId ?? '',
    params.databaseName ?? '',
    params.schemaName ?? '',
    params.tableName ?? '',
  ].join(TARGET_KEY_SEPARATOR);
}
