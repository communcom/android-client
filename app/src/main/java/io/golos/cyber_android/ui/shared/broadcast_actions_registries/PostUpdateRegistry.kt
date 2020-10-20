package io.golos.cyber_android.ui.shared.broadcast_actions_registries

import io.golos.cyber_android.ui.dto.PostDonation
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import kotlinx.coroutines.flow.Flow

interface PostUpdateRegistry {
    val createdPosts: Flow<ContentIdDomain?>

    val updatedPosts: Flow<PostDomain?>

    val donationSend: Flow<PostDonation?>

    suspend fun setPostCreated(id: ContentIdDomain)

    suspend fun setPostUpdated(post: PostDomain)

    suspend fun setDonationSend(postDonation: PostDonation)
}