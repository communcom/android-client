package io.golos.cyber_android.ui.shared.broadcast_actions_registries

import io.golos.domain.dto.ContentIdDomain
import kotlinx.coroutines.flow.Flow

interface PostCreateEditRegistry {
    val createdPosts: Flow<ContentIdDomain?>

    suspend fun setPostCreated(id: ContentIdDomain)
}