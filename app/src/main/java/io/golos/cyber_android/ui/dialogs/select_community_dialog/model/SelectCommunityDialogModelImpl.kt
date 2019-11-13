package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModelImpl
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.utils.IdUtil
import javax.inject.Inject
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.extensions.mapSuccess
import io.golos.domain.utils.MurmurHash

class SelectCommunityDialogModelImpl
@Inject
constructor(
    communitiesApi: CommunitiesApi,
    private val search: CommunitiesSearch,
    dispatchersProvider: DispatchersProvider
) : CommunitiesModelImpl(
    communitiesApi,
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