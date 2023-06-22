export function isOkay(obj: any) {
  return obj != null;
}

export function flattenAll(arr: any[]) {
  return arr
    .filter(part => {
      return isOkay(part);
    })
    .reduce((prev, cur) => {
      return prev.concat(cur);
    }, []);
}

export function createTableName(tableNames: { [key: string]: string }) {
  const returnValue: any = {
    type: 'identifier',
    variant: 'tableName',
  };

  Object.keys(tableNames).forEach(eachTableNamesKey => {
    returnValue[eachTableNamesKey] = tableNames[eachTableNamesKey];
  });

  returnValue.tableNames = Object.keys(tableNames);

  return returnValue;
}
