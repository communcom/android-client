package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.use_cases.post.post_dto.Block
import org.json.JSONObject

abstract class MapperBase<T: Block>(protected val mappersFactory: MappersFactory): MapperJsonUtils() {
    abstract fun map(source: JSONObject): T
}