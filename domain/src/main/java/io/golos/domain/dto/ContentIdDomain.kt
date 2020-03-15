package io.golos.domain.dto

data class ContentIdDomain(
    val communityId: CommunityIdDomain,
    val permlink: String,
    val userId: String
)