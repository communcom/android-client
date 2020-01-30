package io.golos.data

import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import kotlinx.coroutines.flow.Flow

interface ServerMessageReceiver: RpcServerMessageCallback {

    data class Message(val method: String, val data: Any)

    fun messagesFlow(): Flow<Message>
}