package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryImpl
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.FollowingUserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.user.UsersRepository
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
) : ListWorkerBase(
    pageSize,
    userRepository,
    FollowersFilter.FOLLOWINGS
) , ListWorker {

    override suspend fun getData(offset: Int): List<FollowersListItem>? =
        try {
            userRepository
                .getUserFollowing(userId, offset, pageSize)
                .map { it.map() }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

    private fun FollowingUserDomain.map() =
        FollowersListItem(
            id = MurmurHash.hash64(user.userId.userId),
            version = 0,
            follower = user,
            isJoined = isFollowed,
            isProgress = false,
            filter = FollowersFilter.FOLLOWINGS)
}