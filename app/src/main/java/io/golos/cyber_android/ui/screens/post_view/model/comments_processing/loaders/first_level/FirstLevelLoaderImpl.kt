package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class FirstLevelLoaderImpl
constructor(
    private val postToProcess: ContentIdDomain,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val pageSize: Int,
    commentsStorage: CommentsStorage,
    currentUserRepository: CurrentUserRepository
) : CommentsLoaderBase(
    dispatchersProvider,
    commentsStorage
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
            if(endOfDataReached){
                return
            }

            postListDataSource.addLoadingCommentsIndicator()

            val comments = discussionRepository.getComments(
                offset = pageOffset,
                pageSize = pageSize,
                commentType = CommentDomain.CommentTypeDomain.POST,
                userId = postToProcess.userId,
                permlink = postToProcess.permlink,
                communityId = postToProcess.communityId)

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
                                commentToModelMapper.map(it)
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
            Timber.e(ex)
            isInErrorState = true

            throw ex
        }
    }
}