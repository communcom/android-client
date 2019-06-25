package io.golos.data.errors

import java.net.SocketTimeoutException

/**
 * Base class for all mappers
 */
abstract class ErrorMapper {
    abstract fun canMap(source: Throwable): Boolean

    abstract fun map(source: Throwable): Throwable
}

/**
 * Mapping by message text
 */
abstract class MessageTextMapper(
    private val messageText: String,
    private val result: Throwable
) : ErrorMapper() {
    override fun canMap(source: Throwable) = source is CyberServicesError && source.message?.contains(messageText, true) ?: false

    override fun map(source: Throwable) = result
}

class TransparentMapper : ErrorMapper() {
    override fun canMap(source: Throwable) = true

    override fun map(source: Throwable) = source
}

class SocketTimeoutExceptionMapper : ErrorMapper() {
    override fun canMap(source: Throwable) = source is SocketTimeoutException

    override fun map(source: Throwable) = AppError.RequestTimeOutException
}

class CannotDeleteDiscussionWithChildCommentsMapper :
    MessageTextMapper(
        "You can't delete comment with child comments",
        AppError.CannotDeleteDiscussionWithChildCommentsError)

class NotEnoughPowerMapper : MessageTextMapper("Not enough power", AppError.NotEnoughPowerError)

class RequestTimeOutMapper: MessageTextMapper("Request timeout", AppError.RequestTimeOutException)

class NotFoundMapper: MessageTextMapper("code=404", AppError.NotFoundError)

class ForbiddenMapper: MessageTextMapper("code=403", AppError.ForbiddenError)

class NameIsAlreadyInUseMapper : MessageTextMapper("Name is already in use", AppError.NameIsAlreadyInUseError)

class NotPinnedMapper: MessageTextMapper("You have not pinned this account", AppError.NotPinnedError)

class AlreadyPinnedMapper: MessageTextMapper("You already have pinned this account", AppError.AlreadyPinnedError)

class UnknownErrorMapper: ErrorMapper() {
    override fun canMap(source: Throwable) = source is CyberServicesError

    override fun map(source: Throwable) = AppError.UnknownError(source as CyberServicesError)
}