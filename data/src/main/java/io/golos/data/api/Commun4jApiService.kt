package io.golos.data.api

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.comn.gallery.*
import io.golos.commun4j.abi.implementation.comn.social.AccountmetaComnSocialStruct
import io.golos.commun4j.abi.implementation.comn.social.PinComnSocialStruct
import io.golos.commun4j.abi.implementation.comn.social.UpdatemetaComnSocialStruct
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionParentReceipt
import io.golos.commun4j.http.rpc.model.transaction.response.TransactionProcessed
import io.golos.commun4j.model.*
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.AuthSecret
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.Either
import io.golos.data.errors.CyberServicesError
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import java.io.File
import java.util.*
import javax.inject.Inject
import io.golos.commun4j.utils.Pair as CommunPair

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
@ApplicationScope
open class Commun4jApiService
@Inject
constructor(private val commun4j: Commun4j) :
    PostsApiService,
    AuthApi,
    VoteApi,
    CommentsApiService,
    EmbedApi,
    DiscussionsCreationApi,
    RegistrationApi,
    SettingsApi,
    ImageUploadApi,
    EventsApi,
    UserMetadataApi,
    TransactionsApi,
    PushNotificationsApi {

    override fun getUserAccount(user: CyberName): UserProfile {
        return commun4j.getUserAccount(user).getOrThrow()
    }

    override fun getAuthSecret(): AuthSecret {
        return commun4j.getAuthSecret().getOrThrow()
    }

    override fun authWithSecret(user: String, secret: String, signedSecret: String): AuthResult {
        return commun4j.authWithSecret(user, secret, signedSecret).getOrThrow()
    }

    override fun resolveCanonicalCyberName(name: String): ResolvedProfile {
        return commun4j.resolveCanonicalCyberName(name, AppName.COMMUN).getOrThrow()
    }

    override fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: FeedSort,
        timeFrame: FeedTimeFrame,
        sequenceKey: String?,
        tags: List<String>?
    ): DiscussionsResult {
        // note[AS] it'll be "getPosts" method in a future. So far we use a stub
        return DiscussionsResult(listOf(), "")
        //return commun4j.getCommunityPosts(communityId, ContentParsingType.MOBILE, timeFrame, limit, sort, tags, sequenceKey).getOrThrow()
    }

    override fun getPost(user: CyberName, permlink: String): CyberDiscussion {
        return commun4j.getPost(user, "", permlink).getOrThrow()
    }

    override fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        // note[AS] no method so far. So we use a stub
        return DiscussionsResult(listOf(), "")
//        return commun4j.getUserSubscriptions(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey)
//            .getOrThrow()
    }

    override fun getUserPost(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        // note[AS] it'll be "getPosts" method in a future. So far we use a stub
        return DiscussionsResult(listOf(), "")

        //return commun4j.getUserPosts(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        // note[AS] it'll be "getComments" method in a future. So far we use a stub
        return DiscussionsResult(listOf(), "")
//        return commun4j.getCommentsOfPost(
//            user,
//            null,
//            permlink,
//            ContentParsingType.MOBILE,
//            limit,
//            sort,
//            sequenceKey
//        ).getOrThrow()
    }

    override fun getComment(user: CyberName, permlink: String): CyberDiscussion {
        // note[AS] it'll be "getComment" method in a future. So far we use a stub
        return CyberDiscussion(
            CyberDiscussionContent(
                CyberAttributes("", 0.0, ""),
                0L,
                "",
                listOf()),
            DiscussionVotes(0L, 0L),
            DiscussionMetadata(Date()),
            DiscussionId("", ""),
            DiscussionAuthor(CyberName(""), "", ""),
            CyberCommunity("", "", "")
        )

        // return DiscussionsResult(listOf(), "")
        // return commun4j.getComment(user, null, permlink, ContentParsingType.MOBILE).getOrThrow()
    }

    override fun setActiveUserCreds(user: CyberName, activeKey: String) {
        commun4j.keyStorage.addAccountKeys(user, setOf(CommunPair(AuthType.ACTIVE, activeKey)))
    }

    override fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        voteStrength: Short
    ): TransactionCommitted<VoteComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java
        return TransactionCommitted<VoteComnGalleryStruct>(
            "",
            TransactionProcessed("", TransactionParentReceipt("", 0, 0), 0, 0, true, listOf(), null, null),
            VoteComnGalleryStruct(CyberSymbolCode(""), CyberName(""), MssgidComnGalleryStruct(CyberName(""), ""), 0))
//        return commun4j.vote(postOrCommentAuthor, postOrCommentPermlink, voteStrength, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow()
    }

    override fun getIframelyEmbed(url: String): IFramelyEmbedResult {
        return commun4j.getEmbedIframely(url).getOrThrow()
    }

    override fun getOEmbedEmbed(url: String): OEmbedResult {
        return commun4j.getEmbedOembed(url).getOrThrow()
    }

    override fun createComment(
        body: String,
        parentAccount: CyberName,
        parentPermlink: String,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val createStruct = CreatemssgComnGalleryStruct(
            CyberSymbolCode(""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "",
            0
        )

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null),
                createStruct
            ),
            createStruct)

//        return commun4j.createComment(
//            body,
//            parentAccount,
//            parentPermlink,
//            category,
//            metadata,
//            null,
//            beneficiaries,
//            vestPayment,
//            tokenProp.toShort(),
//            null,
//            BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)
//        )
//        .getOrThrow()
//        .run { this to this.extractResult() }
    }

    override fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CommunPair<TransactionCommitted<CreatemssgComnGalleryStruct>, CreatemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val createStruct = CreatemssgComnGalleryStruct(
            CyberSymbolCode(""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "",
            0
        )

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null),
                createStruct
            ),
            createStruct)


