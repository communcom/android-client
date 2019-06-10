package io.golos.domain.requestmodel

import io.golos.domain.Entity
import io.golos.domain.interactors.model.DiscussionIdModel

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreationRequestEntity : Identifiable, Entity

data class PostCreationRequestEntity(
    val title: String,
    val body: String,
    val originalBody: CharSequence,
    val tags: List<String>
) : DiscussionCreationRequestEntity() {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _title = title
        val _body = body
        val _communitites = tags
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_title != other._title) return false
            if (_body != other._body) return false
            if (_communitites != other._communitites) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _title.hashCode()
            result = 31 * result + _body.hashCode()
            result = 31 * result + _communitites.hashCode()
            return result
        }

    }
}

data class CommentCreationRequestEntity(
    val body: String,
    val parentId: DiscussionIdModel,
    val tags: List<String>
) : DiscussionCreationRequestEntity() {
    private val _id = Id()
    override val id: Identifiable.Id
        get() = _id


    inner class Id : Identifiable.Id() {
        val _body = body
        val _parentId = parentId
        val _tags = tags
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_body != other._body) return false
            if (_parentId != other._parentId) return false
            if (_tags != other._tags) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _body.hashCode()
            result = 31 * result + _parentId.hashCode()
            result = 31 * result + _tags.hashCode()
            return result
        }
    }
}


data class DeleteDiscussionRequestEntity(val discussionPermlink: String): DiscussionCreationRequestEntity() {
    override val id = Id()

    inner class Id : Identifiable.Id() {
        val _body = discussionPermlink

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_body != other._body) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _body.hashCode()
            return result
        }
    }
}

data class PostUpdateRequestEntity(
    val postPermlink: String,
    val title: String,
    val body: String,
    val originalBody: CharSequence,
    val tags: List<String>
) : DiscussionCreationRequestEntity() {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _title = title
        val _body = body
        val _tags = tags
        val _postPermlink = postPermlink

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_title != other._title) return false
            if (_body != other._body) return false
            if (_tags != other._tags) return false
            if (_postPermlink != other._postPermlink) return false

            return true
        }

        override fun hashCode(): Int {
            var result = _title.hashCode()
            result = 31 * result + _body.hashCode()
            result = 31 * result + _tags.hashCode()
            result = 31 * result + _postPermlink.hashCode()
            return result
        }

    }
}
