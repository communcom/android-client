package io.golos.cyber_android.ui.screens.profile_communities.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunityListItem
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.utils.id.IdUtil
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProfileCommunitiesModelImpl
@Inject
constructor(
    private val appContext: Context,
    private val sourceData: ProfileCommunities,
    private val dispatchersProvider: DispatchersProvider,
    private val communitiesRepository: CommunitiesRepository
) : ModelBaseImpl(),
    ProfileCommunitiesModel {

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(loadedItems)
    override val items: LiveData<List<VersionedListItem>>
        get() = _items

    private var subscribingInProgress = false

    override fun loadPage() = updateData {
        loadedItems.addAll(sourceData.highlightCommunities.map { it.mapToListItem() })
    }

    /**
     * @return true in case of success
     */
    override suspend fun subscribeUnsubscribe(communityId: CommunityIdDomain): ErrorInfoDomain? {
        if(subscribingInProgress) {
            return null
        }

        subscribingInProgress = true

        val community = loadedItems[getCommunityIndex(communityId)] as CommunityListItem

        setCommunityInProgress(communityId)

        val errorInfo = withContext(dispatchersProvider.ioDispatcher) {
            try {
                if(community.isJoined) {
                    communitiesRepository.unsubscribeToCommunity(communityId)
                } else {
                    communitiesRepository.subscribeToCommunity(communityId)
                }
                null
            } catch (ex: Exception) {
                Timber.e(ex)
                ErrorInfoDomain(ex.getMessage(appContext))
            }
        }

        completeCommunityInProgress(communityId, errorInfo == null)

        subscribingInProgress = false

        return errorInfo
    }

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }

    private fun Community.mapToListItem(): VersionedListItem =
        CommunityListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            community = this,
            isJoined = this.isSubscribed,
            isProgress = false
    )

    private fun getCommunityIndex(communityId: CommunityIdDomain) =
        loadedItems.indexOfFirst { it is CommunityListItem && it.community.communityId == communityId }

    private fun updateCommunity(communityId: CommunityIdDomain, updateAction: (CommunityListItem) -> CommunityListItem) = updateData {
        val itemIndex = getCommunityIndex(communityId)
        loadedItems[itemIndex] = updateAction(loadedItems[itemIndex] as CommunityListItem)
    }

    private fun setCommunityInProgress(communityId: CommunityIdDomain) =
        updateCommunity(communityId) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = true,
                isJoined = !oldCommunity.isJoined)
        }

    private fun completeCommunityInProgress(communityId: CommunityIdDomain, isSuccess: Boolean) =
        updateCommunity(communityId) { oldCommunity ->
            oldCommunity.copy(
                version = oldCommunity.version + 1,
                isProgress = false,
                isJoined = if(isSuccess) oldCommunity.isJoined else !oldCommunity.isJoined
            )
        }
}