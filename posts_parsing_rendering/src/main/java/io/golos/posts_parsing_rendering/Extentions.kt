package io.golos.posts_parsing_rendering

import io.golos.domain.interactors.model.PostModel
import io.golos.domain.post.post_dto.PostMetadata
import io.golos.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper

val PostModel.metadata: PostMetadata
    get() = JsonToDtoMapper(null).getPostMetadata((this.content.body.rawData))
