package io.golos.domain.use_cases.user

import io.golos.domain.dto.UserIdDomain
import javax.inject.Inject

class SubscribeToFollowerUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository): SubscribeToFollowerUseCase {

    override suspend fun subscribeToFollower(userId: String) {
        usersRepository.subscribeToFollower(UserIdDomain(userId))
    }
}