package io.golos.domain.use_cases.user

import io.golos.domain.dto.UserIdDomain
import javax.inject.Inject

class UnsubscribeToFollowerUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository): UnsubscribeToFollowerUseCase {

    override suspend fun unsubscribeToFollower(userId: String) {
        usersRepository.unsubscribeToFollower(UserIdDomain(userId))
    }
}