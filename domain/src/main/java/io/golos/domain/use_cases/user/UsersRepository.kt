package io.golos.domain.use_cases.user

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.FollowerDomain
import io.golos.domain.dto.UserProfileDomain

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

    suspend fun getUserProfile(user: CyberName): UserProfileDomain
}