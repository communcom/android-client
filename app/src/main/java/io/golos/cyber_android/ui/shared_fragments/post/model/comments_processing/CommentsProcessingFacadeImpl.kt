package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing

import dagger.Lazy
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.first_level.FirstLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.first_level.FirstLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.second_level.SecondLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.second_level.SecondLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.posted_comments_collection.PostedCommentsCollection
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
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
    private val commentToModelMapper: CommentToModelMapper,
    private val postedCommentsCollection: Lazy<PostedCommentsCollection>
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
            pageSize,
            postedCommentsCollection.get()
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
                pageSize,
                postedCommentsCollection.get()
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
            postedCommentsCollection.get().addEntity(commentModel.contentId)
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForNewComment()
            throw ex
        }
    }
}