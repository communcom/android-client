package io.golos.cyber_android

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.locator.RepositoriesHolder
import io.golos.data.repositories.AbstractDiscussionsRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
class AppCore(private val locator: RepositoriesHolder, dispatchersProvider: DispatchersProvider) {
    private val isInited = AtomicBoolean(false)
    private val scope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private var lastCreatedComment: CommentCreationResultEntity? = null

    private val observer = Observer<Any> {}
    private val commentsMediator = MediatorLiveData<Any>()

    fun initialize() {
        if (isInited.get()) return

        commentsMediator.observeForever(observer)

        synchronized(this) {

            isInited.set(true)
//            locator
//                .authRepository
//                .makeAction(
//                    locator
//                        .authRepository
//                        .allDataRequest
//                )//todo stub for testing


            observeActiveUserMetadataUpdates()
            observeVotesUpdates()
            observeDiscussionCreationUpdates()
        }
    }

    private fun observeDiscussionCreationUpdates() {
        locator.discussionCreationRepository.getAsLiveData(locator.discussionCreationRepository.allDataRequest)
            .observeForever {
                when (it) {
                    is CommentCreationResultEntity -> {
                        val commentCreationResult = (it as? CommentCreationResultEntity) ?: return@observeForever

                        if (lastCreatedComment != commentCreationResult) {
                            lastCreatedComment = commentCreationResult
                            commentsMediator.addSource(
                                locator.commentsRepository.getDiscussionAsLiveData(
                                    commentCreationResult.commentId
                                )
                            ) { commentEntity ->
                                commentEntity ?: return@addSource
                                commentsMediator.removeSource(
                                    locator.commentsRepository.getDiscussionAsLiveData(
                                        commentCreationResult.commentId
                                    )
                                )
                                onRelatedToCommentDataChanged()
                            }
                        }
                    }

                    is DeleteDiscussionResultEntity -> {
                        locator.postFeedRepository.requestDiscussionUpdate(it.id)
                    }

                    is UpdatePostResultEntity -> {
                        locator.postFeedRepository.requestDiscussionUpdate(it.id)
                    }
                }
            }
    }

    private fun observeVotesUpdates() {
        locator.voteRepository.getAsLiveData(locator.voteRepository.allDataRequest).observeForever {
            val vote = it ?: return@observeForever
            scope.launch {
                when (vote) {
                    is VoteRequestEntity.VoteForAPostRequestEntity -> locator.postFeedRepository.requestDiscussionUpdate(
                        vote.discussionIdEntity
                    )
                    is VoteRequestEntity.VoteForACommentRequestEntity -> locator.commentsRepository.requestDiscussionUpdate(
                        vote.discussionIdEntity
                    )
                }
            }
        }
    }

    private fun observeActiveUserMetadataUpdates() {
        var savedMetadata: UserMetadataEntity? = null

        locator.userMetadataRepository.getAsLiveData(locator.userMetadataRepository.allDataRequest)
            .observeForever { metadataCollection ->
                val authState = locator.authRepository.getAsLiveData(locator.authRepository.allDataRequest).value
                authState ?: return@observeForever
                val activeUserMetadata = metadataCollection[authState.user]
                activeUserMetadata ?: return@observeForever
                if (savedMetadata != activeUserMetadata) {
                    savedMetadata = activeUserMetadata
                    locator.postFeedRepository.onAuthorMetadataUpdated(activeUserMetadata)
                }
            }
    }


    private fun onRelatedToCommentDataChanged() {
        val lastCommentCreatedId = lastCreatedComment ?: return
        val comment =
            locator.commentsRepository.getDiscussionAsLiveData(lastCommentCreatedId.commentId).value ?: return

        if (comment.contentId != lastCommentCreatedId.commentId) return

        locator.postFeedRepository.requestDiscussionUpdate(comment.parentPostId)

        locator.commentsRepository.fixOnPositionDiscussion(comment, lastCommentCreatedId.parentId)

    }
}