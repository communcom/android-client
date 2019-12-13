package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.MetaDomain

fun MetaDomain.mapToMeta(): Post.Meta {
    return Post.Meta(this.creationTime)
}