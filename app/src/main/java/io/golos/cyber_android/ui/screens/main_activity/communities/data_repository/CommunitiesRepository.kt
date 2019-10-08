package io.golos.cyber_android.ui.screens.main_activity.communities.data_repository

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityExt
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType

interface CommunitiesRepository {
    /**
     * @param top - how many communities to return
     * @param skip - how many communities to skip (if 0 - returns the very first page)
     */
    suspend fun getCommunities(top: Int, skip: Int, type: CommunityType): Either<List<CommunityExt>, Throwable>

    suspend fun joinToCommunity(externalId: String): Either<Unit, Throwable>

    /**
     * @param top - how many communities to return
     * @param skip - how many communities to skip (if 0 - returns the very first page)
     */
    suspend fun searchInCommunities(query: String, type: CommunityType): Either<List<CommunityExt>, Throwable>
}