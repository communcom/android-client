package io.golos.domain.dto.block

import com.squareup.moshi.Json

data class DocumentAttributeEntity(
    @Json(name = "version") val id: String? = null,
    @Json(name = "type") val type: String? = null
)