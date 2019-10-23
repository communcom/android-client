package io.golos.data.api.discussions

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.commun4j.utils.Pair as CommunPair

interface DiscussionsApi {
    fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: FeedSort,
        timeFrame: FeedTimeFrame,
        sequenceKey: String? = null,
        tags: List<String>? = null
    ): GetDiscussionsResultRaw

    fun getPost(user: CyberName, permlink: Permlink): PostDiscussionRaw

    fun getUserSubscriptions(userId: String, limit: Int, sort: FeedSort, sequenceKey: String? = null): GetDiscussionsResultRaw

    fun getUserPost(userId: String, limit: Int, sort: FeedSort, sequenceKey: String? = null): GetDiscussionsResultRaw

    fun getCommentsOfPost(
        user: CyberName,
        permlink: Permlink,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String? = null
    ): GetDiscussionsResultRaw

    fun getComment(user: CyberName, permlink: Permlink): CommentDiscussionRaw

    fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: Permlink,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct>

    fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        communityId: CommunityId,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct>

    fun updatePost(
        postPermlink: Permlink,
        newTitle: String,
        newBody: String,
        newTags: List<Tag>,
        newJsonMetadata: DiscussionCreateMetadata
    ): CommunPair<TransactionCommitted<UpdatemssgComnGalleryStruct>, UpdatemssgComnGalleryStruct>

    fun deletePostOrComment(postOrCommentPermlink: Permlink): CommunPair<TransactionCommitted<DeletemssgComnGalleryStruct>, DeletemssgComnGalleryStruct>

    /**
     * Returns list of comments
     * @param postId - id of a post
     */
    fun getCommentsListForPost(offset: Int, pageSize: Int, postId: DiscussionIdModel): List<CommentDiscussionRaw>

    /**
     * Returns child comments for comment
     * @param parentCommentId - id of a parent comment
     */
    fun getCommentsListForComment(offset: Int, pageSize: Int, parentCommentId: DiscussionIdModel): List<CommentDiscussionRaw>
}