package io.golos.data.api.communities

import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.CommunityId

interface CommunitiesApi {
    /**
     * @param isUser is user's or discoverer communities
     */
    suspend fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): List<Community>

    suspend fun joinToCommunity(externalId: String)

    /**
     * @param isUser is user's or discoverer communities
     */
    suspend fun searchInCommunities(query: String, isUser: Boolean): List<Community>

    suspend fun getCommunityById(communityId: CommunityId): Community?
}