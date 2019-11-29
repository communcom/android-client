package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.use_cases.post.post_dto.EmbedBlock
import org.json.JSONObject

class EmbedMapper(
    mappersFactory: MappersFactory
) : MapperBase<EmbedBlock>(mappersFactory) {

    override fun map(source: JSONObject): EmbedBlock {
        val attributes = source.getAttributes()

        return EmbedBlock(
            source.getContentAsUri(),
            attributes?.tryString(Attribute.TITLE),
            attributes?.tryUri(Attribute.URL),
            attributes?.tryString(Attribute.AUTHOR),
            attributes?.tryUri(Attribute.AUTHOR_URL),
            attributes?.tryString(Attribute.PROVIDER_NAME),
            attributes?.tryString(Attribute.DESCRIPTION),
            attributes?.tryUri(Attribute.THUMBNAIL_URL),
            attributes?.tryInt(Attribute.THUMBNAIL_WIDTH),
            attributes?.tryInt(Attribute.THUMBNAIL_HEIGHT),
            attributes?.tryString(Attribute.HTML)
        )
    }
}