package io.golos.cyber_android.ui.shared.broadcast_actions_registries

import io.golos.cyber_android.ui.dto.PostDonation
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@ApplicationScope
class PostUpdateRegistryImpl
@Inject
constructor(): PostUpdateRegistry {
    private val postCreateChannel: ConflatedBroadcastChannel<ContentIdDomain?> = ConflatedBroadcastChannel(null)

    private val postUpdatedChannel: ConflatedBroadcastChannel<PostDomain?> = ConflatedBroadcastChannel(null)

    private val postDonationChannel: ConflatedBroadcastChannel<PostDonation?> = ConflatedBroadcastChannel(null)

    override val createdPosts: Flow<ContentIdDomain?>
        get() = postCreateChannel.asFlow()

    override val updatedPosts: Flow<PostDomain?>
        get() = postUpdatedChannel.asFlow()

    override val donationSend: Flow<PostDonation?>
        get() = postDonationChannel.asFlow()

    override suspend fun setPostCreated(id: ContentIdDomain) = postCreateChannel.send(id)

    override suspend fun setPostUpdated(post: PostDomain) = postUpdatedChannel.send(post)

    override suspend fun setDonationSend(postDonation: PostDonation) = postDonationChannel.send(postDonation)
}