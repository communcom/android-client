package io.golos.cyber_android.ui.shared.broadcast_actions_registries

import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import kotlinx.coroutines.flow.Flow

interface PostCreateEditRegistry {
    val createdPosts: Flow<ContentIdDomain?>

    val updatedPosts: Flow<PostDomain?>

    suspend fun setPostCreated(id: ContentIdDomain)

    suspend fun setPostUpdated(post: PostDomain)
}