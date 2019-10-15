package io.golos.data.api.user

import io.golos.domain.entities.FollowersPageDomain

interface UserApi {

    /**
     * Get followers in size [pageSizeLimit] as next page with [sequenceKey]
     */
    suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain

    /**
     * Subscribe to follower
     *
     * @param userId user id
     */
    suspend fun subscribeToFollower(userId: String)

    /**
     * Unsubscribe to follower
     *
     * @param userId user id
     */
    suspend fun unsubscribeToFollower(userId: String)
}