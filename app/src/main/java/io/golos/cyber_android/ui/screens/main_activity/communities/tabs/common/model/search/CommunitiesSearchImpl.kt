package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search

import io.golos.cyber_android.ui.common.search.SearchEngineBase
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.CommunitiesRepository
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityExt
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.getOrThrow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CommunitiesSearchImpl
@Inject
constructor(
    private val communitiesRepository: CommunitiesRepository,
    dispatchersProvider: DispatchersProvider,
    private val communityType: CommunityType
) : SearchEngineBase<CommunityExt>(dispatchersProvider), CommunitiesSearch {

    override suspend fun doSearch(searchString: String): List<CommunityExt>? {
        return if(searchString.length < 3) {
            return null
        } else {
            communitiesRepository.searchInCommunities(searchString, communityType).getOrThrow()
        }
    }
}