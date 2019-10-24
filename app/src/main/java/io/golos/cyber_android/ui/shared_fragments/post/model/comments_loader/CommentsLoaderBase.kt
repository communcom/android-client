package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

abstract class CommentsLoaderBase
constructor(
    private val dispatchersProvider: DispatchersProvider
) {
    protected var pageOffset = 0
    protected var endOfDataReached = false

    protected var loadingInProgress = false
    protected var isInErrorState = false

    protected suspend fun loading(loadingAction: suspend () -> Unit) {
        withContext(dispatchersProvider.ioDispatcher) {
            if(loadingInProgress || isInErrorState) {
                return@withContext
            }
            loadingInProgress = true

            try {
                loadingAction()
            } catch (ex: Exception) {
                Timber.e(ex)
            } finally {
                loadingInProgress = false
            }
        }
    }

    protected suspend fun loadNext() {
        loading {
            if(endOfDataReached) {
                return@loading
            }

            loadPage()
        }
    }

    protected suspend fun retry() {
        isInErrorState = false
        loading {
            loadPage()
        }
    }

    protected abstract suspend fun loadPage()
}