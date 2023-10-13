import React, { useEffect, useState } from 'react';

const App: React.FC = () => {
  const [name] = useState<string>('');
  const [user, setUser] = useState<{ name: string; age: number }>({ name: '', age: 0 });

  useEffect(() => {
    setUser({ name: 'jack', age: 18 });
  }, []);

  const fuckUser = () => {
    const { name: _name, age } = user;
    // 一系列操作
    console.log(_name, age);
    return true;
  };
  return name + fuckUser();
};

export default App;
