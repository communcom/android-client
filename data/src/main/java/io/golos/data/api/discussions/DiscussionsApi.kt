package io.golos.data.api.discussions

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
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

    fun getPost(user: CyberName, permlink: String): CyberDiscussionRaw

    fun getUserSubscriptions(userId: String, limit: Int, sort: FeedSort, sequenceKey: String? = null): GetDiscussionsResultRaw

    fun getUserPost(userId: String, limit: Int, sort: FeedSort, sequenceKey: String? = null): GetDiscussionsResultRaw

    fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String? = null
    ): GetDiscussionsResultRaw

    fun getComment(user: CyberName, permlink: String): CyberDiscussionRaw

    fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: String,
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
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary> = emptyList(),
        vestPayment: Boolean = true,
        tokenProp: Long = 0L
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct>

    fun updatePost(postPermlink: String,
                   newTitle: String,
                   newBody: String,
                   newTags: List<Tag>,
                   newJsonMetadata: DiscussionCreateMetadata
    ): CommunPair<TransactionCommitted<UpdatemssgComnGalleryStruct>, UpdatemssgComnGalleryStruct>

    fun deletePostOrComment(postOrCommentPermlink: String): CommunPair<TransactionCommitted<DeletemssgComnGalleryStruct>, DeletemssgComnGalleryStruct>

}