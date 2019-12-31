package io.golos.data.api

import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.Either
import io.golos.data.errors.CyberServicesError
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.dto.AuthStateDomain

abstract class Commun4jApiBase
constructor(
    protected val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepositoryRead
) {
    protected val authState: AuthStateDomain
        get() = currentUserRepository.authState!!

    protected fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)
}