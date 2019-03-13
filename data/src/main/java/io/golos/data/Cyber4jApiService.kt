package io.golos.data

import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.DiscussionTimeSort
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.cyber4j.utils.Either
import io.golos.data.errors.CyberServicesError

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
class Cyber4jApiService(private val cyber4j: Cyber4J) : PostsApiService {

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

    private fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
        (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)
}