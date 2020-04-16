package io.golos.data.repositories.network_call

import io.golos.commun4j.Commun4j
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.commun4j.utils.StringSigner
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.data.mappers.mapToApiResponseErrorDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.UserKeyType
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.SocketTimeoutException
import javax.inject.Inject

class NetworkCallProxyImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j,
    private val keyValueStorage: KeyValueStorageFacade,
    private val userKeyStore: UserKeyStore
) : NetworkCallProxy {

    override suspend fun <TR>call(action: suspend () -> Either<TR, ApiResponseError>): TR = callInternal { action().getOrThrow() }

    override suspend fun <TR>callBC(action: suspend () -> Either<TR, GolosEosError>): TR = callInternal { action().getOrThrow() }

    private suspend fun <TR>callInternal(action: suspend () -> TR): TR =
        try {
            if(!networkStateChecker.isConnected) {
                throw SocketTimeoutException("No connection")
            }

            withContext(dispatchersProvider.ioDispatcher) {
                try {
                    action()
                } catch (ex: ApiResponseErrorException) {
                    // We can't use a code (1103) here - the value is used in  other cases too (for example, in SignUp scenario)
                    if(ex.errorInfo.message == "Unauthorized request: access denied") {
                        // Reconnect
                        val authSecret = commun4j.getAuthSecret().getOrThrow().secret
                        val userName = keyValueStorage.getAuthState()!!.userName
                        val activePrivateKey = userKeyStore.getKey(UserKeyType.ACTIVE)

                        commun4j.authWithSecret(userName, authSecret, StringSigner.signString(authSecret, activePrivateKey))

                        action()
                    } else {
                        throw ex
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }

    @JvmName("getOrThrowApiResponseError")
    private fun <S> Either<S, ApiResponseError>.getOrThrow(): S =
        (this as? Either.Success)?.value
            ?:
            throw ApiResponseErrorException((this as Either.Failure).value.mapToApiResponseErrorDomain())

    @JvmName("getOrThrowGolosEosError")
    private fun <S> Either<S, GolosEosError>.getOrThrow(): S =
        (this as? Either.Success)?.value
            ?:
            throw ApiResponseErrorException((this as Either.Failure).value.mapToApiResponseErrorDomain())
}