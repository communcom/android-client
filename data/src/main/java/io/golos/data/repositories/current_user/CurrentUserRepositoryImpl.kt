package io.golos.data.repositories.current_user

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

/**
 * Current logged in user
 */
@ApplicationScope
class CurrentUserRepositoryImpl
@Inject
constructor() : CurrentUserRepository, CurrentUserRepositoryRead {

    private val avatarChannel: ConflatedBroadcastChannel<String?> = ConflatedBroadcastChannel(null)

    override var authState: AuthStateDomain? = null

    override val userId: UserIdDomain
        get() = authState!!.user

    override val userName: String
        get() = authState!!.userName

    override var userAvatarUrl: String? = null

    override suspend fun sendUserAvatarChanges() {
        avatarChannel.send(userAvatarUrl)
    }

    override val userAvatarFlow: Flow<String?> = avatarChannel.asFlow()
}