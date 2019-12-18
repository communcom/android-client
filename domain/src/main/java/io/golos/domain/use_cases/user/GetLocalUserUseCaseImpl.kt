package io.golos.domain.use_cases.user

import io.golos.domain.dto.UserDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class GetLocalUserUseCaseImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepositoryRead
): GetLocalUserUseCase {

    override suspend fun getLocalUser(): UserDomain{
        return UserDomain(currentUserRepository.userId, "", currentUserRepository.userAvatarUrl, null, null)
    }
}