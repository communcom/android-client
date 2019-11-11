package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.use_cases.post.post_dto.PostFormatVersion
import io.golos.domain.use_cases.post.post_dto.PostMetadata
import io.golos.domain.use_cases.post.post_dto.PostType
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.PostTypeJson
import org.json.JSONObject

class PostMetadataMapper : MapperJsonUtils() {
    fun map(source: JSONObject): PostMetadata {
        val jsonAttributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty")

        val version = PostFormatVersion.parse(jsonAttributes.getString(Attribute.VERSION))

        val postType = jsonAttributes.getString(Attribute.TYPE).toPostType()

        return PostMetadata(version, postType)
    }

    private fun String.toPostType(): PostType =
        when(this) {
            PostTypeJson.ARTICLE -> PostType.ARTICLE
            PostTypeJson.BASIC -> PostType.BASIC
            PostTypeJson.COMMENT -> PostType.COMMENT
            else -> throw java.lang.UnsupportedOperationException("This type of post is not supported: $this")
        }
}