import React, { useEffect, useState } from 'react';

export enum CustomerTypeEnum {
  visitor = 'visitor',
  person = 'person',
}

export enum CustomerTypeEnum2 {
  visitor = 'visitor',
  person = 'person',
}

export type CustomerType1 = CustomerTypeEnum | CustomerTypeEnum2;

const App: React.FC = () => {


  return (
    <div>demo</div>
  );
};

export default App;
