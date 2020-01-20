package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.second_level

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.ParentCommentIdentifierDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SecondLevelLoaderImpl
constructor(
    private val contentIdDomain: ContentIdDomain,
    private val parentComment: DiscussionIdModel,
    private val totalComments: Int,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val pageSize: Int,
    commentsStorage: CommentsStorage,
    currentUserRepository: CurrentUserRepository
) : CommentsLoaderBase(
    dispatchersProvider,
    commentsStorage,
    currentUserRepository),
    SecondLevelLoader {

    // Loaded comments and their author
    private val authors = mutableMapOf<DiscussionIdModel, DiscussionAuthorModel>()

    /**
     * Loads a next comments page
     */
    override suspend fun loadNextPage() = loadNext()

    /**
     * Try to reload
     */
    override suspend fun retryLoadPage() = retry()

    override suspend fun loadPage() {
        try {
            postListDataSource.addLoadingCommentsIndicator(parentComment, pageOffset)

            delay(1000)

            // To error simulation
            if(Random.nextInt () % 2 == 0) {
                throw Exception("")
            }

            val comments = discussionRepository.getComments(
                offset = pageOffset,
                pageSize = pageSize + 1,
                commentType = CommentDomain.CommentTypeDomain.POST,
                userId = UserIdDomain(parentComment.userId),
                permlink = parentComment.permlink.value,
                parentComment = ParentCommentIdentifierDomain(parentComment.permlink.value, UserIdDomain(parentComment.userId)),
                communityId = contentIdDomain.communityId
            )

            if(comments.size < pageSize + 1) {
                endOfDataReached = true
            }

            @Suppress("NestedLambdaShadowedImplicitParameter")
            val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                comments
                    .map {
                        commentToModelMapper.map(it)
                            .also {
                                authors[it.contentId] = it.author
                                storeComment(it)
                            }
                    }
                    .filter { !wasCommentPosted(it.contentId) }
            }

            postListDataSource.addSecondLevelComments(
                parentComment,
                mapperComments.take(pageSize),
                authors,
                pageOffset,
                totalComments,
                endOfDataReached,
                if(endOfDataReached) null else mapperComments.lastOrNull()?.author)

            pageOffset += pageSize
        } catch (ex: Exception) {
            postListDataSource.addRetryLoadingComments(parentComment, pageOffset)
            isInErrorState = true

            throw ex
        }
    }
}