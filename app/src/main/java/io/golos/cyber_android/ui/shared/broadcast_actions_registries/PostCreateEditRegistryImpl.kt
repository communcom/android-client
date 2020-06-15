package io.golos.cyber_android.ui.shared.broadcast_actions_registries

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.ContentIdDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@ApplicationScope
class PostCreateEditRegistryImpl
@Inject
constructor(): PostCreateEditRegistry {
    private val postCreateChannel: ConflatedBroadcastChannel<ContentIdDomain?> = ConflatedBroadcastChannel(null)

    override val createdPosts: Flow<ContentIdDomain?>
        get() = postCreateChannel.asFlow()

    override suspend fun setPostCreated(id: ContentIdDomain) = postCreateChannel.send(id)
}