package io.golos.domain.interactors.user

interface SubscribeToFollowerUseCase {

    /**
     * Subscribe to follower
     *
     * @param userId user id
     */
    suspend fun subscribeToFollower(userId: String)
}