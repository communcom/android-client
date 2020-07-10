package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class CommentsLoaderBase(
    private val dispatchersProvider: DispatchersProvider,
    private val commentsStorage: CommentsStorage
) {
    protected var pageOffset = 0
    protected var endOfDataReached = false

    protected var loadingInProgress = false
    protected var isInErrorState = false

    protected suspend fun loading(loadingAction: suspend () -> Unit) {
        withContext(dispatchersProvider.ioDispatcher) {
            if (loadingInProgress || isInErrorState) {
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
            if (endOfDataReached) {
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

    /**
     * Was the comment with such id have been posted in this post viewing/editing session
     */
    protected fun wasCommentPosted(id: ContentIdDomain) = commentsStorage.isCommentPosted(id)

    protected fun storeComment(comment: CommentDomain) = commentsStorage.addComment(comment)
}