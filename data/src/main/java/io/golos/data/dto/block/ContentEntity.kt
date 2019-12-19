package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ContentEntity(
    @Json(name = "id") val id: String?,
    @Json(name = "content") val content: String,
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: ContentAttributeEntity? = null
)