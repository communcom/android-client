package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.*
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Contains list with post data
 */
@FragmentScope
class PostListDataSourceImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val currentUserRepository: CurrentUserRepositoryRead
) : PostListDataSource,
    PostListDataSourcePostControls,
    PostListDataSourceComments {

    private val postList = mutableListOf<VersionedListItem>()

    private val _post = MutableLiveData<List<VersionedListItem>>()
    override val post: LiveData<List<VersionedListItem>> = _post

    private val hasPostTitle: Boolean
        get() = postList.indexOfFirst { it is PostTitleListItem } != -1

    // For thread-safety
    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    override suspend fun createOrUpdatePostData(postModel: PostModel) =
        updateSafe {
            createOrUpdatePostTitle(postModel)
            createOrUpdatePostBody(postModel)
            createOrUpdatePostControls(postModel)
            createOrUpdateCommentsTitle(postModel)
        }

    override suspend fun updatePostVoteStatus(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long) =
        updatePostControls { oldControls ->
            oldControls.copy(
                isUpVoteActive = isUpVoteActive ?: oldControls.isUpVoteActive,
                isDownVoteActive = isDownVoteActive ?: oldControls.isDownVoteActive,
                voteBalance = oldControls.voteBalance + voteBalanceDelta )
        }

    override suspend fun updateCommentsSorting(sortingType: SortingType) =
        updateSafe {
            val commentsTitleIndex = postList.indexOfFirst { it is CommentsTitleListItem }
            val commentsTitle = postList[commentsTitleIndex] as CommentsTitleListItem

            if(sortingType != commentsTitle.soring) {
                postList[commentsTitleIndex] = commentsTitle.copy(version = commentsTitle.version + 1, soring = sortingType)
            }
        }

    override suspend fun addFirstLevelComments(comments: List<CommentModel>) =
        updateSafe {
            postList.removeAt(postList.lastIndex)       // Removing Loading indicator

            comments
                .map { CommentsMapper.mapToFirstLevel(it, currentUserRepository.authState!!.user.name) }
                .let {postList.addAll(it)}
        }

    override suspend fun addLoadingCommentsIndicator(isFirstLevel: Boolean) {
        updateSafe {
            // Remove Retry button if needed
            val lastItem = postList.last()
            if(lastItem is FirstLevelCommentRetryListItem || lastItem is SecondLevelCommentRetryListItem) {
                postList.removeAt(postList.lastIndex)
            }

            // Add Loading indicator
            val indicator = if(isFirstLevel) {
                FirstLevelCommentLoadingListItem(IdUtil.generateLongId(), 0)
            }
            else {
                SecondLevelCommentLoadingListItem(IdUtil.generateLongId(), 0)
            }
            postList.add(indicator)
        }
    }

    override suspend fun addRetryLoadingComments(isFirstLevel: Boolean) {
        updateSafe {
            // Remove Loading indicator if needed
            val lastItem = postList.last()
            if(lastItem is FirstLevelCommentLoadingListItem || lastItem is SecondLevelCommentLoadingListItem) {
                postList.removeAt(postList.lastIndex)
            }

            // Add Retry button
            val indicator = if(isFirstLevel) {
                FirstLevelCommentRetryListItem(IdUtil.generateLongId(), 0)
            }
            else {
                SecondLevelCommentRetryListItem(IdUtil.generateLongId(), 0)
            }
            postList.add(indicator)
        }
    }

    private suspend fun updateSafe(action: () -> Unit) =
        withContext(singleThreadDispatcher) {
            action()

            withContext(dispatchersProvider.uiDispatcher) {
                _post.value = postList
            }
        }

    private suspend fun updatePostControls(updateAction: (PostControlsListItem) -> PostControlsListItem) {
        updateSafe {
            val controlsIndex = postList.indexOfFirst { it is PostControlsListItem }
            val controls = postList[controlsIndex] as PostControlsListItem
            postList[controlsIndex] = updateAction(controls.copy(version = controls.version + 1))
        }
    }

    private fun createOrUpdatePostTitle(postModel: PostModel) {
        val oldTitle = postList.singleOrNull { it is PostTitleListItem }

        val newTitle = postModel.content.body.postBlock.title?.let {
            PostTitleListItem(IdUtil.generateLongId(), 0, it)
        }

        when {
            oldTitle == null && newTitle == null -> {}
            oldTitle == null && newTitle != null -> postList.add(0, newTitle)
            oldTitle != null && newTitle == null -> postList.remove(oldTitle)
            oldTitle != null && newTitle != null ->
                postList[0] = (oldTitle as PostTitleListItem).copy(version = oldTitle.version + 1, title = newTitle.title)
        }
    }

    private fun createOrUpdatePostBody(postModel: PostModel) {
        val oldBodyIndex = postList.indexOfFirst { it is PostBodyListItem }

        if(oldBodyIndex == -1) {
            postList.add(PostBodyListItem(IdUtil.generateLongId(), 0, postModel.content.body.postBlock))
        } else {
            val oldBody = postList[oldBodyIndex]
            postList[oldBodyIndex] = PostBodyListItem(oldBody.id, oldBody.version + 1, postModel.content.body.postBlock)
        }
    }

    private fun createOrUpdatePostControls(postModel: PostModel) {
        val oldControlsIndex = postList.indexOfFirst { it is PostControlsListItem }

        val controls = PostControlsListItem(
            IdUtil.generateLongId(),
            version = 0,
            voteBalance = postModel.votes.upCount - postModel.votes.downCount,
            isUpVoteActive = false,
            isDownVoteActive = false,
            totalComments = postModel.comments.count,
            totalViews = postModel.stats.viewsCount
        )

        if(oldControlsIndex == -1) {
            postList.add(controls)
        } else {
            val oldControls = postList[oldControlsIndex]
            postList[oldControlsIndex] = controls.copy(id = oldControls.id, version = oldControls.version + 1)
        }
    }

    private fun createOrUpdateCommentsTitle(postModel: PostModel) {
        val oldTitle = postList.singleOrNull { it is CommentsTitleListItem }

        val newTitle = if(postModel.comments.count != 0L) {
            CommentsTitleListItem(IdUtil.generateLongId(), 0, SortingType.INTERESTING_FIRST) }
        else  {
            null
        }

        val commentsTitleIndex = if(hasPostTitle) 3 else 2

        when {
            oldTitle == null && newTitle == null -> {}
            oldTitle == null && newTitle != null -> postList.add(commentsTitleIndex, newTitle)
            oldTitle != null && newTitle == null -> postList.remove(oldTitle)
            oldTitle != null && newTitle != null ->
                postList[commentsTitleIndex] = newTitle.copy(id = oldTitle.id, version = oldTitle.version + 1)
        }
    }
}