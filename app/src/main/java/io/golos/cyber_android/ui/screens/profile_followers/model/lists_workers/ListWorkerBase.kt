package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.RetryListItem
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.IdUtil
import timber.log.Timber

abstract class ListWorkerBase(
    private val pageSize: Int,
    private val userRepository: UsersRepository,
    private val filter: FollowersFilter
) : ListWorker {

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
            LoadingState.ALL_DATA_LOADED -> {
            }
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

    abstract suspend fun getData(offset: Int): List<FollowersListItem>?

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

    private fun addLoading() = updateData { loadedItems.add(LoadingListItem(IdUtil.generateLongId(), 0, filter))}

    private fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] =
            LoadingListItem(IdUtil.generateLongId(), 0, filter)
    }

    private fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] =
            RetryListItem(IdUtil.generateLongId(), 0, filter)
    }

    private fun addLoadedData(data: List<FollowersListItem>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)

        if(loadedItems.isNotEmpty()) {
            val lastItem = loadedItems[loadedItems.lastIndex] as FollowersListItem
            loadedItems[loadedItems.lastIndex] = lastItem.copy(version = lastItem.version + 1, isLastItem = false)
        }

        loadedItems.addAll(data)
    }

    /**
     * @return true in case of success
     */
    override suspend fun subscribeUnsubscribe(userId: UserIdDomain): Boolean {
        if(subscribingInProgress) {
            return true
        }

        subscribingInProgress = true

        val user = loadedItems[getUserIndex(userId)] as FollowersListItem

        setUserInProgress(userId)

        val isSuccess =
            try {
                if(user.isJoined) {
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

    private fun setUserInProgress(userId: UserIdDomain) =
        updateUser(userId) { oldUser ->
            oldUser.copy(
                version = oldUser.version + 1,
                isProgress = true,
                isJoined = !oldUser.isJoined)
        }

    private fun completeUserInProgress(userId: UserIdDomain, isSuccess: Boolean) =
        updateUser(userId) { oldUser ->
            oldUser.copy(
                version = oldUser.version + 1,
                isProgress = false,
                isJoined = if(isSuccess) oldUser.isJoined else !oldUser.isJoined
            )
        }

    private fun updateUser(userId: UserIdDomain, updateAction: (FollowersListItem) -> FollowersListItem) {
        val itemIndex = getUserIndex(userId)

        if(itemIndex == -1) {
            return
        }

        updateData {
            loadedItems[itemIndex] = updateAction(loadedItems[itemIndex] as FollowersListItem)
        }
    }

    private fun getUserIndex(userId: UserIdDomain) =
        loadedItems.indexOfFirst { it is FollowersListItem && it.follower.userId == userId }

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }
}