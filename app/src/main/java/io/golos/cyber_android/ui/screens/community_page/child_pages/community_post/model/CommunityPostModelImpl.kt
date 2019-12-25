package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model

import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunityPostModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider
) : CommunityPostModel,
    GetPostsUseCase by getPostsUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override suspend fun addToFavorite(permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
        }
    }

    override suspend fun removeFromFavorite(permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            delay(1000)
        }
    }

    override suspend fun deletePost(permlink: String, communityId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePostOrComment(permlink, communityId)
        }
    }

}