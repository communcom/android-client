package io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.communities

import android.content.Context
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.ListWorkerBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.utils.id.MurmurHash
import javax.inject.Inject
import javax.inject.Named

class ListWorkerCommunitiesImpl
@Inject
constructor(
    private val appContext: Context,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val communitiesRepository: CommunitiesRepository,
    private val currentUserRepository: CurrentUserRepository
) : ListWorkerBaseImpl<CommunityIdDomain, CommunityListItem>(
    appContext,
    pageSize
), ListWorkerCommunities {

    override fun isItemInPositiveState(item: CommunityListItem): Boolean = item.isInPositiveState

    override suspend fun moveItemToPositiveState(id: CommunityIdDomain) = communitiesRepository.moveCommunityToBlackList(id)

    override suspend fun moveItemToNegativeState(id: CommunityIdDomain) = communitiesRepository.moveCommunityFromBlackList(id)

    override fun setItemInProgress(id: CommunityIdDomain) =
        updateItem(id) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = true,
                isInPositiveState = !oldCommunity.isInPositiveState)
        }


    override fun completeItemInProgress(id: CommunityIdDomain, isSuccess: Boolean) =
        updateItem(id) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = false,
                isInPositiveState = if(isSuccess) oldCommunity.isInPositiveState else !oldCommunity.isInPositiveState
            )
        }

    override fun getItemIndex(id: CommunityIdDomain): Int =
        loadedItems.indexOfFirst { it is CommunityListItem && it.community.communityId == id }

    override suspend fun getPage(offset: Int): List<CommunityListItem> =
        communitiesRepository
            .getCommunitiesInBlackList(offset, pageSize, currentUserRepository.userId)
            .map { rawItem -> rawItem.map() }

    private fun CommunityDomain.map() =
        CommunityListItem(
            id = MurmurHash.hash64(this.communityId.code),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            community = this,
            isInPositiveState = true,
            isProgress = false
        )

    override fun markAsFirst(item: VersionedListItem): VersionedListItem = (item as CommunityListItem).copy(isFirstItem = true)

    override fun markAsLast(item: VersionedListItem): VersionedListItem = (item as CommunityListItem).copy(isLastItem = true)

    override fun unMarkAsLast(item: VersionedListItem): VersionedListItem = (item as CommunityListItem).copy(isLastItem = false)
}
