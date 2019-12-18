package io.golos.cyber_android.ui.screens.profile_comments.model

import com.squareup.moshi.Moshi
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.data.dto.block.ListContentBlockEntity
import io.golos.data.mappers.mapToContentBlock
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileCommentsModelImpl @Inject constructor(
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val moshi: Moshi
) : ProfileCommentsModel, ModelBaseImpl() {

    override suspend fun getComments(offset: Int, pageSize: Int): List<CommentDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.getComments(
                offset,
                pageSize,
                CommentDomain.CommentTypeDomain.USER
            )
        }
    }

    override suspend fun commentUpVote(commentId: ContentIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(
                commentId.communityId,
                commentId.userId,
                commentId.permlink
            )
        }
    }

    override suspend fun commentDownVote(commentId: ContentIdDomain) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(
                commentId.communityId,
                commentId.userId,
                commentId.permlink
            )
        }
    }

    override suspend fun deleteComment(userId: String, permlink: String, communityId: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePostOrComment(
                userId,
                permlink,
                communityId
            )
        }
    }

    override suspend fun editComment(
        userId: String,
        permlink: String,
        communityId: String,
        body: ContentBlock?
    ) {
        val contentEntity = body?.mapToContentBlock()
        val adapter = moshi.adapter(ListContentBlockEntity::class.java)
        val jsonBody = adapter.toJson(contentEntity)

        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.editPostOrComment(
                userId,
                permlink,
                communityId,
                "", //todo
                jsonBody,
                arrayListOf(), //todo
                ""//todo
            )
        }
    }
}