package io.golos.cyber_android.ui.screens.wallet.model.lists_workers

import io.golos.cyber_android.ui.screens.wallet.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.paging.LoadedItemsPagedListBase
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.FollowingUserDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.MurmurHash
import javax.inject.Inject
import javax.inject.Named

class ListWorkerSendPointsImpl
@Inject
constructor(
    private val currentUserRepository: CurrentUserRepositoryRead,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val userRepository: UsersRepository
) : LoadedItemsPagedListBase<SendPointsListItem>(pageSize), ListWorkerSendPoints {
    
    override suspend fun getData(offset: Int): List<SendPointsListItem> =
        userRepository
            .getUserFollowing(currentUserRepository.userId, offset, pageSize)
            .let { list ->
                list.mapIndexed { index, item -> item.map() }
            }
    
    private fun FollowingUserDomain.map() =
        SendPointsListItem(
            id = MurmurHash.hash64(user.userId.userId),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            userId = user.userId,
            name = user.userName,
            avatarUrl = user.userAvatar
        )

    override fun markAsFirst(item: SendPointsListItem) = item.copy(isFirstItem = true)

    override fun markAsLast(item: SendPointsListItem) = item.copy(isLastItem = true)

    override fun unMarkAsLast(item: SendPointsListItem) = item.copy(isLastItem = false)
}