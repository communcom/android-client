package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ContentEntity(
    @Json(name = "content") val content: String,
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: ContentAttribute? = null
)