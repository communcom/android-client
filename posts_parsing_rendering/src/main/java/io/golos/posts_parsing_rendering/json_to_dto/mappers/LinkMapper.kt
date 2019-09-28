package io.golos.posts_parsing_rendering.json_to_dto.mappers

import io.golos.domain.post.post_dto.LinkBlock
import io.golos.posts_parsing_rendering.Attribute
import org.json.JSONObject

class LinkMapper(mappersFactory: MappersFactory): MapperBase<LinkBlock>(mappersFactory) {
    override fun map(source: JSONObject): LinkBlock {
        val attributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty")

        return LinkBlock(
            source.getContentAsString(),
            attributes.getUri(Attribute.URL),
            attributes.getLinkType(Attribute.TYPE),
            attributes.tryUri(Attribute.THUMBNAIL_URL)
        )
    }
}