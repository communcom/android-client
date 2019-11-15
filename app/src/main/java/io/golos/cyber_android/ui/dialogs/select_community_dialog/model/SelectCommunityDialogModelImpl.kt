package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModelImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.mapSuccess
import io.golos.domain.use_cases.community.CommunitiesRepository
import javax.inject.Inject

class SelectCommunityDialogModelImpl
@Inject
constructor(
    private val search: CommunitiesSearch,
    dispatchersProvider: DispatchersProvider,
    communitiesRepository: CommunitiesRepository
) : CommunitiesModelImpl(
    communitiesRepository,
    dispatchersProvider
), SelectCommunityDialogModel {

    override val showUserCommunityOnly: Boolean = true

    override fun search(searchText: String) = search.search(searchText)

    @Suppress("NestedLambdaShadowedImplicitParameter")
    override fun setOnSearchResultListener(listener: (Either<List<VersionedListItem>?, Throwable>) -> Unit) {
        search.setOnSearchResultListener {
            it.mapSuccess {
                it?.map { it.map() as VersionedListItem }
            }
            .let { listener(it) }
        }
    }

    override fun close() = search.close()
}