package io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search

import io.golos.cyber_android.ui.shared.search.SearchEngineBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.utils.helpers.capitalize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CommunitiesSearchImpl
@Inject
constructor(
    private val communitiesRepository: CommunitiesRepository,
    private val currentUserRepository: CurrentUserRepositoryRead,
    dispatchersProvider: DispatchersProvider
) : SearchEngineBase<CommunityDomain>(dispatchersProvider), CommunitiesSearch {

    override suspend fun doSearch(searchString: String): List<CommunityDomain>? {
        return if(searchString.length < 3) {
            return null
        } else {
            communitiesRepository.getCommunitiesList(
                currentUserRepository.userId,
                0,
                GlobalConstants.PAGE_SIZE,
                false,
                searchString.capitalize())
        }
    }
}