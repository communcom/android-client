package io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search

import io.golos.cyber_android.ui.shared.search.SearchEngineBase
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CommunitiesSearchImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    dispatchersProvider: DispatchersProvider
) : SearchEngineBase<CommunityDomain>(dispatchersProvider), CommunitiesSearch {

    override suspend fun doSearch(searchString: String): List<CommunityDomain>? {
        return if(searchString.length < 3) {
            return null
        } else {
            communitiesApi.searchInCommunities(searchString, true)
        }
    }
}