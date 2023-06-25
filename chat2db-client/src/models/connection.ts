import { IConnectionDetails } from "@/typings/connection"

export interface ConnectionState {
    curConnection: IConnectionDetails
    connectionList: IConnectionDetails[]
}


export default {
    namespace: 'connection',
    state: {
        curConnection: null,
        connectionList: [],
    },

    reducers: {
        // 获取连接池列表
        setConnectionList(state: ConnectionState, { payload }: { payload: ConnectionState['connectionList'] }) {
            return {
                ...state,
                connectionList: payload
            }
        },
        
        // 设置当前选着的Connection
        setCurConnection(
            state: ConnectionState,
            { payload }: { payload: ConnectionState['curConnection'] },
        ) {
            return { ...state, curConnection: payload }
        },



    }
}