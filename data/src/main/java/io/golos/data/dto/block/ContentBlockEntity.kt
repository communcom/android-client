package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ContentBlockEntity(
    @Json(name = "type") val type: String,
    @Json(name = "content") val content: List<ContentEntity>
)

