package io.golos.data.api.vote

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.VoteCGalleryStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.commun_entities.Permlink
import javax.inject.Inject

class VoteApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), VoteApi {

    override fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: Permlink,
        voteStrength: Short
    ): TransactionCommitted<VoteCGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        return TransactionCommitted<VoteCGalleryStruct>(
            "",
            TransactionProcessed("", TransactionParentReceipt("", 0, 0), 0, 0, true, listOf(), null, null),
            VoteCGalleryStruct(CyberSymbolCode(""), CyberName(""), MssgidCGalleryStruct(CyberName(""), ""), 0)
        )
//        return commun4j.vote(postOrCommentAuthor, postOrCommentPermlink, voteStrength, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow()
    }

    override fun unVote() {
            // do nothing in a stub
    }
}