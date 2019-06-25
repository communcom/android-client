package io.golos.data.errors


/**
 * Class represents some error that can be showed to the app user. Example:
 * when (error) {
 *  is AppError.SomeError ->
 *      showSomeError()
 *  is AppError.OtherError ->
 *      showOtherError()
 *  else ->
 *      showUnknownError()
 * }
 */
sealed class AppError: Exception() {

    object ForbiddenError: AppError()

    object NameIsAlreadyInUseError: AppError()

    object RequestTimeOutException: AppError()

    object NotFoundError: AppError()

    object NotEnoughPowerError: AppError()

    object CannotDeleteDiscussionWithChildCommentsError: AppError()

    object NotPinnedError: AppError()

    object AlreadyPinnedError: AppError()

    class UnknownError(override val cause: CyberServicesError): AppError()  {
        override val message = cause.message
    }
}
