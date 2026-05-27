import { IConnectionListItem } from '@/typings/connection';

export type ConnectionSortOrder = 'asc' | 'desc';
export type ConnectionSortMode = 'manual' | ConnectionSortOrder;

export interface IConnectionGroup {
  key: string;
  environment: IConnectionListItem['environment'] | null;
  connections: IConnectionListItem[];
}

const collator = new Intl.Collator(undefined, {
  numeric: true,
  sensitivity: 'base',
});

const getEnvironmentName = (connection: IConnectionListItem) => {
  return connection.environment?.name || '';
};

const compareConnection = (sortOrder: ConnectionSortOrder) => {
  return (prev: IConnectionListItem, next: IConnectionListItem) => {
    const environmentCompare = collator.compare(getEnvironmentName(prev), getEnvironmentName(next));
    const aliasCompare = collator.compare(prev.alias || '', next.alias || '');
    const idCompare = Number(prev.id || 0) - Number(next.id || 0);
    const result = environmentCompare || aliasCompare || idCompare;

    return sortOrder === 'asc' ? result : -result;
  };
};

export const sortConnectionList = (
  connectionList: IConnectionListItem[] = [],
  sortMode: ConnectionSortMode = 'manual',
) => {
  if (sortMode === 'manual') {
    // The backend already returns the current user's persisted manual order.
    return [...connectionList];
  }

  return [...connectionList].sort(compareConnection(sortMode));
};

export const groupConnectionList = (
  connectionList: IConnectionListItem[] = [],
  sortMode: ConnectionSortMode = 'manual',
) => {
  const groupMap = new Map<string, IConnectionGroup>();

  sortConnectionList(connectionList, sortMode).forEach((connection) => {
    const environment = connection.environment || null;
    const key = environment?.id ? String(environment.id) : environment?.name || 'unknown';
    const group = groupMap.get(key);

    if (group) {
      group.connections.push(connection);
      return;
    }

    groupMap.set(key, {
      key,
      environment,
      connections: [connection],
    });
  });

  return Array.from(groupMap.values());
};
