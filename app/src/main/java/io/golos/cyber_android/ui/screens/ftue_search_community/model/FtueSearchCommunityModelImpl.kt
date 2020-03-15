package io.golos.cyber_android.ui.screens.ftue_search_community.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.FtueBoardStageDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.domain.repositories.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FtueSearchCommunityModelImpl
@Inject constructor(
    private val repository: CommunitiesRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val usersRepository: UsersRepository,
    private val currentUserRepository: CurrentUserRepository
) : ModelBaseImpl(), FtueSearchCommunityModel {

    override fun saveCommunitySubscriptions(communitySubscriptions: List<CommunityDomain>) {
        repository.saveCommunitySubscriptions(communitySubscriptions)
    }

    override suspend fun getCommunitySubscriptions(): List<CommunityDomain> = repository.getCommunitySubscriptions()

    override suspend fun setFtueBoardStage(stage: FtueBoardStageDomain) {
        usersRepository.setFtueBoardStage(stage)
    }

    override suspend fun sendCommunitiesCollection(communityCodes: List<String>) {
        withContext(dispatchersProvider.ioDispatcher) {
            repository.sendCommunitiesCollection(communityCodes)
        }
    }

    override suspend fun getCommunities(query: String?, offset: Int, pageSize: Int): List<CommunityDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            repository.getCommunitiesList(currentUserRepository.userId, offset, pageSize, true, query)
        }
    }

    override suspend fun onFollowToCommunity(communityId: CommunityIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            repository.subscribeToCommunity(communityId)
        }
    }

    override suspend fun onUnFollowFromCommunity(communityId: CommunityIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            repository.unsubscribeToCommunity(communityId)
        }
    }
}