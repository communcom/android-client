package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.post.post_dto.TextBlock
import io.golos.domain.posts_parsing_rendering.Attribute
import org.json.JSONObject

class TextMapper(mappersFactory: MappersFactory): MapperBase<TextBlock>(mappersFactory) {
    override fun map(source: JSONObject): TextBlock {
        val attributes = source.getAttributes()

        return TextBlock(
            source.getContentAsString(),
            attributes?.tryTextStyle(Attribute.STYLE),
            attributes?.tryColor(Attribute.TEXT_COLOR)
        )
    }
}
