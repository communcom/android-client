package io.golos.domain.repositories

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.*
import java.io.File

interface DiscussionRepository {

    suspend fun getComments(
        offset: Int,
        pageSize: Int,
        commentType: CommentDomain.CommentTypeDomain,
        userId: UserIdDomain,
        permlink: String? = null,
        communityId: CommunityIdDomain? = null,
        communityAlias: String? = null,
        parentComment: ParentCommentIdentifierDomain? = null
    ): List<CommentDomain>

    suspend fun upVote(contentIdDomain: ContentIdDomain)

    suspend fun unVote(contentIdDomain: ContentIdDomain)

    suspend fun downVote(contentIdDomain: ContentIdDomain)

    suspend fun reportPost(communityId: CommunityIdDomain, authorId: UserIdDomain, permlink: String, reason: String)

    suspend fun getPost(user: CyberName, communityId: CommunityIdDomain, permlink: String): PostDomain

    suspend fun deletePost(permlink: String, communityId: CommunityIdDomain)

    suspend fun deleteComment(permlink: String, communityId: CommunityIdDomain)

    suspend fun updateComment(commentDomain: CommentDomain)

    suspend fun replyOnComment(parentCommentId: ContentIdDomain, jsonBody: String, toList: List<String>): CommentDomain

    suspend fun getPosts(postsConfigurationDomain: PostsConfigurationDomain, typeObject: TypeObjectDomain): List<PostDomain>

    suspend fun uploadContentAttachment(file: File): String

    suspend fun createPost(communityId: CommunityIdDomain,title:String, body: String, tags: List<String>): ContentIdDomain

    suspend fun updatePost(contentIdDomain: ContentIdDomain,title:String, body: String, tags: List<String>): PostDomain

    suspend fun sendComment(postIdDomain: ContentIdDomain, jsonBody: String, tags: List<String>): CommentDomain

    fun recordPostView(postId: ContentIdDomain, deviceId: String)
}