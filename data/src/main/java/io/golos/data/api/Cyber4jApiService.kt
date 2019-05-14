package io.golos.data.api

import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.model.*
import io.golos.cyber4j.services.model.*
import io.golos.cyber4j.utils.Either
import io.golos.cyber4j.utils.Pair
import io.golos.data.errors.CyberServicesError
import java.io.File
import java.util.*
import kotlin.collections.HashSet

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
class Cyber4jApiService(private val cyber4j: Cyber4J) : PostsApiService,
    AuthApi,
    AuthListener,
    VoteApi,
    CommentsApiService,
    EmbedApi,
    DiscussionsCreationApi,
    RegistrationApi,
    SettingsApi,
    ImageUploadApi,
    EventsApi,
    UserMetadataApi {
    private val listeners = Collections.synchronizedSet(HashSet<AuthListener>())

    init {
        cyber4j.addAuthListener(this)
    }

    override fun onAuthSuccess(forUser: CyberName) {
        listeners.forEach { it.onAuthSuccess(forUser) }
    }

    override fun getUserAccount(user: CyberName): UserProfile {
        return cyber4j.getUserAccount(user).getOrThrow()
    }

    override fun onFail(e: Exception) {
        listeners.forEach { it.onFail(e) }
    }

    override fun getCommunityPosts(
        communityId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getCommunityPosts(communityId, ContentParsingType.MOBILE, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getPost(user: CyberName, permlink: String, refBlockNum: Long): CyberDiscussion {
        return cyber4j.getPost(user, permlink, refBlockNum, ContentParsingType.MOBILE).getOrThrow()
    }

    override fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserSubscriptions(CyberName(userId), ContentParsingType.MOBILE, limit, sort, sequenceKey)
            .getOrThrow()
    }

    override fun getUserPost(
        userId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserPosts(CyberName(userId), ContentParsingType.MOBILE, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        refBlockNum: Long,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getCommentsOfPost(
            user,
            permlink,
            refBlockNum,
            ContentParsingType.MOBILE,
            limit,
            sort,
            sequenceKey
        ).getOrThrow()
    }

    override fun getComment(user: CyberName, permlink: String, refBlockNum: Long): CyberDiscussion {
        return cyber4j.getComment(user, permlink, refBlockNum, ContentParsingType.MOBILE).getOrThrow()
    }

    override fun setActiveUserCreds(user: CyberName, activeKey: String) {
        cyber4j.keyStorage.addAccountKeys(user, setOf(Pair(AuthType.ACTIVE, activeKey)))
    }

    override fun addAuthListener(listener: AuthListener) {
        listeners.add(listener)
    }

    override fun vote(
        postOrCommentAuthor: CyberName,
        postOrCommentPermlink: String,
        postOrCommentRefBlockNum: Long,
        voteStrength: Short
    ): VoteResult {
        return cyber4j.vote(postOrCommentAuthor, postOrCommentPermlink, postOrCommentRefBlockNum, voteStrength)
            .getOrThrow().extractResult()
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
        parentDiscussionRefBlockNum: Long,
        category: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CreateDiscussionResult {
        return cyber4j.createComment(
            body, parentAccount, parentPermlink, parentDiscussionRefBlockNum,
            category, metadata, null, beneficiaries, vestPayment, tokenProp
        ).getOrThrow().extractResult()
    }

    override fun createPost(
        title: String,
        body: String,
        tags: List<Tag>,
        metadata: DiscussionCreateMetadata,
        beneficiaries: List<Beneficiary>,
        vestPayment: Boolean,
        tokenProp: Long
    ): CreateDiscussionResult {
        return cyber4j.createPost(title, body, tags, metadata, null, beneficiaries, vestPayment, tokenProp).getOrThrow()
            .extractResult()
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

    override fun setVerifiedUserName(user: CyberName, phone: String): ResultOk {
        return cyber4j.setVerifiedUserName(user, phone).getOrThrow()
    }

    override fun writeUserToBlockChain(
        userName: CyberName,
        owner: String,
        active: String,
        posting: String,
        memo: String
    ): ResultOk {
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
        type: String?,
        app: String?,
        email: String?,
        phone: String?,
        facebook: String?,
        instagram: String?,
        telegram: String?,
        vk: String?,
        website: String?,
        first_name: String?,
        last_name: String?,
        name: String?,
        birthDate: String?,
        gender: String?,
        location: String?,
        city: String?,
        about: String?,
        occupation: String?,
        iCan: String?,
        lookingFor: String?,
        businessCategory: String?,
        backgroundImage: String?,
        coverImage: String?,
        profileImage: String?,
        userImage: String?,
        icoAddress: String?,
        targetDate: String?,
        targetPlan: String?,
        targetPointA: String?,
        targetPointB: String?
    ): ProfileMetadataUpdateResult {
        return cyber4j.setUserMetadata(
            type,
            app,
            email,
            phone,
            facebook,
            instagram,
            telegram,
            vk,
            website,
            first_name,
            last_name,
            name,
            birthDate,
            gender,
            location,
            city,
            about,
            occupation,
            iCan,
            lookingFor,
            businessCategory,
            backgroundImage,
            coverImage,
            profileImage,
            userImage,
            icoAddress,
            targetDate,
            targetPlan,
            targetPointA,
            targetPointB
        ).getOrThrow().extractResult()
    }

    override fun getUserMetadata(user: CyberName): UserMetadataResult {
        return cyber4j.getUserMetadata(user).getOrThrow()
    }

    private fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)

    private fun <T> TransactionSuccessful<T>.extractResult() = this.processed.action_traces.first().act.data
}