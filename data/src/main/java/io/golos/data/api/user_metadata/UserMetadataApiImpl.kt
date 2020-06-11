package io.golos.data.api.user_metadata

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.social.AccountmetaCSocialStruct
import io.golos.commun4j.abi.implementation.c.social.PinCSocialStruct
import io.golos.commun4j.abi.implementation.c.social.UpdatemetaCSocialStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.commun_entities.GetProfileResultExt
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
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
    ): TransactionCommitted<UpdatemetaCSocialStruct> {

        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = UpdatemetaCSocialStruct(
            CyberName(""),
            AccountmetaCSocialStruct(
                avatar_url = "",
                biography = "",
                cover_url = "",
                facebook = "",
                telegram = "",
                whatsapp = "",
                wechat = "",
                first_name = null,
                last_name = null,
                country = null,
                city = null,
                birth_date = null,
                instagram = null,
                linkedin = null,
                twitter = null,
                github = null,
                website_url = null)
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

    override fun getUserMetadata(user: UserIdDomain): GetProfileResultExt =
        GetProfileResultExt(
            commun4j.getUserProfile(CyberName(user.userId), null).getOrThrow(),
            "https://pickaface.net/gallery/avatar/centurypixel5229a9f0ae77f.png")       // Fake

    override fun pin(user: CyberName): CommunPair<TransactionCommitted<PinCSocialStruct>, PinCSocialStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = PinCSocialStruct(CyberName(""), CyberName(""))

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

    override fun unPin(user: CyberName): CommunPair<TransactionCommitted<PinCSocialStruct>, PinCSocialStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val struct = PinCSocialStruct(CyberName(""), CyberName(""))

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