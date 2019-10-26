package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.second_level

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.our_comments_collection.OurCommentsCollection
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SecondLevelLoaderImpl
constructor(
    private val parentComment: DiscussionIdModel,
    private val totalComments: Int,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val pageSize: Int,
    ourCommentsCollection: OurCommentsCollection,
    currentUserRepository: CurrentUserRepository
) : CommentsLoaderBase(
    dispatchersProvider,
    ourCommentsCollection,
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

            val comments = discussionsApi.getCommentsListForComment(pageOffset, pageSize + 1, parentComment)

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
                                storeCommentIfNeeded(it)
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