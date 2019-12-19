package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.use_cases.post.post_dto.LinkBlock
import org.json.JSONObject

class LinkMapper(mappersFactory: MappersFactory) : MapperBase<LinkBlock>(mappersFactory) {
    override fun map(source: JSONObject): LinkBlock {
        val attributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty")

        return LinkBlock(
            source.tryString(CommonType.ID),
            source.getContentAsString(),
            attributes.getUri(Attribute.URL)
        )
    }
}