package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import javax.inject.Inject

class UserDiscoveryModelImpl
@Inject
constructor(
    private val profileUserId: UserIdDomain,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val followingListWorker: UsersListWorker
) : UserDiscoveryModel,
    ModelBaseImpl() {

    override val isCurrentUser: Boolean
        get() = profileUserId == currentUserRepository.userId

    override fun getItems(filter: FollowersFilter): LiveData<List<VersionedListItem>> = followingListWorker.items

    override suspend fun loadPage(filter: FollowersFilter) = followingListWorker.loadPage()

    override suspend fun retry(filter: FollowersFilter) = followingListWorker.retry()

    /**
     * @return true in case of success
     */
    override suspend fun subscribeUnsubscribe(userId: UserIdDomain, filter: FollowersFilter): ErrorInfoDomain? {
        val errorInfo = followingListWorker.subscribeUnsubscribe(userId)

        return errorInfo
    }
}