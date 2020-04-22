package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.TagBlock
import org.json.JSONObject

class TagMapper(mappersFactory: MappersFactory): MapperBase<TagBlock>(mappersFactory) {
    override fun map(source: JSONObject): TagBlock =
        TagBlock(
            source.tryLong(CommonType.ID),
            source.getContentAsString()
        )
}