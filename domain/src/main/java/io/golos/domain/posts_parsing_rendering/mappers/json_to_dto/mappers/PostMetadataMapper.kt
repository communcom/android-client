package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.DocumentFormatVersion
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.PostMetadata
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.PostType
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.DocumentType
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class PostMetadataMapper : MapperJsonUtils() {
    fun map(source: JSONObject): PostMetadata {
        val jsonAttributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty")

        val versionString: String? = try {
            jsonAttributes.getString(Attribute.VERSION)
        } catch (e: Exception){
            null
        }
        val version = versionString?.let { DocumentFormatVersion.parse(it) } ?: DocumentFormatVersion(
            1,
            0
        )

        val postType = jsonAttributes.getString(Attribute.TYPE).toPostType()

        return PostMetadata(version, postType)
    }

    private fun String.toPostType(): PostType =
        when(this.toLowerCase(Locale.getDefault())) {
            DocumentType.ARTICLE.value -> PostType.ARTICLE
            DocumentType.BASIC.value -> PostType.BASIC
            DocumentType.COMMENT.value -> PostType.COMMENT
            else -> throw java.lang.UnsupportedOperationException("This type of post is not supported: $this")
        }
}