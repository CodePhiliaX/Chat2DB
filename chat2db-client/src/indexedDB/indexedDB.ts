import { tableList } from './table';

// 创建数据库的方法
export const createDB = (dbName: string, version: number) => {
  return new Promise((resolve, reject) => {
    const request = window.indexedDB.open(dbName, version);
    request.onerror = (event: any) => {
      reject(event.target.error);
    };
    request.onsuccess = (event: any) => {
      resolve(event.target.result);
    };
    request.onupgradeneeded = (event: any) => {
      const db = event.target.result; // 数据库对象
      // 创建存储库
      tableList.forEach((item: any) => {
        const { tableDetails } = item;
        const objectStore = db.createObjectStore(tableDetails.name, tableDetails.primaryKey);
        tableDetails.column.forEach((i: any) => {
          if (i.isIndex) {
            objectStore.createIndex(i.name, i.keyPath, i.options);
          }
        });
      });
    };
  });
};

// 添加数据
export const addData = (db: any, tableName: string, data: any) => {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(tableName, 'readwrite');
    const objectStore = transaction.objectStore(tableName);
    const request = objectStore.add(data);
    request.onsuccess = () => {
      resolve(true);
    };
    request.onerror = (error) => {
      reject(error);
    };
  });
};

// 删除数据
export const deleteData = (db: any, tableName: string, key: string) => {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(tableName, 'readwrite');
    const objectStore = transaction.objectStore(tableName);
    const request = objectStore.delete(key);
    request.onsuccess = () => {
      resolve(true);
    };
    request.onerror = () => {
      reject(false);
    };
  });
};

// 修改数据
export const updateData = (db: any, tableName: string, data: any) => {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(tableName, 'readwrite');
    const objectStore = transaction.objectStore(tableName);
    const request = objectStore.put(data);
    request.onsuccess = () => {
      resolve(true);
    };
    request.onerror = () => {
      reject(false);
    };
  });
};

// 查询数据
export const getData = (db: any, tableName: string, key: string) => {
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(tableName, 'readwrite');
    const objectStore = transaction.objectStore(tableName);
    const request = objectStore.get(key);
    request.onsuccess = () => {
      resolve(request.result);
    };
    request.onerror = () => {
      reject(false);
    };
  });
};

// 关闭数据库
export const closeDB = (db: any) => {
  return new Promise((resolve) => {
    db.close();
    resolve(true);
  });
};

export default {
  createDB,
  addData,
  deleteData,
  updateData,
  getData,
  closeDB,
};
