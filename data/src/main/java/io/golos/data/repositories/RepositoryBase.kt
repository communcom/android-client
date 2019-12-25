package io.golos.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.data.mappers.mapToApiResponseErrorDomain
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.SocketTimeoutException

abstract class RepositoryBase(
    private val appContext: Context,
    private val dispatchersProvider: DispatchersProvider
) {
    private val isConnected: Boolean
        get() {
            val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo?.isConnected ?: false
        }

    protected suspend fun <TR>apiCall(action: suspend () -> Either<TR, ApiResponseError>): TR =
        try {
            if(!isConnected) {
                throw SocketTimeoutException("No connection")
            }

            withContext(dispatchersProvider.ioDispatcher) {
                action().getOrThrow()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }

    protected suspend fun <TR>apiCallChain(action: suspend () -> Either<TR, GolosEosError>): TR =
        try {
            if(!isConnected) {
                throw SocketTimeoutException("No connection")
            }

            withContext(dispatchersProvider.ioDispatcher) {
                action().getOrThrow()
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