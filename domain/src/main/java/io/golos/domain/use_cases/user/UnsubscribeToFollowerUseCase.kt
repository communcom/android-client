package io.golos.domain.use_cases.user

interface UnsubscribeToFollowerUseCase {

    /**
     * Unsubscribe to follower
     *
     * @param userId user id
     */
    suspend fun unsubscribeToFollower(userId: String)
}