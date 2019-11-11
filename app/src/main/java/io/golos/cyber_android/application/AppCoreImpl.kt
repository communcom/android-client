package io.golos.cyber_android.application

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.*
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
@ApplicationScope
class AppCoreImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val discussionCreationRepository: Repository<DiscussionCreationResultEntity, DiscussionCreationRequestEntity>,
    private val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
    private val postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    private val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    private val userMetadataRepository: Repository<UserMetadataCollectionEntity, UserMetadataRequest>,
    private val authRepository: AuthStateRepository
) : AppCore {
    private val isInited = AtomicBoolean(false)
    private val scope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())
    private var lastCreatedComment: CommentCreationResultEntity? = null

    private val observer = Observer<Any> {}
    private val commentsMediator = MediatorLiveData<Any>()

    override fun initialize() {
        if (isInited.get()) return

        commentsMediator.observeForever(observer)

        synchronized(this) {

            isInited.set(true)

            observeActiveUserMetadataUpdates()
            observeVotesUpdates()
            observeDiscussionCreationUpdates()
        }
    }

    private fun observeDiscussionCreationUpdates() {
        discussionCreationRepository.getAsLiveData(discussionCreationRepository.allDataRequest)
            .observeForever {
                when (it) {
                    is CommentCreationResultEntity -> {
                        val commentCreationResult = (it as? CommentCreationResultEntity) ?: return@observeForever

                        if (lastCreatedComment != commentCreationResult) {
                            lastCreatedComment = commentCreationResult
                            commentsMediator.addSource(
                                commentsRepository.getDiscussionAsLiveData(
                                    commentCreationResult.commentId
                                )
                            ) { commentEntity ->
                                commentEntity ?: return@addSource
                                commentsMediator.removeSource(
                                    commentsRepository.getDiscussionAsLiveData(
                                        commentCreationResult.commentId
                                    )
                                )
                                onRelatedToCommentDataChanged()
                            }
                        }
                    }

                    is DeleteDiscussionResultEntity -> {
                        postFeedRepository.requestDiscussionUpdate(it.id)
                    }

                    is UpdatePostResultEntity -> {
                        postFeedRepository.requestDiscussionUpdate(it.id)
                    }
                }
            }
    }

    private fun observeVotesUpdates() {
        voteRepository.getAsLiveData(voteRepository.allDataRequest).observeForever {
            val vote = it ?: return@observeForever
            scope.launch {
                when (vote) {
                    is VoteRequestEntity.VoteForAPostRequestEntity -> postFeedRepository.requestDiscussionUpdate(
                        vote.discussionIdEntity
                    )
                    is VoteRequestEntity.VoteForACommentRequestEntity -> commentsRepository.requestDiscussionUpdate(
                        vote.discussionIdEntity
                    )
                }
            }
        }
    }

    private fun observeActiveUserMetadataUpdates() {
        var savedMetadata: UserMetadataEntity? = null

        userMetadataRepository.getAsLiveData(userMetadataRepository.allDataRequest)
            .observeForever { metadataCollection ->
                val authState = authRepository.getAsLiveData(authRepository.allDataRequest).value
                authState ?: return@observeForever
                val activeUserMetadata = metadataCollection[authState.user]
                activeUserMetadata ?: return@observeForever
                if (savedMetadata != activeUserMetadata) {
                    savedMetadata = activeUserMetadata
                    postFeedRepository.onAuthorMetadataUpdated(activeUserMetadata)
                }
            }
    }


    private fun onRelatedToCommentDataChanged() {
        val lastCommentCreatedId = lastCreatedComment ?: return
        val comment = commentsRepository.getDiscussionAsLiveData(lastCommentCreatedId.commentId).value ?: return

        if (comment.contentId != lastCommentCreatedId.commentId) return

        // Temporary commented
        //postFeedRepository.requestDiscussionUpdate(comment.parentPostId)

        commentsRepository.fixOnPositionDiscussion(comment, lastCommentCreatedId.parentId)

    }
}