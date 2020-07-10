package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.second_level

import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.CommentsLoaderBase
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import timber.log.Timber

class SecondLevelLoaderImpl
constructor(
    private val postIdDomain: ContentIdDomain,
    private val parentComment: ContentIdDomain,
    private val totalComments: Int,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val pageSize: Int,
    commentsStorage: CommentsStorage
) : CommentsLoaderBase(
    dispatchersProvider,
    commentsStorage
),
    SecondLevelLoader {

    // Loaded comments and their author
    private val authors = mutableMapOf<ContentIdDomain, UserBriefDomain>()

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

            val comments = discussionRepository.getComments(
                offset = pageOffset,
                pageSize = pageSize + 1,
                commentType = CommentDomain.CommentTypeDomain.POST,
                userId = postIdDomain.userId,
                permlink = postIdDomain.permlink,
                parentComment = ParentCommentIdentifierDomain(parentComment.permlink, parentComment.userId),
                communityId = postIdDomain.communityId
            )

            if(comments.size < pageSize + 1) {
                endOfDataReached = true
            }

            @Suppress("NestedLambdaShadowedImplicitParameter")
            val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                comments.forEach {
                    authors[it.contentId] = it.author
                    storeComment(it)
                }
                comments.filter { !wasCommentPosted(it.contentId) }
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
            Timber.e(ex)
            postListDataSource.addRetryLoadingComments(parentComment, pageOffset)
            isInErrorState = true

            throw ex
        }
    }
}