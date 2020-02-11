package io.golos.cyber_android.ui.shared.recycler_view.versioned.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import timber.log.Timber

/**
 * Base class for lists with three type of items: 1) general; 2) loading; 3) retry
 * @param [T] type of general list item
 */
abstract class LoadedItemsPagedListBase<T: VersionedListItem>(private val pageSize: Int) : LoadedItemsList {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    protected var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(listOf())
    override val items: LiveData<List<VersionedListItem>> = _items

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

    override suspend fun clear() {
        currentLoadingState = LoadingState.READY_TO_LOAD
        updateData {
            loadedItems.clear()
        }
    }

    private suspend fun loadData(isRetry: Boolean) {
        currentLoadingState = LoadingState.LOADING

        if(isRetry) {
            replaceRetryByLoading()
        } else {
            addLoading()
        }

        val data = try {
            getData(loadedItems.size-1)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

        currentLoadingState = if(data != null) {
            addLoadedData(data)

        if(data.size < pageSize) LoadingState.ALL_DATA_LOADED else LoadingState.READY_TO_LOAD
        } else {
            replaceLoadingByRetry()
            LoadingState.IN_ERROR
        }
    }

    protected abstract suspend fun getData(offset: Int): List<T>

    private fun addLoading() = updateData { loadedItems.add(createLoadingListItem())}

    private fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] = createLoadingListItem()
    }

    private fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] = createRetryListItem()
    }

    @Suppress("UNCHECKED_CAST")
    private fun addLoadedData(data: List<VersionedListItem>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)         // Remove Loading or Retry items

        if(data.isNotEmpty()) {
            val lastIndex = loadedItems.lastIndex

            loadedItems.addAll(data)

            if(lastIndex == -1) {
                loadedItems[0] = markAsFirst(loadedItems[0] as T)
            } else {
                loadedItems[lastIndex] = unMarkAsLast(loadedItems[lastIndex] as T)
            }
            loadedItems[loadedItems.lastIndex] = markAsLast(loadedItems[loadedItems.lastIndex] as T)
        }
    }

    protected abstract fun markAsFirst(item: T): T
    protected abstract fun markAsLast(item: T): T
    protected abstract fun unMarkAsLast(item: T): T

    protected open fun createLoadingListItem(): VersionedListItem = LoadingListItem()

    protected open fun createRetryListItem(): VersionedListItem = RetryListItem()

    protected fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }
}