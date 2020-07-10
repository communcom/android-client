package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class FirstLevelLoaderImpl
constructor(
    private val postToProcess: ContentIdDomain,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val pageSize: Int,
    commentsStorage: CommentsStorage
) : CommentsLoaderBase(
    dispatchersProvider,
    commentsStorage
),
    FirstLevelLoader {

    private val loadedComments = ConcurrentHashMap<ContentIdDomain, CommentDomain>()

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

    override fun getLoadedComment(commentId: ContentIdDomain): CommentDomain = loadedComments[commentId]!!

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
                val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                    comments.forEach {
                        loadedComments[it.contentId] = it
                        storeComment(it)
                    }
                    comments.filter { !wasCommentPosted(it.contentId) }
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