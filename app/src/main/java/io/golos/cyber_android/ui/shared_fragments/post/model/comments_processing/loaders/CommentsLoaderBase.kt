package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.our_comments_collection.OurCommentsCollection
import io.golos.data.repositories.current_user_repository.CurrentUserRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class CommentsLoaderBase
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val ourCommentsCollection: OurCommentsCollection,
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
    protected fun wasCommentPosted(id: DiscussionIdModel) = ourCommentsCollection.isCommentPosted(id)

    protected fun storeCommentIfNeeded(comment: CommentModel) {
        if(comment.author.userId.userId == currentUserRepository.userId) {
            ourCommentsCollection.addComment(comment)
        }
    }
}