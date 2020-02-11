package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.NoDataListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.IdUtil
import io.golos.domain.utils.MurmurHash
import javax.inject.Inject

class ListWorkerMutual
@Inject
constructor(
    userRepository: UsersRepository,
    private val mutualUsers: List<UserDomain>
) : UsersListWorkerBase<FollowersListItem>(
    Int.MAX_VALUE,
    userRepository
) , UsersListWorker {

    override suspend fun getData(offset: Int): List<FollowersListItem> =
        mutualUsers
            .let { list ->
                list.mapIndexed { index, item -> item.map(index, list.lastIndex) }
            }

    override fun isUserWithId(userId: UserIdDomain, item: Any): Boolean =
        item is FollowersListItem && item.follower.userId == userId

    override fun createLoadingListItem() = LoadingListItem(FollowersFilter.MUTUALS)

    override fun createRetryListItem() = RetryListItem(FollowersFilter.MUTUALS)

    override fun createNoDataListItem(): VersionedListItem = NoDataListItem(FollowersFilter.MUTUALS)

    override fun markAsFirst(item: FollowersListItem) = item.copy(isFirstItem = true)

    override fun markAsLast(item: FollowersListItem) = item.copy(isLastItem = true)

    override fun unMarkAsLast(item: FollowersListItem) = item.copy(isLastItem = false)

    private fun UserDomain.map(index: Int, lastIndex: Int) =
        FollowersListItem(
            id = MurmurHash.hash64(this.userId.userId),
            version = 0,
            isFirstItem = false,
            isLastItem = index == lastIndex,
            follower = this,
            isFollowing = true,
            filter = FollowersFilter.MUTUALS)
}