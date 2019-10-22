package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentsLoaderImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper
) : CommentsLoader {
    private val pageSize = 20
    private var offset = 0

    override suspend fun loadFirstLevelPage() {
        withContext(dispatchersProvider.ioDispatcher) {
            val comments = discussionsApi.getCommentsList(offset, pageSize, postToProcess)

            val mapperComments = withContext(dispatchersProvider.calculationsDispatcher) {
                comments.map { commentToModelMapper.map(it) }
            }

            postListDataSource.addFirstLevelComments(mapperComments)
        }
    }
}