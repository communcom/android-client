package io.golos.data.api.discussions

import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.RemoveCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.UpdateCGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.Beneficiary
import io.golos.commun4j.model.DiscussionCreateMetadata
import io.golos.commun4j.model.GetDiscussionsResultRaw
import io.golos.commun4j.model.Tag
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.CommentDiscussionRaw
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
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
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct>

    fun createComment(
        commentContentAsJson: String,
        parentId: DiscussionIdModel,
        commentAuthor: DiscussionAuthorModel,
        commentPermlink: Permlink
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct>

    fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        communityId: CommunityId,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CommunPair<TransactionCommitted<CreateCGalleryStruct>, CreateCGalleryStruct>

    fun updatePost(
        postPermlink: Permlink,
        newTitle: String,
        newBody: String,
        newTags: List<Tag>,
        newJsonMetadata: DiscussionCreateMetadata
    ): CommunPair<TransactionCommitted<UpdateCGalleryStruct>, UpdateCGalleryStruct>

    fun deletePost(postPermlink: Permlink): CommunPair<TransactionCommitted<RemoveCGalleryStruct>, RemoveCGalleryStruct>

    fun deleteComment(commentPermlink: Permlink): CommunPair<TransactionCommitted<RemoveCGalleryStruct>, RemoveCGalleryStruct>
}