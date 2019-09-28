package io.golos.posts_parsing_rendering.json_to_dto.mappers

import io.golos.domain.post.post_dto.WebsiteBlock
import io.golos.posts_parsing_rendering.Attribute
import org.json.JSONObject

class WebsiteMapper(mappersFactory: MappersFactory): MapperBase<WebsiteBlock>(mappersFactory) {
    override fun map(source: JSONObject): WebsiteBlock {
        val attributes = source.getAttributes()

        return WebsiteBlock(
            source.getContentAsUri(),
            attributes?.tryUri(Attribute.THUMBNAIL_URL),
            attributes?.tryString(Attribute.TITLE),
            attributes?.tryString(Attribute.PROVIDER_NAME),
            attributes?.tryString(Attribute.DESCRIPTION)
        )
    }
}