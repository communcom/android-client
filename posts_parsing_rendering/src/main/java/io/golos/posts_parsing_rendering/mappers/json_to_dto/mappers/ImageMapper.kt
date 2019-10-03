package io.golos.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.post.post_dto.ImageBlock
import io.golos.posts_parsing_rendering.Attribute
import org.json.JSONObject

class ImageMapper(mappersFactory: MappersFactory): MapperBase<ImageBlock>(mappersFactory) {
    override fun map(source: JSONObject): ImageBlock =
        ImageBlock(
            source.getContentAsUri(),
            source.getAttributes()?.tryString(Attribute.DESCRIPTION)
        )
}