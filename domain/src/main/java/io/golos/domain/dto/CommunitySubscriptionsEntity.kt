package io.golos.domain.dto

import com.squareup.moshi.Json

data class CommunitySubscriptionsEntity(
    @Json(name = "communities")
    val communities: List<CommunityEntity>
)