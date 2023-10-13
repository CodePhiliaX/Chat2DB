import React, { useEffect, useState } from 'react';
import indexedDB from '@/indexedDB/indexedDB';

const App: React.FC = () => {
  const [name] = useState<string>('');
  const [user, setUser] = useState<{ name: string; age: number }>({ name: '', age: 0 });
  const [db, setDb] = useState<any>();

  useEffect(() => {
    setUser({ name: 'jack', age: 18 });
    indexedDB.createDB('chat2db', 2).then((db) => {
      setDb(db);
    });
  }, []);

  const fuckUser = () => {
    const { name: _name, age } = user;
    // 一系列操作
    console.log(_name, age);
    return true;
  };

  const add = () => {
    indexedDB.addData(db, 'users', {
      id: 8,
      a: 1,
    });
  };

  return (
    <div>
      {name + fuckUser()}
      <button onClick={add}>add</button>
    </div>
  );
};

export default App;
