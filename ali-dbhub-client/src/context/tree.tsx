// import React, { memo, useEffect, useState, useRef, createContext } from 'react';

// export type ICreateConsoleDialog = false | {
//   dataSourceId: number;
//   databaseName: string;
// }


// export interface IModel {
//   createConsoleDialog: ICreateConsoleDialog;
// }

// export interface IContext {
//   model: IModel;
//   setCreateConsoleDialog: (value: ICreateConsoleDialog) => void;
// }

// const initDatabaseValue: IModel = {
//   createConsoleDialog: false,
// }

// export const TreeContext = createContext<IContext>({} as any);

// export default function DatabaseContextProvider({ children }: { children: React.ReactNode }) {
//   const [model, setStateModel] = useState<IModel>(initDatabaseValue);

//   const setCreateConsoleDialog = (createConsoleDialog: ICreateConsoleDialog) => {
//     setStateModel({
//       ...model,
//       createConsoleDialog
//     })
//   }
//   const setOperationDataDialog = (operationData: IOperationDataDialog) => {
//     setStateModel({
//       ...model,
//       operationData
//     })
//   }

//   return <TreeContext.Provider value={{
//     model,
//     setCreateConsoleDialog,
//   }}>
//     {children}
//   </TreeContext.Provider>
// }