package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModelImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.mapSuccess
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import javax.inject.Inject

class SelectCommunityDialogModelImpl
@Inject
constructor(
    private val search: CommunitiesSearch,
    currentUserRepository: CurrentUserRepository,
    dispatchersProvider: DispatchersProvider,
    communitiesRepository: CommunitiesRepository
) : CommunitiesListModelImpl(
    currentUserRepository.userId,
    false,
    communitiesRepository,
    dispatchersProvider
), SelectCommunityDialogModel {

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