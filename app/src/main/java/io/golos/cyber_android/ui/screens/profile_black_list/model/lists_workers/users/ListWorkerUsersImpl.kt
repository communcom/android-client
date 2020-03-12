package io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.users

import io.golos.cyber_android.ui.screens.profile_black_list.dto.UserListItem
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.ListWorkerBaseImpl
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.UsersRepository
import io.golos.utils.id.MurmurHash
import javax.inject.Inject
import javax.inject.Named

class ListWorkerUsersImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val usersRepository: UsersRepository,
    private val currentUserRepository: CurrentUserRepository
) : ListWorkerBaseImpl<UserIdDomain, UserListItem>(
    pageSize,
    currentUserRepository
), ListWorkerUsers {
    override fun isItemInPositiveState(item: UserListItem): Boolean = item.isInPositiveState

    override suspend fun moveItemToPositiveState(id: UserIdDomain) = usersRepository.moveUserToBlackList(id)

    override suspend fun moveItemToNegativeState(id: UserIdDomain) = usersRepository.moveUserFromBlackList(id)

    override suspend fun getPage(offset: Int): List<UserListItem> =
        usersRepository
            .getUsersInBlackList(offset, pageSize, currentUserRepository.userId)
            .map { it.map() }

    override fun setItemInProgress(id: UserIdDomain) =
        updateItem(id) { oldUser ->
            oldUser.copy(
                version = oldUser.version + 1,
                isProgress = true,
                isInPositiveState = !oldUser.isInPositiveState)
        }


    override fun completeItemInProgress(id: UserIdDomain, isSuccess: Boolean) =
        updateItem(id) { oldUser ->
            oldUser.copy(
                version = oldUser.version + 1,
                isProgress = false,
                isInPositiveState = if(isSuccess) oldUser.isInPositiveState else !oldUser.isInPositiveState
            )
        }

    override fun getItemIndex(id: UserIdDomain): Int = loadedItems.indexOfFirst { it is UserListItem && it.user.userId == id }

    private fun UserDomain.map() =
        UserListItem(
            id = MurmurHash.hash64(this.userId.userId),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            user = this,
            isInPositiveState = true,
            isProgress = false
        )
}