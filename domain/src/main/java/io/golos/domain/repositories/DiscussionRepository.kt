package io.golos.domain.repositories

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import java.io.File

interface DiscussionRepository {

    @Deprecated("For create post need use createPost")
    fun createOrUpdate(params: DiscussionCreationRequestEntity): DiscussionCreationResultEntity

    suspend fun getComments(
        offset: Int,
        pageSize: Int,
        commentType: CommentDomain.CommentTypeDomain,
        userId: UserIdDomain,
        permlink: String? = null,
        communityId: String? = null,
        communityAlias: String? = null,
        parentComment: ParentCommentIdentifierDomain? = null
    ): List<CommentDomain>

    suspend fun upVote(contentIdDomain: ContentIdDomain)

    suspend fun downVote(contentIdDomain: ContentIdDomain)

    suspend fun reportPost(communityId: String, authorId: String, permlink: String, reason: String)

    suspend fun getPost(user: CyberName, communityId: String, permlink: String): PostDomain

    suspend fun deletePostOrComment(
        permlink: String,
        communityId: String
    )

    suspend fun updateComment(commentDomain: CommentDomain)

    @Deprecated("Use getPost method with 3 params")
    fun getPost(user: CyberName, permlink: Permlink): PostModel

    fun deletePost(postId: DiscussionIdModel)

    fun createCommentForPost(postId: DiscussionIdModel, commentText: String): CommentModel

    fun deleteComment(commentId: DiscussionIdModel)

    @Deprecated("Need use method updateComment")
    fun updateCommentText(comment: CommentModel, newCommentText: String): CommentModel

    fun createReplyComment(repliedCommentId: DiscussionIdModel, newCommentText: String): CommentModel

    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain): List<PostDomain>

    suspend fun uploadContentAttachment(file: File): String

    suspend fun createPost(communityId: String, body: String, tags: List<String>): ContentIdDomain

    suspend fun updatePost(contentIdDomain: ContentIdDomain, body: String, tags: List<String>): ContentIdDomain
}