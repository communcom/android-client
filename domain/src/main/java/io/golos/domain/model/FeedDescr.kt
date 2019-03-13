package io.golos.domain.model

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
/** interface represents some identifiable essence.
 * Purpose of it - give a descendant class way of
 * comparing themselves in a decoupled way.
 *
 * */
interface Identifiable {
    val id: Id

    /** represents a way to associate or identify entities
     * in a typesafe way
     * */
    abstract class Id
}

sealed class PostFeedUpdateRequest : Identifiable

sealed class CommentFeedpdateRequest : Identifiable

data class CommunityFeedUpdateRequest(
    val communityId: String,
    val limit: Int,
    val sort: DiscussionsSort,
    val sequenceKey: String? = null
) : PostFeedUpdateRequest() {
    init {
        if (limit < 0) throw IllegalStateException("cannot fetch less then zero posts, limit = $limit")
        if (communityId.isEmpty()) throw IllegalStateException("communityId cannot be empty")
    }

    override val id = object : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CommunityFeedUpdateRequest

            if (communityId != other.communityId) return false
            if (sort != other.sort) return false

            return true
        }

        override fun hashCode(): Int {
            var result = communityId.hashCode()
            result = 31 * result + sort.hashCode()
            return result
        }
    }
}

