export interface IDemoStore {
  student: {
    name: 'James',
    age: 18,
  }
  setDemoData: (student: Partial<IDemoStore['student']>) => void
}


export const demoStore = (set): IDemoStore => ({
  student: {
    name: 'James',
    age: 18,
  },
  setDemoData: (student) => set((state) => ({
    student: {
      ...state.student,
      ...student,
    },
  })),
});
