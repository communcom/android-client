package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.FollowingUserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.MurmurHash
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ListWorkerFollowers
@Inject
constructor(
    private val userId: UserIdDomain,
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    private val userRepository: UsersRepository
) : ListWorkerBase(
    pageSize,
    userRepository,
    FollowersFilter.FOLLOWERS
), ListWorker {

    override suspend fun getData(offset: Int): List<FollowersListItem>? =
        try {
            userRepository
                .getUserFollowers(userId, offset, pageSize)
                .let { list ->
                    list.mapIndexed { index, item -> item.map(index, list.lastIndex) }
                }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

    private fun FollowingUserDomain.map(index: Int, lastIndex: Int) =
        FollowersListItem(
            id = MurmurHash.hash64(this.user.userId.userId),
            version = 0,
            follower = this.user,
            isJoined = isSubscribed,
            isProgress = false,
            filter = FollowersFilter.FOLLOWERS,
            isLastItem = index == lastIndex)
}