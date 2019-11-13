package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.comments_storage.CommentsStorage
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class CommentsLoaderBase
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val commentsStorage: CommentsStorage,
    private val currentUserRepository: CurrentUserRepository
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
    protected fun wasCommentPosted(id: DiscussionIdModel) = commentsStorage.isCommentPosted(id)

    protected fun storeComment(comment: CommentModel) = commentsStorage.addComment(comment)
}