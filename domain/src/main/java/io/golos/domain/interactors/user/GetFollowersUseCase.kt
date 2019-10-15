package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowersDomain

interface GetFollowersUseCase {

    /**
     * Get followers in size [pageSizeLimit] as next page with [sequenceKey]
     */
    suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): List<FollowersDomain>
}