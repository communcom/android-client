package io.golos.domain.use_cases.user

import io.golos.domain.dto.FollowerDomain

interface GetFollowersUseCase {

    /**
     * Get followers in size [pageSizeLimit] as next page with [offset]
     */
    suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain>
}