package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.use_cases.post.post_dto.RichBlock
import org.json.JSONObject

class RichMapper(
    mappersFactory: MappersFactory
) : MapperBase<RichBlock>(mappersFactory) {

    override fun map(source: JSONObject): RichBlock {
        val attributes = source.getAttributes()

        return RichBlock(
            source.tryString(CommonType.ID),
            source.getContentAsUri(),
            attributes?.tryString(Attribute.TITLE),
            attributes?.tryUri(Attribute.URL),
            attributes?.tryString(Attribute.AUTHOR),
            attributes?.tryUri(Attribute.AUTHOR_URL),
            attributes?.tryString(Attribute.PROVIDER_NAME),
            attributes?.tryString(Attribute.DESCRIPTION),
            attributes?.tryUri(Attribute.THUMBNAIL_URL),
            attributes?.tryInt(Attribute.HEIGHT) ?: attributes?.tryInt(Attribute.THUMBNAIL_WIDTH),
            attributes?.tryInt(Attribute.HEIGHT) ?:attributes?.tryInt(Attribute.THUMBNAIL_HEIGHT),
            attributes?.tryString(Attribute.HTML)
        )
    }
}