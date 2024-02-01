import { isObj } from './check';

/**
 * Recursively merges two objects, including nested structures.
 * @param target The target object.
 * @param source The source object, which properties will be merged into the target.
 * @param mergeArrays A boolean flag to indicate whether to merge or overwrite arrays.
 * @returns The merged object.
 */
export function deepMerge<T>(target: T, source: T, mergeArrays = false) {
  let output = Array.isArray(target) ? target.slice() : { ...target };

  if (isObj(target) && isObj(source)) {
    Object.keys(source).forEach((key) => {
      if (isObj(source[key])) {
        if (!(key in target)) {
          output[key] = source[key];
        } else {
          output[key] = deepMerge(target[key], source[key], mergeArrays);
        }
      } else if (Array.isArray(source[key])) {
        if (Array.isArray(target[key])) {
          output[key] = mergeArrays ? target[key].concat(source[key]) : source[key].slice();
        } else {
          output[key] = source[key].slice();
        }
      } else {
        output[key] = source[key];
      }
    });
  } else if (Array.isArray(target) && Array.isArray(source)) {
    output = mergeArrays ? target.concat(source) : source.slice();
  }
  return output as T;
}
