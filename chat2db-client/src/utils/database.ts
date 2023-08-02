import { IWorkspaceModelType } from '@/models/workspace';
import { Option } from '@/typings/common';

export function handleDatabaseAndSchema(databaseAndSchema: IWorkspaceModelType['state']['databaseAndSchema']) {
  let newCascaderOptions: Option[] = [];
  if (databaseAndSchema.databases) {
    newCascaderOptions = (databaseAndSchema?.databases || []).map((t) => {
      let schemasList: Option[] = [];
      if (t.schemas) {
        schemasList = t.schemas.map((t) => {
          return {
            value: t.name,
            label: t.name,
            type: 'schema',
            isLeaf: true,
          };
        });
      }
      return {
        value: t.name,
        label: t.name,
        type: 'database',
        children: schemasList,
        isLeaf: schemasList.length === 0,
      };
    });
  } else if (databaseAndSchema?.schemas) {
    newCascaderOptions = (databaseAndSchema?.schemas || []).map((t) => {
      return {
        value: t.name,
        label: t.name,
        type: 'schema',
        isLeaf: true,
      };
    });
  }
  return newCascaderOptions;
}
