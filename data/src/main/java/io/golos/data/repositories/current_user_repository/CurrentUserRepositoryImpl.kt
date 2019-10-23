package io.golos.data.repositories.current_user_repository

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.AuthState
import javax.inject.Inject

/**
 * Current logged in user
 */
@ApplicationScope
class CurrentUserRepositoryImpl
@Inject
constructor() : CurrentUserRepository, CurrentUserRepositoryRead {
    override var authState: AuthState? = null

    override val userId: String
        get() = authState!!.user.name
}