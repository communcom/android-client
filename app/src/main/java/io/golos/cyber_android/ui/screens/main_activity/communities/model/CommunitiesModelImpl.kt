package io.golos.cyber_android.ui.screens.main_activity.communities.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.RetryListItem
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.utils.IdUtil
import io.golos.domain.utils.MurmurHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

open class CommunitiesModelImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val dispatchersProvider: DispatchersProvider
) : ModelBaseImpl(), CommunitiesModel {

    private enum class State {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED
    }

    private var currentState = State.READY_TO_LOAD

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(listOf())
    override val items: LiveData<List<VersionedListItem>>
        get() = _items

    override val pageSize = 25

    override suspend fun loadPage() {
        when(currentState) {
            State.READY_TO_LOAD -> loadData(false)

            State.LOADING,
            State.ALL_DATA_LOADED -> { /* do nothing */ }
        }
    }

    override suspend fun retry() {
        when(currentState) {
            State.READY_TO_LOAD -> loadData(true)

            State.LOADING,
            State.ALL_DATA_LOADED -> { /* do nothing */ }
        }
    }


    protected fun CommunityDomain.map() = CommunityListItem(MurmurHash.hash64(this.communityId), 0, this, false)

    private suspend fun loadData(isRetry: Boolean) {
        currentState = State.LOADING

        if(isRetry) {
            replaceRetryByLoading()
        } else {
            addLoading()
        }

        val data = getData(loadedItems.size-1)

        currentState = if(data != null) {
            addLoadedData(data)

            if(data.size < pageSize) State.ALL_DATA_LOADED else State.READY_TO_LOAD
        } else {
            replaceLoadingByRetry()
            State.READY_TO_LOAD
        }
    }

    private suspend fun getData(offset: Int): List<CommunityListItem>? =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)

            try {
                communitiesApi.getCommunitiesList(offset, pageSize, true)
                    .map { rawItem -> rawItem.map() }
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }

    private fun addLoading() = updateData { loadedItems.add(LoadingListItem(IdUtil.generateLongId(), 0)) }

    private fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] = LoadingListItem(IdUtil.generateLongId(), 0)
    }

    private fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] = RetryListItem(IdUtil.generateLongId(), 0)
    }

    private fun addLoadedData(data: List<CommunityListItem>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)
        loadedItems.addAll(data)
    }
}