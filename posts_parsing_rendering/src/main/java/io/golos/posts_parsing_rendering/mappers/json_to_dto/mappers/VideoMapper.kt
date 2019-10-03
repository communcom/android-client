package io.golos.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.post.post_dto.VideoBlock
import io.golos.posts_parsing_rendering.Attribute
import org.json.JSONObject

class VideoMapper(mappersFactory: MappersFactory): MapperBase<VideoBlock>(mappersFactory) {
    override fun map(source: JSONObject): VideoBlock {
        val attributes = source.getAttributes()

        return VideoBlock(
            source.getContentAsUri(),
            attributes?.tryString(Attribute.TITLE),
            attributes?.tryString(Attribute.PROVIDER_NAME),
            attributes?.tryString(Attribute.AUTHOR),
            attributes?.tryUri(Attribute.AUTHOR_URL),
            attributes?.tryString(Attribute.DESCRIPTION),
            attributes?.tryUri(Attribute.THUMBNAIL_URL),
            attributes?.trySize(Attribute.THUMBNAIL_SIZE),
            attributes?.tryString(Attribute.HTML)
        )
    }
}