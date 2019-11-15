package io.golos.domain.repositories

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.AuthState

interface CurrentUserRepositoryRead {
    val authState: AuthState?

    val userId: String

    val userAvatarUrl: String?

    val user: CyberName
}

interface CurrentUserRepository : CurrentUserRepositoryRead {
    override var authState: AuthState?

    override var userAvatarUrl: String?
}