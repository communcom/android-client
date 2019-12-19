package io.golos.data.dto.block

import com.squareup.moshi.Json

data class ContentAttributeEntity(
    @Json(name = "title") val title: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "author") val author: String? = null,
    @Json(name = "author_url") val authorUrl: String? = null,
    @Json(name = "provider_name") val providerName: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "thumbnail_width") val thumbnailWidth: Int? = null,
    @Json(name = "thumbnail_height") val thumbnailHeight: Int? = null,
    @Json(name = "html") val html: String? = null,
    @Json(name = "style") val style: List<String>? = null,
    @Json(name = "text_color") val textColor: String? = null
)