package io.golos.domain.use_cases.user

import javax.inject.Inject

class UnsubscribeToFollowerUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository): UnsubscribeToFollowerUseCase {

    override suspend fun unsubscribeToFollower(userId: String) {
        usersRepository.unsubscribeToFollower(userId)
    }
}