//        return commun4j.createPost(
//            title,
//            body,
//            tags,
//            metadata,
//            null,
//            beneficiaries,
//            vestPayment,
//            tokenProp.toShort(),
//            null,
//            BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES)
//        )
//        .getOrThrow()
//        .run { this to this.extractResult() }
    }

    override fun updatePost(
        postPermlink: String,
        newTitle: String,
        newBody: String,
        newTags: List<Tag>,
        newJsonMetadata: DiscussionCreateMetadata
    ): CommunPair<TransactionCommitted<UpdatemssgComnGalleryStruct>, UpdatemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java
        val updateStruct = UpdatemssgComnGalleryStruct(
            CyberSymbolCode(""),
            MssgidComnGalleryStruct(CyberName(""), ""),
            "",
            "",
            listOf(),
            "")

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null), updateStruct),
            updateStruct)

//        return commun4j.updatePost(postPermlink, newTitle, newBody, newTags, newJsonMetadata, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run { this to this.extractResult() }
    }

    override fun deletePostOrComment(postOrCommentPermlink: String):
            CommunPair<TransactionCommitted<DeletemssgComnGalleryStruct>, DeletemssgComnGalleryStruct> {
        // It's the BC method
        // We can wait for Yury or get Max's implementation from here:
        // https://github.com/communcom/communTestKit/blob/master/src/main/java/commun_test/communHelpers.java

        val deleteStruct = DeletemssgComnGalleryStruct(CyberSymbolCode(""), MssgidComnGalleryStruct(CyberName(""), ""))

        return CommunPair(
            TransactionCommitted(
                "",
                TransactionProcessed(
                    "",
                    TransactionParentReceipt("", 0, 0),
                    0, 0, true, listOf(), null, null), deleteStruct),
            deleteStruct)

//        return commun4j.deletePostOrComment(postOrCommentPermlink, BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES))
//            .getOrThrow().run {
//                this to this.extractResult()
//            }
    }

    override fun getRegistrationState(phone: String): UserRegistrationStateResult {
        return commun4j.getRegistrationState(null, phone).getOrThrow()
    }

    override fun firstUserRegistrationStep(phone: String, testingPass: String?): FirstRegistrationStepResult {
        return commun4j.firstUserRegistrationStep(null, phone, testingPass).getOrThrow()
    }

    override fun verifyPhoneForUserRegistration(phone: String, code: Int): ResultOk {
        return commun4j.verifyPhoneForUserRegistration(phone, code).getOrThrow()
    }

    override fun setVerifiedUserName(user: String, phone: String): ResultOk {
        return commun4j.setVerifiedUserName(user, phone).getOrThrow()
    }

    override fun writeUserToBlockChain(
        userName: String,
        owner: String,
        active: String,
        posting: String,
        memo: String
    ): RegisterResult {
        return commun4j.writeUserToBlockChain(userName, owner, active, posting, memo).getOrThrow()
    }

    override fun resendSmsCode(phone: String): ResultOk {
        return commun4j.resendSmsCode(phone).getOrThrow()
    }

    override fun uploadImage(file: File): String {
        return commun4j.uploadImage(file).getOrThrow()
    }

    override fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk {
        return commun4j.setUserSettings(deviceId, basic, null, null).getOrThrow()
    }

    override fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk {
        return commun4j.setUserSettings(deviceId, null, null, notifSettings).getOrThrow()
    }

    override fun getSettings(deviceId: String): UserSettings {
        return commun4j.getUserSettings(deviceId).getOrThrow()
    }

    override fun getEvents(
        userProfile: String,
        afterId: String?,
        limit: Int?,
        markAsViewed: Boolean?,
        freshOnly: Boolean?,
        types: List<EventType>
    ): EventsData {
        return commun4j.getEvents(userProfile, afterId, limit, markAsViewed, freshOnly, types).getOrThrow()
    }

    override fun markEventsAsNotFresh(ids: List<String>): ResultOk {
        return commun4j.markEventsAsNotFresh(ids).getOrThrow()
    }

    override fun markAllEventsAsNotFresh(): ResultOk {
        return commun4j.markAllEventsAsNotFresh().getOrThrow()
    }

    override fun getFreshNotifsCount(profileId: String): FreshResult {
        return commun4j.getFreshNotificationCount(profileId).getOrThrow()
    }

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
            AccountmetaComnSocialStruct("", "", "", "", "", "", ""))

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

    override fun getUserMetadata(user: CyberName): GetProfileResult {
        return commun4j.getUserProfile(user, null).getOrThrow()
    }

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

    override fun waitForTransaction(transactionId: String): ResultOk {
        return commun4j.waitForTransaction(transactionId).getOrThrow()
    }

    override fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk {
        return commun4j.subscribeOnMobilePushNotifications(deviceId, fcmToken, AppName.GLS).getOrThrow()
    }

    override fun unSubscribeOnNotifications(userId: CyberName, deviceId: String): ResultOk {
        return commun4j.unSubscribeOnNotifications(userId, deviceId, AppName.GLS).getOrThrow()
    }

    protected fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)

    private fun <T> TransactionCommitted<T>.extractResult() = this.resolvedResponse ?:
    throw IllegalStateException("cannot extract result form transaction")
}