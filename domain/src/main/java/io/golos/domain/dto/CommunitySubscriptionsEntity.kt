package io.golos.domain.dto

import com.squareup.moshi.Json
import io.golos.domain.dto.CommunityEntity

data class CommunitySubscriptionsEntity(
    @Json(name = "communities")
    val communities: List<CommunityEntity>
)