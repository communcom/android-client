package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

import io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.first_level.FirstLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.first_level.FirstLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.second_level.SecondLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.second_level.SecondLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import javax.inject.Inject

class CommentsLoadingFacadeImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper
): CommentsLoadingFacade {

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
}