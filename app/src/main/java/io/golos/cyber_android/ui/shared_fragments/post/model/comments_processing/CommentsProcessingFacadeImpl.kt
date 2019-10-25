package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.first_level_loader.FirstLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.first_level_loader.FirstLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.second_level_loader.SecondLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.second_level_loader.SecondLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.data.repositories.discussion.DiscussionRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CommentsProcessingFacadeImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper
): CommentsProcessingFacade {

    override val pageSize: Int
        get() = 20

    private val secondLevelLoaders = mutableMapOf<DiscussionIdModel, SecondLevelLoader>()

    private val firstLevelCommentsLoader: FirstLevelLoader by lazy {
        FirstLevelLoaderImpl(
            postToProcess,
            postListDataSource,
            discussionsApi,
            dispatchersProvider,
            commentToModelMapper,
            pageSize
        )
    }

    override suspend fun loadStartFirstLevelPage() = firstLevelCommentsLoader.loadStartPage()

    override suspend fun loadNextFirstLevelPageByScroll() = firstLevelCommentsLoader.loadNextPageByScroll()

    override suspend fun retryLoadFirstLevelPage() = firstLevelCommentsLoader.retryLoadPage()

    override suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel) {
        getSecondLevelLoader(parentCommentId).loadNextPage()
    }

    override suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel) =
        getSecondLevelLoader(parentCommentId).retryLoadPage()

    private fun getSecondLevelLoader(parentCommentId: DiscussionIdModel): SecondLevelLoader {
        return secondLevelLoaders[parentCommentId]
            ?: SecondLevelLoaderImpl(
                parentCommentId,
                firstLevelCommentsLoader.getLoadedComment(parentCommentId).childTotal.toInt(),
                postListDataSource,
                discussionsApi,
                dispatchersProvider, commentToModelMapper,
                pageSize
            ).also {
                secondLevelLoaders[parentCommentId] = it
            }
    }

    override suspend fun sendComment(commentText: String, postHasComments: Boolean) {
        if(!postHasComments) {
            postListDataSource.addCommentsHeader()
        }
        postListDataSource.addLoadingForNewComment()

        try {
            val commentModel = withContext(dispatchersProvider.ioDispatcher) {
                delay(1000)
                discussionRepository.createCommentForPost(commentText, postToProcess)
            }
            postListDataSource.addNewComment(commentModel)
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForNewComment()
            throw ex
        }
    }
}