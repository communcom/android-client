package io.golos.domain.interactors.user

interface UnsubscribeToFollowerUseCase {

    /**
     * Unsubscribe to follower
     *
     * @param userId user id
     */
    suspend fun unsubscribeToFollower(userId: String)
}