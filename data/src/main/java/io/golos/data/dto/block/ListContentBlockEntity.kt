package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ListContentBlockEntity(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: DocumentAttributeEntity,
    @Json(name = "content") val content: List<ContentBlockEntity>)