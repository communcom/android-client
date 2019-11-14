package io.golos.data.repositories

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.data.mappers.mapToApiResponseErrorDomain
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class RepositoryBase(private val dispatchersProvider: DispatchersProvider) {
    protected suspend fun <TR>apiCall(action: suspend () -> Either<TR, ApiResponseError>): TR =
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                action().getOrThrow()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }

    private fun <S> Either<S, ApiResponseError>.getOrThrow(): S =
        (this as? Either.Success)?.value
        ?:
        throw ApiResponseErrorException((this as Either.Failure).value.mapToApiResponseErrorDomain())
}