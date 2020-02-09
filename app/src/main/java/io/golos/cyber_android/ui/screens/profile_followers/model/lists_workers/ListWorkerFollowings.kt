package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.FollowingUserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.MurmurHash
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ListWorkerFollowings
@Inject
constructor(
    private val userId: UserIdDomain,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val userRepository: UsersRepository
) : UsersListWorkerBase<FollowersListItem>(
    pageSize,
    userRepository
) , UsersListWorker {

    override suspend fun getData(offset: Int): List<FollowersListItem> =
        userRepository
            .getUserFollowing(userId, offset, pageSize)
            .let { list ->
                list.mapIndexed { index, item -> item.map(index, list.lastIndex) }
            }

    override fun isUserWithId(userId: UserIdDomain, item: Any): Boolean =
        item is FollowersListItem && item.follower.userId == userId

    override fun createLoadingListItem(): VersionedListItem =
        LoadingListItem(FollowersFilter.FOLLOWINGS)

    override fun createRetryListItem(): VersionedListItem =
        RetryListItem(FollowersFilter.FOLLOWINGS)

    override fun markAsFirst(item: FollowersListItem) = item.copy(isFirstItem = true)

    override fun markAsLast(item: FollowersListItem) = item.copy(isLastItem = true)

    override fun unMarkAsLast(item: FollowersListItem) = item.copy(isLastItem = false)

    private fun FollowingUserDomain.map(index: Int, lastIndex: Int) =
        FollowersListItem(
            id = MurmurHash.hash64(user.userId.userId),
            version = 0,
            isFirstItem = false,
            isLastItem = index == lastIndex,
            follower = user,
            isFollowing = user.isSubscribed,
            filter = FollowersFilter.FOLLOWINGS)
}