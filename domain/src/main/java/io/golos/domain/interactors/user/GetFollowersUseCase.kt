package io.golos.domain.interactors.user

import io.golos.domain.entities.FollowerDomain

interface GetFollowersUseCase {

    /**
     * Get followers in size [pageSizeLimit] as next page with [offset]
     */
    suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain>
}