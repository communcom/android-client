package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.profile_followers.dto.UserListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import timber.log.Timber

/**
 * [TLI] type of user list item
 */
abstract class UsersListWorkerBase<TLI : UserListItem<*>>(
    private val pageSize: Int,
    private val userRepository: UsersRepository
) : UsersListWorker {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    private var subscribingInProgress = false

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

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

    abstract suspend fun getData(offset: Int): List<TLI>?

    abstract fun isUserWithId(userId: UserIdDomain, item: Any): Boolean

    abstract fun createLoadingListItem(): VersionedListItem

    abstract fun createRetryListItem(): VersionedListItem

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

    private fun addLoading() = updateData { loadedItems.add(createLoadingListItem())}

    private fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] = createLoadingListItem()
    }

    private fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] = createRetryListItem()
    }

    @Suppress("UNCHECKED_CAST")
    private fun addLoadedData(data: List<TLI>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)

        if(loadedItems.isNotEmpty()) {
            val lastItem = loadedItems[loadedItems.lastIndex] as TLI
            loadedItems[loadedItems.lastIndex] = lastItem.updateIsLastItem(false)
        }

        loadedItems.addAll(data)
    }

    /**
     * @return true in case of success
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun subscribeUnsubscribe(userId: UserIdDomain): Boolean {
        if(subscribingInProgress) {
            return true
        }

        subscribingInProgress = true

        val user = loadedItems[getUserIndex(userId)] as TLI

        setUserInProgress(userId)

        val isSuccess =
            try {
                if(user.isFollowing) {
                    userRepository.unsubscribeToFollower(userId)
                } else {
                    userRepository.subscribeToFollower(userId)
                }
                true
            } catch (ex: Exception) {
                Timber.e(ex)
                false
            }

        completeUserInProgress(userId, isSuccess)

        subscribingInProgress = false

        return isSuccess
    }

    /**
     * Subscribe/unsubscribe without network call
     */
    override fun subscribeUnsubscribeInstant(userId: UserIdDomain) = setUserInProgress(userId)

    @Suppress("UNCHECKED_CAST")
    private fun setUserInProgress(userId: UserIdDomain) =
        updateUser(userId) { oldUser -> oldUser.updateIsFollowing(!oldUser.isFollowing) as TLI }

    @Suppress("UNCHECKED_CAST")
    private fun completeUserInProgress(userId: UserIdDomain, isSuccess: Boolean) =
        updateUser(userId) { oldUser ->
            oldUser.updateIsFollowing(if(isSuccess) oldUser.isFollowing else !oldUser.isFollowing) as TLI
        }

    @Suppress("UNCHECKED_CAST")
    private fun updateUser(userId: UserIdDomain, updateAction: (TLI) -> TLI) {
        val itemIndex = getUserIndex(userId)

        if(itemIndex == -1) {
            return
        }

        updateData {
            loadedItems[itemIndex] = updateAction(loadedItems[itemIndex] as TLI)
        }
    }

    private fun getUserIndex(userId: UserIdDomain) =
        loadedItems.indexOfFirst { isUserWithId(userId, it) }

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }
}