package io.golos.domain.interactors.user

import javax.inject.Inject

class SubscribeToFollowerUseCaseImpl @Inject constructor(private val usersRepository: UsersRepository): SubscribeToFollowerUseCase {

    override suspend fun subscribeToFollower(userId: String) {
        usersRepository.subscribeToFollower(userId)
    }
}