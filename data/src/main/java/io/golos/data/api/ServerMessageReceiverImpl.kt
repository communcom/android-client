package io.golos.data.api

import io.golos.commun4j.http.rpc.RpcServerMessage
import io.golos.commun4j.http.rpc.RpcServerMessageCallback
import io.golos.data.ServerMessageReceiver
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ApplicationScope
class ServerMessageReceiverImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider
) : RpcServerMessageCallback,
    ServerMessageReceiver,
    CoroutineScope {

    override val coroutineContext: CoroutineContext = dispatchersProvider.ioDispatcher

    private val messagesBroadcastChannel = BroadcastChannel<ServerMessageReceiver.Message>(1)

    override fun messagesFlow(): Flow<ServerMessageReceiver.Message> = messagesBroadcastChannel.asFlow()


    override fun onMessage(message: RpcServerMessage) {
        launch {
            try {
                messagesBroadcastChannel.send(ServerMessageReceiver.Message(message.method, message.params))
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }
}