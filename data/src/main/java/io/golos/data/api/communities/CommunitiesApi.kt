package io.golos.data.api.communities

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.commun_entities.Community

interface CommunitiesApi {
    /**
     * @param isUser is user's or discoverer communities
     */
    suspend fun getCommunitiesList(offset: Int, pageSize: Int, isUser: Boolean): Either<List<Community>, Throwable>

    suspend fun joinToCommunity(externalId: String): Either<Unit, Throwable>

    /**
     * @param isUser is user's or discoverer communities
     */
    suspend fun searchInCommunities(query: String, isUser: Boolean): Either<List<Community>, Throwable>
}