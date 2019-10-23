package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import kotlin.random.Random

class CommentsLoaderImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper
) : CommentsLoader {

    override val pageSize
        get() = 20

    private var firstLevelPageOffset = 0
    private var firstLevelEndOfDataReached = false

    private var loadingInProgress = false
    private var isInErrorState = false

    /**
     * Loads the very first first-levels comments page
     */
    override suspend fun loadStartFirstLevelPage() =
        loading {
            if(firstLevelPageOffset > 0) {          // already loaded
                return@loading
            }

            loadFirstLevelPage()
        }

    /**
     * Loads a next first-levels comments page
     */
    override suspend fun loadNextFirstLevelPageByScroll() =
        loading {
            if(firstLevelEndOfDataReached) {
                return@loading
            }

            loadFirstLevelPage()
        }

    /**
     * Try to reload
     */
    override suspend fun retryLoadFirstLevelPage() {
        isInErrorState = false
        loading {
            loadFirstLevelPage()
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

    private suspend fun loadFirstLevelPage() {
        try {
            postListDataSource.addLoadingCommentsIndicator(true)

            delay(1000)

            // To error simulation
            if(Random.nextInt () % 2 == 0) {
                throw Exception("")
            }

            val comments = discussionsApi.getCommentsList(firstLevelPageOffset, pageSize, postToProcess)

            if(comments.size < pageSize) {
                firstLevelEndOfDataReached = true
            }

            val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                comments.map { commentToModelMapper.map(it) }
            }

            postListDataSource.addFirstLevelComments(mapperComments)

            firstLevelPageOffset+=pageSize
        } catch (ex: Exception) {
            postListDataSource.addRetryLoadingComments(true)
            isInErrorState = true

            throw ex
        }
    }
}