package io.golos.data.errors

import javax.inject.Inject

/**
 * Interface map [CyberServicesError] to a specific [AppError], which can be used in app
 */
interface CyberToAppErrorMapper {
    /**
     * Maps [e] to a some [AppError] if [e] is [CyberServicesError], otherwise just returns [e] itself
     */
    fun mapIfNeeded(e: Throwable): Throwable
}

class CyberToAppErrorMapperImpl
@Inject
constructor() : CyberToAppErrorMapper {
    private val handlersList: List<ErrorMapper> by lazy {
        listOf(
            CannotDeleteDiscussionWithChildCommentsMapper(),
            NotEnoughPowerMapper(),
            RequestTimeOutMapper(),
            NotFoundMapper(),
            ForbiddenMapper(),
            NameIsAlreadyInUseMapper(),
            UserNameIsAlreadyTakenMapper(),
            NotPinnedMapper(),
            AlreadyPinnedMapper(),
            CashoutWindowMapper(),
            UnknownErrorMapper(),
            SocketTimeoutExceptionMapper(),
            TransparentMapper())
    }

    override fun mapIfNeeded(e: Throwable): Throwable = handlersList.first { it.canMap(e) }.map(e)
}