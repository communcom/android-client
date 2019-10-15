package io.golos.domain.interactors.user

import javax.inject.Inject

class SubscribeToFollowerUseCaseImpl @Inject constructor(private val userRepository: UsersRepository): SubscribeToFollowerUseCase {

    override suspend fun subscribeToFollower(userId: String) {
        userRepository.subscribeToFollower(userId)
    }
}