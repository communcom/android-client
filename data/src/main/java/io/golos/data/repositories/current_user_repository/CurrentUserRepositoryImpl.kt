package io.golos.data.repositories.current_user_repository

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

/**
 * Current logged in user
 */
@ApplicationScope
class CurrentUserRepositoryImpl
@Inject
constructor() : CurrentUserRepository, CurrentUserRepositoryRead {
    override var authState: AuthStateDomain? = null

    override val userId: UserIdDomain
        get() = authState!!.user

    override val userName: String
        get() = authState!!.userName

    override var userAvatarUrl: String? = null
}