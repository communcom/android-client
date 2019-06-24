package io.golos.data.errors

import java.net.SocketTimeoutException

/**
 * Base class for all mappers
 */
abstract class ErrorMapper(protected val source: Throwable) {
    abstract fun canMap(): Boolean

    abstract fun map(): Throwable
}

/**
 * Mapping by message text
 */
abstract class MessageTextMapper(
    source: Throwable,
    private val messageText: String,
    private val result: Throwable
) : ErrorMapper(source) {
    override fun canMap() = source is CyberServicesError && source.message?.contains(messageText, true) ?: false

    override fun map() = result
}

class TransparentMapper(source: Throwable): ErrorMapper(source) {
    override fun canMap() = true

    override fun map() = source
}

class SocketTimeoutExceptionMapper(source: Throwable): ErrorMapper(source) {
    override fun canMap() = source is SocketTimeoutException

    override fun map() = AppError.RequestTimeOutException
}

class CannotDeleteDiscussionWithChildCommentsMapper(source: Throwable):
    MessageTextMapper(
        source,
        "You can't delete comment with child comments",
        AppError.CannotDeleteDiscussionWithChildCommentsError)

class NotEnoughPowerMapper(source: Throwable): MessageTextMapper(source, "Not enough power", AppError.NotEnoughPowerError)

class RequestTimeOutMapper(source: Throwable): MessageTextMapper(source, "Request timeout", AppError.RequestTimeOutException)

class NotFoundMapper(source: Throwable): MessageTextMapper(source, "code=404", AppError.NotFoundError)

class ForbiddenMapper(source: Throwable): MessageTextMapper(source, "code=403", AppError.ForbiddenError)

class NameIsAlreadyInUseMapper(source: Throwable):
    MessageTextMapper(
        source,
        "Name is already in use",
        AppError.NameIsAlreadyInUseError)

class UnknownErrorMapper(source: Throwable): ErrorMapper(source) {
    override fun canMap() = source is CyberServicesError

    override fun map() = AppError.UnknownError(source as CyberServicesError)
}