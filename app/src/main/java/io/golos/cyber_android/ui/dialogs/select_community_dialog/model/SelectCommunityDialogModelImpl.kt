package io.golos.cyber_android.ui.dialogs.select_community_dialog.model

import android.content.Context
import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModelImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.extensions.mapSuccess
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.utils.id.IdUtil
import javax.inject.Inject

class SelectCommunityDialogModelImpl
@Inject
constructor(
    private val appContext: Context,
    private val search: CommunitiesSearch,
    private val currentUserRepository: CurrentUserRepositoryRead,
    dispatchersProvider: DispatchersProvider,
    communitiesRepository: CommunitiesRepository
) : CommunitiesListModelImpl(
    appContext,
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

    override fun onInitEmptyList(list: MutableList<VersionedListItem>) {
        list.add(getMyFeedListItem())
    }

    private fun getMyFeedListItem(): CommunityListItem {
        return CommunityListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            isFirstItem = true,
            isLastItem = false,
            isInPositiveState = false,
            isProgress = false,
            community = CommunityDomain(
                communityId = CommunityIdDomain(GlobalConstants.MY_FEED_COMMUNITY_ID),
                alias = GlobalConstants.MY_FEED_COMMUNITY_ALIAS,
                name = appContext.getString(R.string.my_feed_community),
                avatarUrl = currentUserRepository.userAvatarUrl,
                coverUrl = null,
                subscribersCount = 0,
                postsCount = 0,
                isSubscribed = false
            ),
            isInBlockList = false
        )
    }

}