package io.golos.cyber_android.ui.screens.wallet.model.lists_workers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.wallet.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.FollowingUserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.IdUtil
import io.golos.domain.utils.MurmurHash
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ListWorkerSendPointsImpl
@Inject
constructor(
    private val currentUserRepository: CurrentUserRepositoryRead,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val userRepository: UsersRepository
) : ListWorkerSendPoints {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

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

    private suspend fun getData(offset: Int): List<SendPointsListItem>? =
        try {
            userRepository
                .getUserFollowing(currentUserRepository.userId, offset, pageSize)
                .let { list ->
                    list.mapIndexed { index, item -> item.map() }
                }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

    private fun addLoading() = updateData { loadedItems.add(createLoadingListItem())}

    private fun replaceRetryByLoading() = updateData {
        loadedItems[loadedItems.lastIndex] = createLoadingListItem()
    }

    private fun replaceLoadingByRetry() = updateData {
        loadedItems[loadedItems.lastIndex] = createRetryListItem()
    }

    @Suppress("UNCHECKED_CAST")
    private fun addLoadedData(data: List<VersionedListItem>) = updateData {
        loadedItems.removeAt(loadedItems.lastIndex)
        loadedItems.addAll(data)
    }

    private fun isUserWithId(userId: UserIdDomain, item: Any): Boolean =
        item is SendPointsListItem && item.userId == userId

    private fun createLoadingListItem(): VersionedListItem = LoadingListItem(IdUtil.generateLongId(), 0)

    private fun createRetryListItem(): VersionedListItem = RetryListItem(IdUtil.generateLongId(), 0)

    private fun FollowingUserDomain.map() =
        SendPointsListItem(
            id = MurmurHash.hash64(user.userId.userId),
            version = 0,
            userId = user.userId,
            name = user.userName,
            avatarUrl = user.userAvatar
        )

    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }
}