package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class FirstLevelLoaderImpl
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val pageSize: Int,
    commentsStorage: CommentsStorage,
    currentUserRepository: CurrentUserRepository
) : CommentsLoaderBase(
    dispatchersProvider,
    commentsStorage,
    currentUserRepository
),
    FirstLevelLoader {

    private val loadedComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    /**
     * Loads the very first first-levels comments page
     */
    override suspend fun loadStartPage() =
        loading {
            if (pageOffset > 0) {          // already loaded
                return@loading
            }

            loadPage()
        }

    /**
     * Loads a next first-levels comments page
     */
    override suspend fun loadNextPageByScroll() = loadNext()

    override fun getLoadedComment(commentId: DiscussionIdModel): CommentModel = loadedComments[commentId]!!

    /**
     * Try to reload
     */
    override suspend fun retryLoadPage() = retry()

    override suspend fun loadPage() {
        try {
            postListDataSource.addLoadingCommentsIndicator()

            delay(1000)

            // To error simulation
            if (Random.nextInt() % 2 == 0) {
                throw Exception("")
            }

            val comments = discussionsApi.getCommentsListForPost(
                pageOffset,
                pageSize,
                postToProcess
            )

            if (comments.size < pageSize) {
                endOfDataReached = true
            }

            if (comments.isEmpty() && pageOffset == 0) {
                postListDataSource.addEmptyCommentsStub()
            } else {
                @Suppress("NestedLambdaShadowedImplicitParameter")
                val mapperComments =
                    withContext(dispatchersProvider.calculationsDispatcher) {
                        comments
                            .map {
                                commentToModelMapper.map(it, 0)
                                    .also {
                                        loadedComments[it.contentId] = it
                                        storeComment(it)
                                    }
                            }
                            .filter { !wasCommentPosted(it.contentId) }
                    }

                postListDataSource.addFirstLevelComments(mapperComments)

                pageOffset += pageSize
            }
        } catch (ex: Exception) {
            postListDataSource.addRetryLoadingComments()
            isInErrorState = true

            throw ex
        }
    }
}