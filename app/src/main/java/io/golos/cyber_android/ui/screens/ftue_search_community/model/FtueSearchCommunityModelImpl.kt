package io.golos.cyber_android.ui.screens.ftue_search_community.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FtueSearchCommunityModelImpl
@Inject constructor(
    private val repository: CommunitiesRepository,
    private val dispatchersProvider: DispatchersProvider
) : ModelBaseImpl(), FtueSearchCommunityModel {

    override suspend fun getCommunities(offset: Int, pageCount: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            repository.getCommunitiesList(offset, pageCount, false)
        }
    }

    override suspend fun onFollowToCommunity(communityId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            repository.subscribeToCommunity(communityId)
        }
    }

    override suspend fun onUnFollowFromCommunity(communityId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            repository.unsubscribeToCommunity(communityId)
        }
    }
}