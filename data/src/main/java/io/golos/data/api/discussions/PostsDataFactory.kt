package io.golos.data.api.discussions

import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.RemoveCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.UpdateCGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.CyberCommunity
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.commun_entities.PostDiscussionRaw
import java.util.*
import io.golos.commun4j.utils.Pair as CommunPair

internal object PostsDataFactory {
    fun <T>createCommitedTransaction(struct: T): CommunPair<TransactionCommitted<T>, T> {
        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null),
                struct
            ),
            struct)
    }

    fun getCreatemssgComnGalleryStruct(userId: String, permlink: String): CreateCGalleryStruct =
        CreateCGalleryStruct(
            CyberSymbolCode(""),
            MssgidCGalleryStruct(CyberName(userId), permlink),
            MssgidCGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "",
            0
        )

    fun getUpdatemssgComnGalleryStruct(userId: String, permlink: Permlink): UpdateCGalleryStruct =
        UpdateCGalleryStruct(
            CyberSymbolCode(""),
            MssgidCGalleryStruct(CyberName(userId), permlink.value),
            "",
            "",
            listOf(),
            "")

    fun getDeletemssgComnGalleryStruct(userId: String, permlink: Permlink): RemoveCGalleryStruct =
        RemoveCGalleryStruct(CyberSymbolCode(""), MssgidCGalleryStruct(CyberName(userId), permlink.value))

    fun createPost(body: String, community: Community, userId: String, commentsCount: Long): PostDiscussionRaw =
        PostDiscussionRaw(
            body,
            DiscussionVotes(0, 0),
            DiscussionMetadata(Date()),
            DiscussionId(CyberName(userId), community.id.id, Permlink.generate().value),
            CyberCommunity(community.id.id, community.name, community.logoUrl, "alias"),
            DiscussionAuthor(CyberName(userId), "some user", "https://pickaface.net/gallery/avatar/centurypixel5229a9f0ae77f.png"),
            commentsCount
        )
}