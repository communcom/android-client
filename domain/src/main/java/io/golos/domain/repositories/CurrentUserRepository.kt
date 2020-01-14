package io.golos.domain.repositories

import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.coroutines.flow.Flow

interface CurrentUserRepositoryRead {
    val authState: AuthStateDomain?

    val userId: UserIdDomain

    val userName: String

    val userAvatarUrl: String?

    val userAvatarFlow: Flow<String?>
}

interface CurrentUserRepository : CurrentUserRepositoryRead {
    override var authState: AuthStateDomain?

    override var userAvatarUrl: String?

    suspend fun sendUserAvatarChanges()
}