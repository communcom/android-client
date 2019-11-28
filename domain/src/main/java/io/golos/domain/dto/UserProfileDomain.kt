package io.golos.domain.dto

import io.golos.commun4j.sharedmodel.CyberName
import java.util.*

data class UserProfileDomain(
    val userId: CyberName,
    val coverUrl: String?,
    val avatarUrl: String?,
    val bio: String?,
    val name: String,
    val joinDate: Date,
    val followersCount: Int,
    val followingsCount: Int,
    val communitiesSubscribedCount: Int,
    val highlightCommunities: List<CommunityDomain>
)