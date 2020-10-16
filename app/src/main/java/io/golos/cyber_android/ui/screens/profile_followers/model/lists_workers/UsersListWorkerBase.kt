package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import android.content.Context
import io.golos.cyber_android.ui.screens.profile_followers.dto.UserListItem
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.recycler_view.versioned.paging.LoadedItemsPagedListBase
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import timber.log.Timber

/**
 * [TLI] type of user list item
 */
abstract class UsersListWorkerBase<TLI : UserListItem<*>>(
    private val appContext: Context,
    pageSize: Int,
    private val userRepository: UsersRepository
) : LoadedItemsPagedListBase<TLI>(pageSize),
    UsersListWorker {

    private var subscribingInProgress = false

    abstract fun isUserWithId(userId: UserIdDomain, item: Any): Boolean

    /**
     * @return true in case of success
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun subscribeUnsubscribe(userId: UserIdDomain): ErrorInfoDomain? {
        if(subscribingInProgress) {
            return null
        }

        subscribingInProgress = true

        val user = loadedItems[getUserIndex(userId)] as TLI

        setUserInProgress(userId)

        val errorInfo =
            try {
                if(user.isFollowing) {
                    userRepository.unsubscribeToFollower(userId)
                } else {
                    userRepository.subscribeToFollower(userId)
                }
                null
            } catch (ex: Exception) {
                Timber.e(ex)
                ErrorInfoDomain(ex.getMessage(appContext))
            }

        completeUserInProgress(userId, errorInfo == null)

        subscribingInProgress = false

        return errorInfo
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
}