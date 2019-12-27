package io.golos.cyber_android.ui.screens.community_page_leaders_list.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.EmptyListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.LeaderListIem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.RetryListItem
import io.golos.data.repositories.CommunitiesRepositoryImpl
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityLeaderDomain
import io.golos.domain.utils.IdUtil
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LeadsListModelImpl
@Inject
constructor(
    @Named(Clarification.COMMUNITY_ID)
    private val communityId: String,
    private val communitiesRepository: CommunitiesRepositoryImpl
) : ModelBaseImpl(),
    LeadsListModel {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(listOf())
    override val items: LiveData<List<VersionedListItem>>
        get() = _items

    override suspend fun loadLeaders() {
        if (currentLoadingState == LoadingState.READY_TO_LOAD) {
            loadData(false)
        }
    }

    override suspend fun retry() {
        if (currentLoadingState == LoadingState.IN_ERROR) {
            loadData(true)
        }
    }

    private suspend fun loadData(isRetry: Boolean) {
        currentLoadingState = LoadingState.LOADING

        if(isRetry) {
            updateData { it[0] = LoadingListItem(IdUtil.generateLongId(), 0) }
        } else {
            updateData { it.add(LoadingListItem(IdUtil.generateLongId(), 0)) }
        }

        var data = getData()

        currentLoadingState = if(data != null) {
           if(data.isEmpty()) {
               data = listOf(EmptyListItem(IdUtil.generateLongId(), 0))
           }

            updateData {
                it.removeAt(0)
                it.addAll(data)
            }

            LoadingState.ALL_DATA_LOADED
        } else {
            updateData { it[0] = RetryListItem(IdUtil.generateLongId(), 0) }
            LoadingState.IN_ERROR
        }
    }

    private suspend fun getData(): List<VersionedListItem>? =
        try {
            communitiesRepository
                .getCommunityLeads(communityId)
                .map { rawItem -> rawItem.map() }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }


    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }

    private fun CommunityLeaderDomain.map(): LeaderListIem =
        LeaderListIem(
            id = IdUtil.generateLongId(),
            version = 0,
            userId = userId,
            avatarUrl = avatarUrl,
            username = username,
            rating = rating,
            ratingPercent = ratingPercent
    )
}