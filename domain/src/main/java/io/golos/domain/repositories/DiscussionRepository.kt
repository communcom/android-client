package io.golos.domain.repositories

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.requestmodel.DiscussionCreationRequestEntity
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.Block
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

    suspend fun deletePost(permlink: String, communityId: String)

    suspend fun deleteComment(permlink: String, communityId: String)

    suspend fun updateComment(commentDomain: CommentDomain)

    @Deprecated("Use getPost method with 3 params")
    fun getPost(user: CyberName, permlink: Permlink): PostModel

    @Deprecated("Need use method deletePost with 2 params")
    fun deletePost(postId: ContentIdDomain)

    suspend fun createCommentForPost(postId: DiscussionIdModel, contentId: ContentIdDomain, commentText: String): CommentModel

    @Deprecated("Need use method deleteComment with 2 params")
    fun deleteComment(commentId: DiscussionIdModel)

    suspend fun createReplyComment(repliedCommentId: DiscussionIdModel, contentId: ContentIdDomain, newCommentText: String): CommentModel

    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain, typeObject: TypeObjectDomain): List<PostDomain>

    suspend fun uploadContentAttachment(file: File): String

    suspend fun createPost(communityId: String, body: String, tags: List<String>): ContentIdDomain

    suspend fun updatePost(contentIdDomain: ContentIdDomain, body: String, tags: List<String>): ContentIdDomain

    suspend fun sendComment(postIdDomain: ContentIdDomain, content: List<Block>, attachments: AttachmentsBlock?): CommentDomain
}