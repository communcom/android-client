package io.golos.data.repositories.current_user_repository

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.AuthState
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
    override var authState: AuthState? = null

    override val userId: String
        get() = authState!!.user.name

    override var userAvatarUrl: String? = null

    override val user: CyberName
        get() = authState!!.user
}