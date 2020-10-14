package io.golos.cyber_android.ui.screens.community_page_members.model

import android.content.Context
import androidx.lifecycle.LiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.ErrorInfoDomain
import io.golos.domain.dto.UserIdDomain
import javax.inject.Inject
import javax.inject.Named

class CommunityPageMembersModelImpl
@Inject
constructor(
    private val appContext: Context,
    @Named(Clarification.PAGE_SIZE)
    override val pageSize: Int,
    private val usersListWorker: UsersListWorker
) : ModelBaseImpl(), CommunityPageMembersModel {

    override val title: String
        get() = appContext.resources.getString(R.string.members)

    override val noDataStubText: Int
        get() = R.string.no_members

    override val noDataStubExplanation: Int
        get() = R.string.no_members_community

    override fun getItems(): LiveData<List<VersionedListItem>> = usersListWorker.items

    override suspend fun loadPage() = usersListWorker.loadPage()

    override suspend fun retry() = usersListWorker.retry()

    /**
     * @return true in case of success
     */
    override suspend fun subscribeUnsubscribe(userId: UserIdDomain): ErrorInfoDomain? = usersListWorker.subscribeUnsubscribe(userId)
}