package io.golos.data.errors

import io.golos.data.errors.AppError.CannotDeleteDiscussionWithChildCommentsError
import java.net.SocketTimeoutException

/**
 * Interface map [CyberServicesError] to a specific [AppError], which can be used in app
 */
interface CyberToAppErrorMapper {
    /**
     * Maps [e] to a some [AppError] if [e] is [CyberServicesError], otherwise just returns [e] itself
     */
    fun mapIfNeeded(e: Throwable): Throwable
}

class CyberToAppErrorMapperImpl : CyberToAppErrorMapper {
    private fun map(e: CyberServicesError): AppError {
        return when {
            e.message?.contains("You can't delete comment with child comments", true) == true ->
                CannotDeleteDiscussionWithChildCommentsError
            e.message?.contains("Not enough power", true) == true ->
                AppError.NotEnoughPowerError
            e.message?.contains("Request timeout", true) == true ->
                AppError.RequestTimeOutException
            e.message?.contains("code=404", true) == true ->
                AppError.NotFoundError
            e.message?.contains("code=403", true) == true ->
                AppError.ForbiddenError
            e.message?.contains("Name is already in use", true) == true ->
                AppError.NameIsAlreadyInUseError


            else -> AppError.UnknownError(e)
        }
    }

    override fun mapIfNeeded(e: Throwable) = when (e) {
        is CyberServicesError -> this.map(e)
        is SocketTimeoutException -> AppError.RequestTimeOutException
        else -> e
    }
}