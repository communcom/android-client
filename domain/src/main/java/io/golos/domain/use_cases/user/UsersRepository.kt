package io.golos.domain.use_cases.user

import io.golos.domain.dto.FollowerDomain

interface UsersRepository {

    /**
     * Get followers in size [pageSizeLimit] as next page with [offset]
     */
    suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain>

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