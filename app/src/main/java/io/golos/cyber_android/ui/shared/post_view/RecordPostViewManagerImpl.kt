package io.golos.cyber_android.ui.shared.post_view

import android.content.Context
import io.golos.cyber_android.services.post_view.RecordPostViewService
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.dto.ContentIdDomain
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@UIScope
class RecordPostViewManagerImpl
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider
) : RecordPostViewManager,
    CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext = scopeJob + dispatchersProvider.uiDispatcher

    private val postRegistry = mutableMapOf<ContentIdDomain, Job>()

    override fun onPostShow(postId: ContentIdDomain) {
        postRegistry[postId]?.takeIf { it.isActive }?.cancel()

        postRegistry[postId] = launch {
            delay(6000L)
            if(isActive) {
                RecordPostViewService.record(appContext, postId)
            }
        }
    }

    override fun onPostHide(postId: ContentIdDomain) {
        postRegistry.remove(postId)?.takeIf { it.isActive }?.cancel()
    }
}