@file:Suppress("PropertyName")

package io.golos.domain.model

import io.golos.domain.entities.DiscussionsSort

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

interface FeedUpdateRequest : Identifiable {
    val pageKey: String?
}

sealed class PostFeedUpdateRequest : FeedUpdateRequest

sealed class CommentFeedUpdateRequest : FeedUpdateRequest

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

    inner class Id : Identifiable.Id() {
        val _id = communityId
        val _sort = sort
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_id != other._id) return false
            if (_sort != other._sort) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _id.hashCode()
            result = 31 * result + _sort.hashCode()
            return result
        }
    }

    override val id = Id()
    override val pageKey: String?
        get() = sequenceKey

}

data class UserSubscriptionsFeedUpdateRequest(
    val userId: String,
    val limit: Int,
    val sort: DiscussionsSort,
    val sequenceKey: String? = null
) : PostFeedUpdateRequest() {

    override val id = Id()
    override val pageKey: String?
        get() = sequenceKey

    inner class Id : Identifiable.Id() {
        val _id = userId
        val _sort = sort
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_id != other._id) return false
            if (_sort != other._sort) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _id.hashCode()
            result = 31 * result + _sort.hashCode()
            return result
        }


    }
}

data class UserPostsUpdateRequest(
    val userId: String,
    val limit: Int,
    val sort: DiscussionsSort,
    val sequenceKey: String? = null
) : PostFeedUpdateRequest() {

    override val id = Id()
    override val pageKey: String?
        get() = sequenceKey

    inner class Id : Identifiable.Id() {
        val _id = userId
        val _sort = sort
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_id != other._id) return false
            if (_sort != other._sort) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _id.hashCode()
            result = 31 * result + _sort.hashCode()
            return result
        }
    }
}

data class CommentsOfApPostUpdateRequest(
    val user: String,
    val permlink: String,
    val refBlockNum: Long,
    val limit: Int,
    val sort: DiscussionsSort,
    val sequenceKey: String? = null
) : CommentFeedUpdateRequest() {

    override val id: Identifiable.Id
        get() = Id()
    override val pageKey: String?
        get() = sequenceKey

    inner class Id : Identifiable.Id() {
        val _user = user
        val _permlink = permlink
        val _refBlockNum = refBlockNum
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_user != other._user) return false
            if (_permlink != other._permlink) return false
            if (_refBlockNum != other._refBlockNum) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _user.hashCode()
            result = 31 * result + _permlink.hashCode()
            result = 31 * result + _refBlockNum.hashCode()
            return result
        }
    }
}








