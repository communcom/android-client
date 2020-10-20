package io.golos.cyber_android.ui.mappers

import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.PostDomain


fun List<PostDomain>.mapToPostsList(): List<Post> {
    return this
        .map { it.mapToPost() }
}