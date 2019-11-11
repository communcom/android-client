package io.golos.data.dto

import com.squareup.moshi.Json

data class DocumentEntity(
    @Json(name = "attributes")
    val attributes: AttributesEntity,
    @Json(name = "content")
    val content: List<ContentEntity>,
    @Json(name = "id")
    val id: String,
    @Json(name = "type")
    val type: String
) {
    data class ContentEntity(
        @Json(name = "content")
        val contentBodyList: List<ContentBodyEntity>,
        @Json(name = "id")
        val id: String,
        @Json(name = "type")
        val type: String
    ) {
        data class ContentBodyEntity(
            @Json(name = "content")
            val content: String,
            @Json(name = "id")
            val id: String,
            @Json(name = "type")
            val type: String
        )
    }

    data class AttributesEntity(
        @Json(name = "title")
        val title: String?,
        @Json(name = "type")
        val type: String,
        @Json(name = "version")
        val version: String
    )
}