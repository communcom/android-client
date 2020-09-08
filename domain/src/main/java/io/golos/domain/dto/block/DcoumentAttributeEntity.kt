package io.golos.domain.dto.block

import com.squareup.moshi.Json

data class DocumentAttributeEntity(
    @Json(name = "version") val version: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "titles") val titles:String? = null
)