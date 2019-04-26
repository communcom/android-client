package io.golos.cyber_android

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.locator.RepositoriesHolder
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CommentCreationResultEntity
import io.golos.domain.entities.VoteRequestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
class AppCore(private val locator: RepositoriesHolder, private val dispatchersProvider: DispatchersProvider) {
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

            locator.discussionCreationRepository.getAsLiveData(locator.discussionCreationRepository.allDataRequest)
                .observeForever {
                    val commentCreationResult = (it as? CommentCreationResultEntity) ?: return@observeForever

                    if (lastCreatedComment != commentCreationResult) {
                        lastCreatedComment = commentCreationResult
                        commentsMediator.addSource(
                            locator.commentsRepository.getDiscussionAsLiveData(
                                commentCreationResult.commentId
                            )
                        ) { commentEntity ->
                            commentEntity ?: return@addSource
                            onRelatedToCommentDataChanged()
                        }
                    }
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