package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.posted_comments_collection.PostedCommentsCollectionRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class CommentsLoaderBase
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val postedCommentsCollection: PostedCommentsCollectionRead
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

    /**
     * Was the comment with such id have been posted in this post viewing/editing session
     */
    protected fun wasCommentPosted(id: DiscussionIdModel) = postedCommentsCollection.isEntityExists(id)
}