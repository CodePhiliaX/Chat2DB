/**
 * Deeply compares two values to determine if they are equivalent.
 * @param a The first value to compare.
 * @param b The second value to compare.
 * @returns True if the values are equivalent, false otherwise.
 */
export function deepEqual(a: any, b: any): boolean {
  // Check if the values are identical - covers primitives and same object references
  if (a === b) return true;

  // If one of them is null/undefined but the other isn't, they aren't equal
  if (a == null || b == null || typeof a !== typeof b) return false;

  // If they are functions, compare them as strings (assuming they are deterministic)
  if (typeof a === 'function' && typeof b === 'function') {
    return a.toString() === b.toString();
  }

  // If they are arrays, compare their elements
  if (Array.isArray(a) && Array.isArray(b)) {
    if (a.length !== b.length) return false;
    for (let i = 0; i < a.length; i++) {
      if (!deepEqual(a[i], b[i])) return false;
    }
    return true;
  }

  // If they are objects, compare their keys and values
  if (a instanceof Object && b instanceof Object) {
    const aKeys = Object.keys(a);
    const bKeys = Object.keys(b);

    if (aKeys.length !== bKeys.length) return false;

    for (const key of aKeys) {
      if (!b.hasOwnProperty(key)) return false;
      if (!deepEqual(a[key], b[key])) return false;
    }

    return true;
  }

  // If none of the above conditions match, the values are not equal
  return false;
}
