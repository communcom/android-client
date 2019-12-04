package io.golos.domain.repositories

import io.golos.domain.dto.AuthState

interface CurrentUserRepositoryRead {
    val authState: AuthState?

    val userId: String

    val userName: String

    val userAvatarUrl: String?
}

interface CurrentUserRepository : CurrentUserRepositoryRead {
    override var authState: AuthState?

    override var userAvatarUrl: String?
}