package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.use_cases.post.post_dto.WebsiteBlock
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import org.json.JSONObject

class WebsiteMapper(mappersFactory: MappersFactory): MapperBase<WebsiteBlock>(mappersFactory) {
    override fun map(source: JSONObject): WebsiteBlock {
        val attributes = source.getAttributes()

        return WebsiteBlock(
            source.tryLong(CommonType.ID),
            source.getContentAsUri(),
            attributes?.tryUri(Attribute.THUMBNAIL_URL),
            attributes?.tryString(Attribute.TITLE),
            attributes?.tryString(Attribute.PROVIDER_NAME),
            attributes?.tryString(Attribute.DESCRIPTION)
        )
    }
}