package io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.communities

import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.ListWorkerBaseImpl
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.domain.utils.MurmurHash
import javax.inject.Inject
import javax.inject.Named

class ListWorkerCommunitiesImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val communitiesRepository: CommunitiesRepository,
    private val currentUserRepository: CurrentUserRepository
) : ListWorkerBaseImpl<String, CommunityListItem>(
    pageSize,
    currentUserRepository
), ListWorkerCommunities {

    override fun isItemInPositiveState(item: CommunityListItem): Boolean = item.isInPositiveState

    override suspend fun moveItemToPositiveState(id: String) = communitiesRepository.moveCommunityToBlackList(id)

    override suspend fun moveItemToNegativeState(id: String) = communitiesRepository.moveCommunityFromBlackList(id)

    override fun setItemInProgress(id: String) =
        updateItem(id) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = true,
                isInPositiveState = !oldCommunity.isInPositiveState)
        }


    override fun completeItemInProgress(id: String, isSuccess: Boolean) =
        updateItem(id) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = false,
                isInPositiveState = if(isSuccess) oldCommunity.isInPositiveState else !oldCommunity.isInPositiveState
            )
        }

    override fun getItemIndex(id: String): Int =
        loadedItems.indexOfFirst { it is CommunityListItem && it.community.communityId == id }

    override suspend fun getPage(offset: Int): List<CommunityListItem> =
        communitiesRepository
            .getCommunitiesInBlackList(offset, pageSize, currentUserRepository.userId)
            .map { rawItem -> rawItem.map() }

    private fun CommunityDomain.map() =
        CommunityListItem(
            id = MurmurHash.hash64(this.communityId),
            version = 0,
            community = this,
            isInPositiveState = true,
            isProgress = false
        )
}
