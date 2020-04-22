package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.TextBlock
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import org.json.JSONObject

class TextMapper(mappersFactory: MappersFactory): MapperBase<TextBlock>(mappersFactory) {
    override fun map(source: JSONObject): TextBlock {
        val attributes = source.getAttributes()

        return TextBlock(
            source.tryLong(CommonType.ID),
            source.getContentAsString(),
            attributes?.tryTextStyle(Attribute.STYLE),
            attributes?.tryColor(Attribute.TEXT_COLOR)
        )
    }
}
