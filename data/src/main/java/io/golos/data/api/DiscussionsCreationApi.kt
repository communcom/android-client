package io.golos.data.api

import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.model.Beneficiary
import io.golos.commun4j.model.DiscussionCreateMetadata
import io.golos.commun4j.model.Tag
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.Pair as CommunPair

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
interface DiscussionsCreationApi {

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

    fun deletePostOrComment(postOrCommentPermlink: String):
            CommunPair<TransactionCommitted<DeletemssgComnGalleryStruct>, DeletemssgComnGalleryStruct>
}