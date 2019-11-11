package io.golos.domain.dto

import java.util.*

data class PostDomain(

    val author: AuthorDomain,
    val community: CommunityDomain,
    val contentId: ContentIdDomain,
    val document: DocumentDomain?,
    val meta: MetaDomain,
    val stats: StatsDomain?,
    val type: String?,
    val votes: VotesDomain
) {
    data class VotesDomain(
        val downCount: Long,
        val upCount: Long
    )

    data class MetaDomain(
        val creationTime: Date
    )

    data class DocumentDomain(
        val attributes: AttributesDomain,
        val content: List<ContentDomain>,
        val id: String,
        val type: String
    ) {
        data class ContentDomain(
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

        data class AttributesDomain(
            val type: String,
            val version: String
        )
    }

    data class CommunityDomain(
        val alias: String?,
        val communityId: String,
        val name: String?
    )

    data class StatsDomain(
        val commentsCount: Int
    )

    data class ContentIdDomain(
        val communityId: String,
        val permlink: String,
        val userId: String
    )

    data class AuthorDomain(
        val avatarUrl: String?,
        val userId: String,
        val username: String?
    )
}
