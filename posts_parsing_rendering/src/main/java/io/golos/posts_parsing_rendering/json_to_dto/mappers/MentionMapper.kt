package io.golos.posts_parsing_rendering.json_to_dto.mappers

import io.golos.domain.post.post_dto.MentionBlock
import org.json.JSONObject

class MentionMapper(mappersFactory: MappersFactory): MapperBase<MentionBlock>(mappersFactory) {
    override fun map(source: JSONObject): MentionBlock = MentionBlock(source.getContentAsString())
}