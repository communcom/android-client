package io.golos.cyber_android.ui.screens.feed_my.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getUserProfileUseCase: GetLocalUserUseCase,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val postFilter: PostFiltersHolder,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val currentUserRepository: CurrentUserRepository
) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase,
    GetLocalUserUseCase by getUserProfileUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override val openFeedTypeFlow: Flow<PostFiltersHolder.CurrentOpenTypeFeed> = postFilter.openTypeFeedFlow

    override val userAvatarFlow: Flow<String?> = currentUserRepository.userAvatarFlow

    override suspend fun deletePost(permlink: String, communityId: CommunityIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePost(permlink, communityId)
        }
    }

    override suspend fun addToFavorite(permlink: String) {
        delay(1000)
    }

    override suspend fun removeFromFavorite(permlink: String) {
        delay(1000)
    }

    override suspend fun upVote(communityId: CommunityIdDomain, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
        }
    }

    override suspend fun downVote(communityId: CommunityIdDomain, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
        }
    }

    override suspend fun reportPost(authorPostId: String, communityId: CommunityIdDomain, permlink: String, reason: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.reportPost(communityId, authorPostId, permlink, reason)
        }
    }

    override val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
        get() = postFilter.feedFiltersFlow

}