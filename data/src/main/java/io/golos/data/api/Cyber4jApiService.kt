package io.golos.data.api

import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.golos.abi.implementation.publish.CreatemssgPublishStruct
import io.golos.abi.implementation.publish.DeletemssgPublishStruct
import io.golos.abi.implementation.publish.UpdatemssgPublishStruct
import io.golos.abi.implementation.publish.VotePublishStruct
import io.golos.abi.implementation.social.PinSocialStruct
import io.golos.abi.implementation.social.UpdatemetaSocialStruct
import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.model.*
import io.golos.cyber4j.services.model.*
import io.golos.cyber4j.utils.Pair
import io.golos.data.errors.CyberServicesError
import io.golos.sharedmodel.CyberName
import io.golos.sharedmodel.Either
import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
class Cyber4jApiService(private val cyber4j: Cyber4J) : PostsApiService,
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
        return cyber4j.getUserAccount(user).getOrThrow()
    }

    override fun getAuthSecret(): AuthSecret {
        return cyber4j.getAuthSecret().getOrThrow()
    }

    override fun authWithSecret(user: String, secret: String, signedSecret: String): AuthResult {
        return cyber4j.authWithSecret(user, secret, signedSecret).getOrThrow()
    }

    override fun resolveCanonicalCyberName(name: String): ResolvedProfile {
        return cyber4j.resolveCanonicalCyberName(name, AppName.GLS).getOrThrow()
    }

    override fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: FeedSort,
        timeFrame: FeedTimeFrame,
        tags: List<String>,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getCommunityPosts(communityId, ContentParsingType.MOBILE, timeFrame, limit, sort, tags, sequenceKey).getOrThrow()
    }

    override fun getPost(user: CyberName, permlink: String): CyberDiscussion {
        return cyber4j.getPost(user, null, permlink, ContentParsingType.MOBILE).getOrThrow()
    }

    override fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserSubscriptions(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey)
            .getOrThrow()
    }

    override fun getUserPost(
        userId: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserPosts(CyberName(userId), null, ContentParsingType.MOBILE, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        limit: Int,
        sort: FeedSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getCommentsOfPost(
            user,
            null,
            permlink,
            ContentParsingType.MOBILE,
            limit,
            sort,
            sequenceKey
        ).getOrThrow()
    }

    override fun getComment(user: CyberName, permlink: String): CyberDiscussion {
        return cyber4j.getComment(user, null, permlink, ContentParsingType.MOBILE).getOrThrow()
    }

    override fun setActiveUserCreds(user: CyberName, activeKey: String) {
        cyber4j.keyStorage.addAccountKeys(user, setOf(Pair(AuthType.ACTIVE, activeKey)))
    }

    override fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        voteStrength: Short
    ): TransactionCommitted<VotePublishStruct> {
        return cyber4j.vote(postOrCommentAuthor, postOrCommentPermlink, voteStrength)
            .getOrThrow()
    }

    override fun getIframelyEmbed(url: String): IFramelyEmbedResult {
        return cyber4j.getEmbedIframely(url).getOrThrow()
    }

    override fun getOEmbedEmbed(url: String): OEmbedResult {
        return cyber4j.getEmbedOembed(url).getOrThrow()
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
    ): kotlin.Pair<TransactionCommitted<CreatemssgPublishStruct>, CreatemssgPublishStruct> {
        return cyber4j.createComment(
            body,
            parentAccount,
            parentPermlink,
            category,
            metadata,
            null,
            beneficiaries,
            vestPayment,
            tokenProp.toShort(),
            null
        )
        .getOrThrow()
        .run { this to this.extractResult() }
    }

    override fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): kotlin.Pair<TransactionCommitted<CreatemssgPublishStruct>, CreatemssgPublishStruct> {
        return cyber4j.createPost(
            title,
            body,
            tags,
            metadata,
            null,
            beneficiaries,
            vestPayment,
            tokenProp.toShort(),
            null
        )
        .getOrThrow()
        .run { this to this.extractResult() }
    }

    override fun updatePost(
        postPermlink: String,
        newTitle: String,
        newBody: String,
        newTags: List<Tag>,
        newJsonMetadata: DiscussionCreateMetadata
    ): kotlin.Pair<TransactionCommitted<UpdatemssgPublishStruct>, UpdatemssgPublishStruct> {
        return cyber4j.updatePost(postPermlink, newTitle, newBody, newTags, newJsonMetadata)
            .getOrThrow().run { this to this.extractResult() }
    }

    override fun deletePostOrComment(postOrCommentPermlink: String): kotlin.Pair<TransactionCommitted<DeletemssgPublishStruct>, DeletemssgPublishStruct> {
        return cyber4j.deletePostOrComment(postOrCommentPermlink)
            .getOrThrow().run {
                this to this.extractResult()
            }
    }

    override fun getRegistrationState(phone: String): UserRegistrationStateResult {
        return cyber4j.getRegistrationState(null, phone).getOrThrow()
    }

    override fun firstUserRegistrationStep(phone: String, testingPass: String?): FirstRegistrationStepResult {
        return cyber4j.firstUserRegistrationStep(null, phone, testingPass).getOrThrow()
    }

    override fun verifyPhoneForUserRegistration(phone: String, code: Int): ResultOk {
        return cyber4j.verifyPhoneForUserRegistration(phone, code).getOrThrow()
    }

    override fun setVerifiedUserName(user: String, phone: String): ResultOk {
        return cyber4j.setVerifiedUserName(user, phone).getOrThrow()
    }

    override fun writeUserToBlockChain(
        userName: String,
        owner: String,
        active: String,
        posting: String,
        memo: String
    ): RegisterResult {
        return cyber4j.writeUserToBlockChain(userName, owner, active, posting, memo).getOrThrow()
    }

    override fun resendSmsCode(phone: String): ResultOk {
        return cyber4j.resendSmsCode(phone).getOrThrow()
    }

    override fun uploadImage(file: File): String {
        return cyber4j.uploadImage(file).getOrThrow()
    }

    override fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk {
        return cyber4j.setUserSettings(deviceId, basic, null, null).getOrThrow()
    }

    override fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk {
        return cyber4j.setUserSettings(deviceId, null, null, notifSettings).getOrThrow()
    }

    override fun getSettings(deviceId: String): UserSettings {
        return cyber4j.getUserSettings(deviceId).getOrThrow()
    }

    override fun getEvents(
        userProfile: String,
        afterId: String?,
        limit: Int?,
        markAsViewed: Boolean?,
        freshOnly: Boolean?,
        types: List<EventType>
    ): EventsData {
        return cyber4j.getEvents(userProfile, afterId, limit, markAsViewed, freshOnly, types).getOrThrow()
    }

    override fun markEventsAsNotFresh(ids: List<String>): ResultOk {
        return cyber4j.markEventsAsNotFresh(ids).getOrThrow()
    }

    override fun markAllEventsAsNotFresh(): ResultOk {
        return cyber4j.markAllEventsAsNotFresh().getOrThrow()
    }

    override fun getFreshNotifsCount(profileId: String): FreshResult {
        return cyber4j.getFreshNotificationCount(profileId).getOrThrow()
    }

    override fun setUserMetadata(
        about: String?,
        coverImage: String?,
        profileImage: String?
    ): TransactionCommitted<UpdatemetaSocialStruct> {
        return cyber4j.setUserMetadata(
            about = about,
            coverImage = coverImage,
            profileImage = profileImage
        ).getOrThrow()
    }

    override fun getUserMetadata(user: CyberName): UserMetadataResult {
        return cyber4j.getUserMetadata(user, null).getOrThrow()
    }

    override fun pin(user: CyberName): kotlin.Pair<TransactionCommitted<PinSocialStruct>, PinSocialStruct> {
        return cyber4j.pin(user).getOrThrow().run { this to this.extractResult() }
    }

    override fun unPin(user: CyberName): kotlin.Pair<TransactionCommitted<PinSocialStruct>, PinSocialStruct> {
        return cyber4j.unPin(user).getOrThrow().run { this to this.extractResult() }
    }

    override fun waitForTransaction(transactionId: String): ResultOk {
        return cyber4j.waitForTransaction(transactionId).getOrThrow()
    }

    override fun subscribeOnMobilePushNotifications(deviceId: String, fcmToken: String): ResultOk {
        return cyber4j.subscribeOnMobilePushNotifications(deviceId, fcmToken, AppName.GLS).getOrThrow()
    }

    override fun unSubscribeOnNotifications(userId: CyberName, deviceId: String): ResultOk {
        return cyber4j.unSubscribeOnNotifications(userId, deviceId, AppName.GLS).getOrThrow()
    }

    private fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)

    private fun <T> TransactionCommitted<T>.extractResult() = this.resolvedResponse ?:
    throw IllegalStateException("cannot extract result form transaction")
}