package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.CommunitiesRepository
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.CommunityListItem
import io.golos.domain.extensions.foldValue
import io.golos.shared_core.MurmurHash
import io.golos.sharedmodel.Either
import javax.inject.Inject

class DiscoverModelImpl
@Inject
constructor(
    private val communitiesRepository: CommunitiesRepository
) : ModelBaseImpl(), DiscoverModel {

    private companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun getFirstPage(): Either<List<ListItem>, Throwable> =
        communitiesRepository.getCommunities(PAGE_SIZE, 0, CommunityType.DISCOVERED)
            .foldValue {
                it.map { rawItem ->
                    CommunityListItem(
                        MurmurHash.hash64(rawItem.id),
                        rawItem.id,
                        rawItem.name,
                        rawItem.followersQuantity,
                        rawItem.logoUrl,
                        false)
                }
            }
}