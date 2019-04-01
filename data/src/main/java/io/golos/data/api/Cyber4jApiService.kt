package io.golos.data.api

import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.model.*
import io.golos.cyber4j.utils.Either
import io.golos.cyber4j.utils.Pair
import io.golos.data.errors.CyberServicesError
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
    EmbedApi {
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
        return cyber4j.getCommunityPosts(communityId, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getPost(user: CyberName, permlink: String, refBlockNum: Long): CyberDiscussion {
        return cyber4j.getPost(user, permlink, refBlockNum).getOrThrow()
    }

    override fun getUserSubscriptions(
        userId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserSubscriptions(CyberName(userId), limit, sort, sequenceKey).getOrThrow()
    }

    override fun getUserPost(
        userId: String,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getUserPosts(CyberName(userId), limit, sort, sequenceKey).getOrThrow()
    }

    override fun getCommentsOfPost(
        user: CyberName,
        permlink: String,
        refBlockNum: Long,
        limit: Int,
        sort: DiscussionTimeSort,
        sequenceKey: String?
    ): DiscussionsResult {
        return cyber4j.getCommentsOfPost(user, permlink, refBlockNum, limit, sort, sequenceKey).getOrThrow()
    }

    override fun getComment(user: CyberName, permlink: String, refBlockNum: Long): CyberDiscussion {
        return cyber4j.getComment(user, permlink, refBlockNum).getOrThrow()
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

    private fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)

    private fun <T> TransactionSuccessful<T>.extractResult() = this.processed.action_traces.first().act.data
}