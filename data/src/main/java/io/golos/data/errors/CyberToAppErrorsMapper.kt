package io.golos.data.errors

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
    override fun mapIfNeeded(e: Throwable): Throwable =
        sequenceOf(
            CannotDeleteDiscussionWithChildCommentsMapper(e),
            NotEnoughPowerMapper(e),
            RequestTimeOutMapper(e),
            NotFoundMapper(e),
            ForbiddenMapper(e),
            NameIsAlreadyInUseMapper(e),
            UnknownErrorMapper(e),
            SocketTimeoutExceptionMapper(e),
            TransparentMapper(e))
        .first { it.canMap() }
        .map()
}