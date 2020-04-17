package io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil
import timber.log.Timber

/**
 * [TID] Type of item id
 * [TLI] Type of list item
 */
abstract class ListWorkerBaseImpl<TID, TLI: VersionedListItem>(
    private val pageSize: Int
) : ListWorkerBase<TID> {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    private var userActionInProgress = false

    protected var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(listOf())
    override val items: LiveData<List<VersionedListItem>>
        get() = _items


    override suspend fun loadPage() {
        when(currentLoadingState) {
            LoadingState.READY_TO_LOAD -> loadData(false)

            LoadingState.LOADING,
            LoadingState.IN_ERROR,
            LoadingState.ALL_DATA_LOADED -> { }
        }
    }

    override suspend fun retry() {
        when(currentLoadingState) {
            LoadingState.IN_ERROR -> loadData(true)

            LoadingState.LOADING,
            LoadingState.READY_TO_LOAD,
            LoadingState.ALL_DATA_LOADED -> { }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun switchState(id: TID): Boolean {
        if(userActionInProgress) {
            return true
        }

        userActionInProgress = true

        val item = loadedItems[getItemIndex(id)] as TLI

        setItemInProgress(id)

        val isSuccess =
            try {
                if(isItemInPositiveState(item)) {
                    moveItemToNegativeState(id)
                } else {
                    moveItemToPositiveState(id)
                }
                true
            } catch (ex: Exception) {
                Timber.e(ex)
                false
            }

        completeItemInProgress(id, isSuccess)

        userActionInProgress = false

        return isSuccess
    }

    protected abstract fun isItemInPositiveState(item: TLI): Boolean

    protected abstract suspend fun moveItemToPositiveState(id: TID)

    protected abstract suspend fun moveItemToNegativeState(id: TID)

    private suspend fun loadData(isRetry: Boolean) {
        currentLoadingState = LoadingState.LOADING

        if(isRetry) {
            replaceRetryByLoading()
        } else {
            addLoading()
        }

        val data = getData(loadedItems.size-1)

        currentLoadingState = if(data != null) {
            addLoadedData(data)

            if(data.size < pageSize) LoadingState.ALL_DATA_LOADED else LoadingState.READY_TO_LOAD
        } else {
            replaceLoadingByRetry()
            LoadingState.IN_ERROR
        }
    }

    private suspend fun getData(offset: Int): List<TLI>? =
        try {
            getPage(offset)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

    abstract suspend fun getPage(offset: Int): List<TLI>

    protected abstract fun setItemInProgress(id: TID)

    protected abstract fun completeItemInProgress(id: TID, isSuccess: Boolean)

    @Suppress("UNCHECKED_CAST")
    protected fun updateItem(id: TID, updateAction: (TLI) -> TLI) = updateData {
        val itemIndex = getItemIndex(id)
        loadedItems[itemIndex] = updateAction(loadedItems[itemIndex] as TLI)
    }

    protected abstract fun getItemIndex(id: TID): Int

    protected fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] =
            LoadingListItem(IdUtil.generateLongId(), 0)
    }

    protected fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] =
            RetryListItem(IdUtil.generateLongId(), 0)
    }

    protected fun addLoadedData(data: List<TLI>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)         // Remove Loading or Retry items

        if(data.isNotEmpty()) {
            val lastIndex = loadedItems.lastIndex

            loadedItems.addAll(data)

            if(lastIndex == -1) {
                loadedItems[0] = markAsFirst(loadedItems[0])
            } else {
                loadedItems[lastIndex] = unMarkAsLast(loadedItems[lastIndex])
            }
            loadedItems[loadedItems.lastIndex] = markAsLast(loadedItems[loadedItems.lastIndex])
        }
    }

    protected abstract fun markAsFirst(item: VersionedListItem): VersionedListItem

    protected abstract fun markAsLast(item: VersionedListItem): VersionedListItem

    protected abstract fun unMarkAsLast(item: VersionedListItem): VersionedListItem

    protected fun addLoading() = updateData { loadedItems.add( LoadingListItem(IdUtil.generateLongId(), 0)) }

    protected fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }
}