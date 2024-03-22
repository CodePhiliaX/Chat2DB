import { IAst } from './define';

export const compareIgnoreLowerCaseWhenString = (source: any, target: any) => {
  if (typeof source === 'string' && typeof target === 'string') {
    return source.toLowerCase() === target.toLowerCase();
  }
  return source === target;
};

export const binaryRecursionToArray = (ast: IAst[]) => {
  if (ast[1]) {
    return [ast[0]].concat(ast[1][1]);
  }

  return [ast[0]];
};

export function tailCallOptimize<T>(f: T): T {
  let value: any;
  let active = false;
  const accumulated: any[] = [];
  return function accumulator(this: any) {
    // eslint-disable-next-line prefer-rest-params
    accumulated.push(arguments);
    if (!active) {
      active = true;
      while (accumulated.length) {
        // eslint-disable-next-line babel/no-invalid-this
        value = (f as any).apply(this, accumulated.shift());
      }
      active = false;
      return value;
    }
  } as any;
}

export function getPathByCursorIndexFromAst(obj: any, cursorIndex: number, path?: string) {
  // eslint-disable-next-line no-param-reassign
  path = path || '';
  let fullpath = '';
  // eslint-disable-next-line guard-for-in
  for (const key in obj) {
    if (
      obj[key] &&
      obj[key].token === true &&
      obj[key].position[0] <= cursorIndex &&
      obj[key].position[1] + 1 >= cursorIndex
    ) {
      if (path === '') {
        return key;
      }
      return `${path}.${key}`;
    }
    if (typeof obj[key] === 'object') {
      fullpath = getPathByCursorIndexFromAst(obj[key], cursorIndex, path === '' ? key : `${path}.${key}`) || fullpath;
    }
  }
  return fullpath;
}
