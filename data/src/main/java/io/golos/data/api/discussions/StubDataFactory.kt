package io.golos.data.api.discussions
import io.golos.commun4j.abi.implementation.comn.gallery.CreatemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.DeletemssgComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.MssgidComnGalleryStruct
import io.golos.commun4j.abi.implementation.comn.gallery.UpdatemssgComnGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.CyberCommunity
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.domain.commun_entities.Community
import io.golos.domain.commun_entities.CommunityId
import java.util.*
import io.golos.commun4j.utils.Pair as CommunPair

internal object StubDataFactory {
    private var permlinkCounter = 0

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

    fun getEmptyCreatemssgComnGalleryStruct(): CreatemssgComnGalleryStruct =
        CreatemssgComnGalleryStruct(
            CyberSymbolCode(""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "",
            0,
            0
        )

    fun getEmptyUpdatemssgComnGalleryStruct(): UpdatemssgComnGalleryStruct =
        UpdatemssgComnGalleryStruct(
            CyberSymbolCode(""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "")

    fun getEmptyDeletemssgComnGalleryStruct(): DeletemssgComnGalleryStruct =
        DeletemssgComnGalleryStruct(CyberSymbolCode(""), MssgidComnGalleryStruct(CyberName(""), ""))

    fun createPost(body: String, community: Community, userId: String): CyberDiscussionRaw =
        CyberDiscussionRaw(
            body,
            DiscussionVotes(0, 0),
            DiscussionMetadata(Date()),
            DiscussionId(userId, "pl${permlinkCounter++}"),
            DiscussionAuthor(CyberName(userId), "some user", "https://pickaface.net/gallery/avatar/centurypixel5229a9f0ae77f.png"),
            CyberCommunity(community.id.id, community.name, community.logoUrl)
        )
}