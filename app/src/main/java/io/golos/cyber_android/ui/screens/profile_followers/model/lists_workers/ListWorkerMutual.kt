package io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.domain.dto.UserDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.utils.MurmurHash
import javax.inject.Inject

class ListWorkerMutual
@Inject
constructor(
    userRepository: UsersRepository,
    private val mutualUsers: List<UserDomain>
) : ListWorkerBase(
    Int.MAX_VALUE,
    userRepository,
    FollowersFilter.MUTUALS
) , ListWorker {

    override suspend fun getData(offset: Int): List<FollowersListItem>? =
        mutualUsers
            .let { list ->
                list.mapIndexed { index, item -> item.map(index, list.lastIndex) }
            }


    private fun UserDomain.map(index: Int, lastIndex: Int) =
        FollowersListItem(
            id = MurmurHash.hash64(this.userId.userId),
            version = 0,
            follower = this,
            isJoined = true,
            isProgress = false,
            filter = FollowersFilter.MUTUALS,
            isLastItem = index == lastIndex)
}