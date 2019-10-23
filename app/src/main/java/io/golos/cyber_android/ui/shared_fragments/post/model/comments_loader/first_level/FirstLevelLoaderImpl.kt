package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.first_level

import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentNavigableMap
import kotlin.random.Random

class FirstLevelLoaderImpl
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val pageSize: Int
) : FirstLevelLoader {

    private var pageOffset = 0
    private var endOfDataReached = false

    private var loadingInProgress = false
    private var isInErrorState = false

    private val loadedComments = ConcurrentHashMap<DiscussionIdModel, CommentModel>()

    /**
     * Loads the very first first-levels comments page
     */
    override suspend fun loadStartPage() =
        loading {
            if(pageOffset > 0) {          // already loaded
                return@loading
            }

            loadPage()
        }

    /**
     * Loads a next first-levels comments page
     */
    override suspend fun loadNextPageByScroll() =
        loading {
            if(endOfDataReached) {
                return@loading
            }

            loadPage()
        }

    override fun getLoadedComment(commentId: DiscussionIdModel): CommentModel = loadedComments[commentId]!!

    /**
     * Try to reload
     */
    override suspend fun retryLoadPage() {
        isInErrorState = false
        loading {
            loadPage()
        }
    }

    private suspend fun loading(loadingAction: suspend () -> Unit) {
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

    private suspend fun loadPage() {
        try {
            postListDataSource.addLoadingCommentsIndicator()

            delay(1000)

            // To error simulation
            if(Random.nextInt () % 2 == 0) {
                throw Exception("")
            }

            val comments = discussionsApi.getCommentsListForPost(pageOffset, pageSize, postToProcess)

            if(comments.size < pageSize) {
                endOfDataReached = true
            }

            @Suppress("NestedLambdaShadowedImplicitParameter")
            val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                comments.map {
                    commentToModelMapper.map(it)
                        .also {
                            loadedComments[it.contentId] = it
                        }
                }
            }

            postListDataSource.addFirstLevelComments(mapperComments)

            pageOffset+=pageSize
        } catch (ex: Exception) {
            postListDataSource.addRetryLoadingComments()
            isInErrorState = true

            throw ex
        }
    }
}