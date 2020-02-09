package io.golos.cyber_android.ui.screens.community_page_members.model.list_worker

import io.golos.cyber_android.ui.screens.community_page_members.dto.CommunityUserListItem
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorkerBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.domain.utils.MurmurHash
import timber.log.Timber
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.domain.utils.IdUtil
import javax.inject.Inject
import javax.inject.Named

class ListWorkerCommunityMembers
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    private val pageSize: Int,
    @Named(Clarification.COMMUNITY_ID)
    private val communityId: String,
    userRepository: UsersRepository,
    private val communitiesRepository: CommunitiesRepository
) : UsersListWorkerBase<CommunityUserListItem>(
    pageSize,
    userRepository
), UsersListWorker {

    override suspend fun getData(offset: Int): List<CommunityUserListItem> =
        communitiesRepository.getSubscribers(communityId, offset, pageSize)
            .let { list ->
                list.mapIndexed { index, item -> item.map(index, list.lastIndex) }
            }

    override fun isUserWithId(userId: UserIdDomain, item: Any): Boolean =
        item is CommunityUserListItem && item.user.userId == userId

    override fun createLoadingListItem(): VersionedListItem = LoadingListItem(IdUtil.generateLongId(), 0)

    override fun createRetryListItem(): VersionedListItem = RetryListItem(IdUtil.generateLongId(), 0)

    override fun markAsFirst(item: CommunityUserListItem) = item.copy(isFirstItem = true)

    override fun markAsLast(item: CommunityUserListItem) = item.copy(isLastItem = true)

    override fun unMarkAsLast(item: CommunityUserListItem) = item.copy(isLastItem = false)

    private fun UserDomain.map(index: Int, lastIndex: Int) =
        CommunityUserListItem(
            id = MurmurHash.hash64(this.userId.userId),
            version = 0,
            user = this,
            isFollowing = isSubscribed,
            isLastItem = index == lastIndex,
            canFollow = !isSubscribed,
            showPosts = true
        )
}