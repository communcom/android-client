package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.MentionBlock
import org.json.JSONObject

class MentionMapper(mappersFactory: MappersFactory) : MapperBase<MentionBlock>(mappersFactory) {
    override fun map(source: JSONObject): MentionBlock =
        MentionBlock(
            source.tryLong(CommonType.ID),
            source.getContentAsString()
        )
}