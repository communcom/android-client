package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import org.json.JSONObject

class ImageMapper(mappersFactory: MappersFactory): MapperBase<ImageBlock>(mappersFactory) {
    override fun map(source: JSONObject): ImageBlock =
        ImageBlock(
            source.tryString(CommonType.ID),
            source.getContentAsUri(),
            source.getAttributes()?.tryString(Attribute.DESCRIPTION),
            source.getAttributes()?.tryInt(Attribute.WIDTH),
            source.getAttributes()?.tryInt(Attribute.HEIGHT)
        )
}