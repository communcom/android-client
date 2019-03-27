package io.golos.cyber_android.locator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.golos.cyber4j.Cyber4J
import io.golos.cyber_android.CommunityFeedViewModel
import io.golos.cyber_android.ui.screens.feed.UserSubscriptionsFeedFeedViewModel
import io.golos.data.api.Cyber4jApiService
import io.golos.data.repositories.*
import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.*
import io.golos.domain.interactors.model.CommunityId
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.model.AuthRequest
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class ServiceLocatorImpl(private val appContext: Context) : ServiceLocator, RepositoriesHolder {

    private val cyber4j by lazy { Cyber4J() }

    private val apiService: Cyber4jApiService by lazy { Cyber4jApiService(cyber4j) }

    private val cyberPostToEntityMapper = CyberPostToEntityMapper()
    private val voteToEntityMapper = VoteRequestModelToEntityMapper()
    private val cyberFeedToEntityMapper = CyberFeedToEntityMapper(cyberPostToEntityMapper)

    private val postEntityToModelMapper = PostEntityEntitiesToModelMapper()
    private val feedEntityToModelMapper = PostFeedEntityToModelMapper(postEntityToModelMapper)
    private val voteEntityToPostMapper = VoteRequestEntityToModelMapper()

    private val commentEntityToModelMapper = CommentEntityToModelMapper()
    private val commentFeeEntityToModelMapper = CommentsFeedEntityToModelMapper(commentEntityToModelMapper)


    private val approver = FeedUpdateApprover()

    private val postMerger = PostMerger()
    private val feedMerger = PostFeedMerger()

    private val emptyPostFeedProducer = EmptyPostFeedProducer()

    private val cyberCommentToEntityMapper = CyberCommentToEntityMapper()
    private val cyberCommentFeedToEntityMapper = CyberCommentsToEntityMapper(cyberCommentToEntityMapper)

    private val commentApprover = CommentUpdateApprover()

    private val commentMerger = CommentMerger()
    private val commentFeedMerger = CommentFeedMerger()

    private val emptyCommentFeedProducer = EmptyCommentFeedProducer()

    private val logger = object : Logger {
        override fun invoke(e: Exception) {
            e.printStackTrace()
        }
    }

    override val dispatchersProvider = object : DispatchersProvider {
        override val uiDispatcher: CoroutineDispatcher
            get() = Dispatchers.Main
        override val workDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val networkDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
    }


    override val postFeedRepository: AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>by lazy {
        PostsFeedRepository(
            apiService,
            cyberFeedToEntityMapper,
            cyberPostToEntityMapper,
            postMerger,
            feedMerger,
            approver,
            emptyPostFeedProducer,
            dispatchersProvider,
            logger
        )
    }
    override val commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest> by lazy {
        CommentsFeedRepository(
            apiService,
            cyberCommentFeedToEntityMapper,
            cyberCommentToEntityMapper,
            commentMerger,
            commentFeedMerger,
            commentApprover,
            emptyCommentFeedProducer,
            dispatchersProvider,
            logger
        )
    }

    override val authRepository: Repository<AuthState, AuthRequest>
            by lazy { AuthStateRepository(apiService, dispatchersProvider, logger) }

    override val voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>
            by lazy {
                VoteRepository(apiService, dispatchersProvider, logger)
            }


    override fun getCommunityFeedViewModelFactory(communityId: CommunityId): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    CommunityFeedViewModel::class.java -> CommunityFeedViewModel(
                        getCommunityFeedUseCase(communityId),
                        getVoteUseCase()
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    override fun getUserSubscriptionsFeedViewModelFactory(user: CyberUser): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    UserSubscriptionsFeedFeedViewModel::class.java -> UserSubscriptionsFeedFeedViewModel(
                        getUserPostFeedUseCase(user),
                        getVoteUseCase()
                    ) as T
                    else -> throw IllegalStateException("$modelClass is unsupported")
                }
            }
        }
    }

    override fun getCommunityFeedUseCase(communityId: CommunityId): CommunityFeedUseCase {
        return CommunityFeedUseCase(
            communityId,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }

    override fun getUserSubscriptionsFeedUseCase(user: CyberUser): UserSubscriptionsFeedUseCase {
        return UserSubscriptionsFeedUseCase(
            user,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }

    override fun getUserPostFeedUseCase(user: CyberUser): UserPostFeedUseCase {
        return UserPostFeedUseCase(
            user,
            postFeedRepository,
            voteRepository,
            feedEntityToModelMapper,
            dispatchersProvider
        )
    }

    override fun getVoteUseCase() =
        VoteUseCase(
            authRepository, voteRepository,
            dispatchersProvider, voteEntityToPostMapper,
            voteToEntityMapper
        )

    override fun getCommentsForAPostUseCase(postId: DiscussionIdModel): PostCommentsFeedUseCase {
        return PostCommentsFeedUseCase(
            postId, commentsRepository, voteRepository, commentFeeEntityToModelMapper,
            dispatchersProvider
        )
    }

    override fun getPostWithCommentsUseCase(postId: DiscussionIdModel): PostWithCommentUseCase {
        return PostWithCommentUseCase(
            postId,
            postFeedRepository,
            postEntityToModelMapper,
            commentsRepository,
            voteRepository,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
    }

    override val getAppContext: Context
        get() = appContext

}