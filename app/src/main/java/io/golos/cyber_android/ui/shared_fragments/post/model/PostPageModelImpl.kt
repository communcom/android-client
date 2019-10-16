package io.golos.cyber_android.ui.shared_fragments.post.model

import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.data.repositories.discussion.DiscussionRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.api.AuthApi
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostPageModelImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val dispatchersProvider: DispatchersProvider,
    private val discussionRepository: DiscussionRepository,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val authApi: AuthApi
) : ModelBaseImpl(), PostPageModel {
    override suspend fun getPost(): PostModel =
        discussionRepository.getPost(postToProcess.userId.toCyberName(), postToProcess.permlink)

    override fun getPostHeader(post: PostModel): PostHeader =
        PostHeader(
            post.community.name,
            post.community.avatarUrl,
            post.meta.time,

            post.author.username,
            post.author.userId.userId,

            false,
            post.author.userId.userId == currentUserRepository.authState!!.user.name
        )

    override suspend fun getUserId(userName: String): String =
        withContext(dispatchersProvider.ioDispatcher) {
            authApi.resolveCanonicalCyberName(userName).userId.name
        }
}