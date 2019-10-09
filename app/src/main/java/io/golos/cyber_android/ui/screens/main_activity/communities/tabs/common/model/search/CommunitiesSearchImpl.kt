package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search

import io.golos.cyber_android.ui.common.search.SearchEngineBase
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Community
import io.golos.domain.extensions.getOrThrow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CommunitiesSearchImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    dispatchersProvider: DispatchersProvider,
    private val communityType: CommunityType
) : SearchEngineBase<Community>(dispatchersProvider), CommunitiesSearch {

    override suspend fun doSearch(searchString: String): List<Community>? {
        return if(searchString.length < 3) {
            return null
        } else {
            communitiesApi.searchInCommunities(searchString, communityType == CommunityType.USER).getOrThrow()
        }
    }
}