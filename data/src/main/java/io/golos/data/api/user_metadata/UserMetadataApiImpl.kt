package io.golos.data.api.user_metadata

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.comn.social.AccountmetaComnSocialStruct
import io.golos.commun4j.abi.implementation.comn.social.PinComnSocialStruct
import io.golos.commun4j.abi.implementation.comn.social.UpdatemetaComnSocialStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.services.model.GetProfileResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.commun_entities.GetProfileResultExt
import javax.inject.Inject
import io.golos.commun4j.utils.Pair as CommunPair

class UserMetadataApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), UserMetadataApi {
    override fun setUserMetadata(
        about: String?,
        coverImage: String?,
        profileImage: String?
    ): TransactionCommitted<UpdatemetaComnSocialStruct> {

        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = UpdatemetaComnSocialStruct(
            CyberName(""),
            AccountmetaComnSocialStruct("", "", "", "", "", "", "")
        )

        return TransactionCommitted(
            "",
            TransactionProcessed(
                "",
                TransactionParentReceipt("", 0, 0),
                0, 0, true, listOf(), null, null), struct)

//        return commun4j.setUserMetadata(
//            about = about,
//            coverImage = coverImage,
//            profileImage = profileImage,
//            bandWidthRequest = BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)
//        ).getOrThrow()
    }

    override fun getUserMetadata(user: CyberName): GetProfileResultExt =
        GetProfileResultExt(
            commun4j.getUserProfile(user, null).getOrThrow(),
            "https://pickaface.net/gallery/avatar/centurypixel5229a9f0ae77f.png")       // Fake

    override fun pin(user: CyberName): CommunPair<TransactionCommitted<PinComnSocialStruct>, PinComnSocialStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = PinComnSocialStruct(CyberName(""), CyberName(""))

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null), struct),
            struct)

        //return commun4j.pin(user, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)).getOrThrow().run { this to this.extractResult() }
    }

    override fun unPin(user: CyberName): CommunPair<TransactionCommitted<PinComnSocialStruct>, PinComnSocialStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = PinComnSocialStruct(CyberName(""), CyberName(""))

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null), struct),
            struct)

//        return commun4j.unPin(user, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)).getOrThrow().run { this to this.extractResult() }
    }
}