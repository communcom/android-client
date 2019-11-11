package io.golos.domain.use_cases.user

interface SubscribeToFollowerUseCase {

    /**
     * Subscribe to follower
     *
     * @param userId user id
     */
    suspend fun subscribeToFollower(userId: String)
}