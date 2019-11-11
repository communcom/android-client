package io.golos.domain.use_cases.user

import javax.inject.Inject

class SubscribeToFollowerUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository): SubscribeToFollowerUseCase {

    override suspend fun subscribeToFollower(userId: String) {
        usersRepository.subscribeToFollower(userId)
    }
}