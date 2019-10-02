package io.golos.posts_parsing_rendering.json_to_dto.mappers

import io.golos.domain.post.post_dto.Block
import org.json.JSONObject

abstract class MapperBase<T: Block>(protected val mappersFactory: MappersFactory): MapperJsonUtils() {
    abstract fun map(source: JSONObject): T
}