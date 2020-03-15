package io.golos.cyber_android.ui.screens.profile_black_list.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.ListWorkerBase
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.communities.ListWorkerCommunities
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.users.ListWorkerUsers
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import javax.inject.Inject
import javax.inject.Named

class ProfileBlackListModelImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    override val pageSize: Int,
    private val communitiesWorker: ListWorkerCommunities,
    private val usersWorker: ListWorkerUsers
) : ModelBaseImpl(), ProfileBlackListModel {

    override fun getItems(filter: BlackListFilter): LiveData<List<VersionedListItem>> = getWorker(filter).items

    override suspend fun loadPage(filter: BlackListFilter) = getWorker(filter).loadPage()

    override suspend fun retry(filter: BlackListFilter) = getWorker(filter).retry()

    override suspend fun switchUserState(userId: UserIdDomain): Boolean = usersWorker.switchState(userId)

    override suspend fun switchCommunityState(communityId: CommunityIdDomain): Boolean = communitiesWorker.switchState(communityId)

    private fun getWorker(filter: BlackListFilter): ListWorkerBase<*> =
        when(filter) {
            BlackListFilter.USERS -> usersWorker
            BlackListFilter.COMMUNITIES -> communitiesWorker
        }
}