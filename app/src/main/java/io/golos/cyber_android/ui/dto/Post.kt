package io.golos.cyber_android.ui.dto

import java.util.*

data class Post(

    val author: Author,
    val community: Community,
    val contentId: ContentId,
    val document: Document?,
    val meta: Meta,
    val stats: Stats?,
    val type: String?,
    val votes: Votes
) {
    data class Votes(
        val downCount: Long,
        val upCount: Long,
        val hasUpVote: Boolean,
        val hasDownVote: Boolean
    )

    data class Meta(
        val creationTime: Date
    )

    data class Document(
        val attributes: Attributes,
        val content: List<Content>,
        val id: String,
        val type: String
    ) {
        data class Content(
            val contentBodyList: List<ContentBody>,
            val id: String,
            val type: String
        ) {
            data class ContentBody(
                val content: String,
                val id: String,
                val type: String
            )
        }

        data class Attributes(
            val type: String,
            val version: String
        )
    }

    data class Community(
        val alias: String?,
        val communityId: String,
        val name: String?,
        val avatarUrl: String?
    )

    data class Stats(
        val commentsCount: Int
    )

    data class ContentId(
        val communityId: String,
        val permlink: String,
        val userId: String
    )

    data class Author(
        val avatarUrl: String?,
        val userId: String,
        val username: String?
    )
}
