package io.golos.domain.repositories

import io.golos.domain.dto.AuthState
import io.golos.domain.dto.UserIdDomain

interface CurrentUserRepositoryRead {
    val authState: AuthState?

    val userId: UserIdDomain

    val userName: String

    val userAvatarUrl: String?
}

interface CurrentUserRepository : CurrentUserRepositoryRead {
    override var authState: AuthState?

    override var userAvatarUrl: String?
}