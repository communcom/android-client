package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.*
import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.dto.PostDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionAuthorModel
import io.golos.domain.use_cases.model.DiscussionIdModel
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

    // For thread-safety
    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    override suspend fun createOrUpdatePostData(postDomain: PostDomain) {
        updateSafe {
            createOrUpdatePostTitle(postDomain)
            createOrUpdatePostBody(postDomain)
            createOrUpdatePostControls(postDomain)
            createOrUpdateCommentsTitle(postDomain)
        }
    }

    override suspend fun updatePostVoteStatus(isUpVoteActive: Boolean?, isDownVoteActive: Boolean?, voteBalanceDelta: Long) =
        updatePostControls { oldControls ->
            oldControls.copy(
                isUpVoteActive = isUpVoteActive ?: oldControls.isUpVoteActive,
                isDownVoteActive = isDownVoteActive ?: oldControls.isDownVoteActive,
                voteBalance = oldControls.voteBalance + voteBalanceDelta
            )
        }

    override suspend fun updateCommentsSorting(sortingType: SortingType) =
        updateSafe {
            val commentsTitleIndex = postList.indexOfFirst { it is CommentsTitleListItem }
            val commentsTitle = postList[commentsTitleIndex] as CommentsTitleListItem

            if (sortingType != commentsTitle.soring) {
                postList[commentsTitleIndex] = commentsTitle.copy(version = commentsTitle.version + 1, soring = sortingType)
            }
        }

    override suspend fun addFirstLevelComments(comments: List<CommentModel>) =
        updateSafe {
            postList.removeAll { it is EmptyCommentsListItem }

            postList.removeAll { it is FirstLevelCommentLoadingListItem }       // Removing Loading indicator

            comments.forEachIndexed { commentIndex, rawComment ->
                var commentPosition = -1
                if (commentIndex == 0) {
                    val commentTitle = postList.findLast { it -> it is CommentsTitleListItem }
                    if (commentTitle != null) {
                        commentPosition = postList.indexOf(commentTitle) + 1
                    }
                }

                // Add comment
                val commentListItem = CommentsMapper.mapToFirstLevel(rawComment, currentUserRepository.userId.userId)
                if (commentPosition != -1) {
                    postList.add(commentPosition, commentListItem)
                } else {
                    postList.add(commentListItem)
                }

                // Add collapsed comments list item
                if (rawComment.childTotal > 0) {
                    postList.add(
                        SecondLevelCommentCollapsedListItem(
                            id = IdUtil.generateLongId(),
                            version = 0,
                            topCommentAuthor = rawComment.child[0].author,
                            currentUserId = currentUserRepository.userId.userId,
                            totalChild = rawComment.childTotal,
                            parentCommentId = commentListItem.externalId
                        )
                    )
                }
                sortPostItems()
            }
        }

    override suspend fun addLoadingCommentsIndicator() =
        updateSafe {
            val loadItem = postList.find { it is FirstLevelCommentLoadingListItem }
            if (loadItem == null) {
                FirstLevelCommentLoadingListItem(IdUtil.generateLongId(), 0)
                    .let { loadingIndicator ->
                        val retryItem = postList.find { it is FirstLevelCommentRetryListItem }
                        if (retryItem != null) {
                            val itemIndex = postList.indexOf(retryItem)
                            postList[itemIndex] = loadingIndicator // Replace Retry button if needed
                        } else {
                            postList.add(loadingIndicator)
                        }
                    }
                sortPostItems()
            }
        }

    override suspend fun addEmptyCommentsStub() {
        updateSafe {
            val currentEmptyStub = postList.find { it is EmptyCommentsListItem }
            if (currentEmptyStub == null) {
                EmptyCommentsListItem().let { item ->
                    val retryLoadItem = postList.find { it is FirstLevelCommentLoadingListItem }
                    if (retryLoadItem != null) {
                        val retryLoadItemIndex = postList.indexOf(retryLoadItem)
                        postList[retryLoadItemIndex] = item // Replace Loading indicator if needed
                    } else {
                        postList.add(item)
                    }
                }
                sortPostItems()
            }
        }
    }

    override suspend fun removeEmptyCommentsStub() = updateSafe {
        postList.removeAll { it is EmptyCommentsListItem }
    }

    override suspend fun addRetryLoadingComments() =
        updateSafe {
            val retryItem = postList.find { it is FirstLevelCommentRetryListItem }
            if (retryItem == null) {
                FirstLevelCommentRetryListItem(IdUtil.generateLongId(), 0)
                    .let { retryItem ->
                        val retryLoadItem = postList.find { it is FirstLevelCommentLoadingListItem }
                        if (retryLoadItem != null) {
                            val itemIndex = postList.indexOf(retryLoadItem)
                            postList[itemIndex] = retryItem // Replace Loading indicator if needed
                        } else {
                            postList.add(retryItem)
                        }
                    }
                sortPostItems()
            }
        }

    /**
     * Adds second-level Loading indicator
     */
    override suspend fun addLoadingCommentsIndicator(parentCommentId: DiscussionIdModel, commentsAdded: Int) =
        updateSafe {
            val parentCommentIndex = getCommentIndex(parentCommentId)
            if (parentCommentIndex == -1) {
                return@updateSafe
            }

            // Simply replace Collapse item or Retry button
            postList[parentCommentIndex + commentsAdded + 1] = SecondLevelCommentLoadingListItem(IdUtil.generateLongId(), 0)
            sortPostItems()
        }

    /**
     * Adds second-level Retry button
     */
    override suspend fun addRetryLoadingComments(parentCommentId: DiscussionIdModel, commentsAdded: Int) =
        updateSafe {
            val parentCommentIndex = getCommentIndex(parentCommentId)
            if (parentCommentIndex == -1) {
                return@updateSafe
            }

            postList[parentCommentIndex + commentsAdded + 1] =
                SecondLevelCommentRetryListItem(IdUtil.generateLongId(), 0, parentCommentId)
            sortPostItems()
        }

    /**
     * [repliedAuthors] - id of all loaded comments and their repliedAuthors
     */
    override suspend fun addSecondLevelComments(
        parentCommentId: DiscussionIdModel,
        comments: List<CommentModel>,
        repliedAuthors: Map<DiscussionIdModel, DiscussionAuthorModel>,
        commentsAdded: Int,
        totalComments: Int,
        isEndOfDataReached: Boolean,
        nextTopCommentAuthor: DiscussionAuthorModel?
    ) =

        updateSafe {
            val parentCommentIndex = getCommentIndex(parentCommentId)
            if (parentCommentIndex == -1) {
                return@updateSafe
            }

            val indexToNewData = parentCommentIndex + commentsAdded + 1

            // Removing collapsing control, loading indicator or retry button
            postList.removeAt(indexToNewData)

            // Add comments
            comments
                .map { CommentsMapper.mapToSecondLevel(it, currentUserRepository.userId.userId, repliedAuthors) }
                .union(
                    if (isEndOfDataReached) {
                        listOf()
                    } else {
                        listOf(
                            SecondLevelCommentCollapsedListItem(     // Collapsed comments
                                IdUtil.generateLongId(),
                                0,
                                nextTopCommentAuthor!!,
                                currentUserRepository.userId.userId,
                                (totalComments - commentsAdded - comments.size).toLong(),
                                parentCommentId
                            )
                        )
                    }
                )
                .let { postList.addAll(indexToNewData, it as Collection<VersionedListItem>) }
            sortPostItems()
        }

    /**
     * Adds first-level Loading indicator for a new comment
     */
    override suspend fun addLoadingForNewComment() =
        updateSafe {
            // Add to a fixed position - just after comments title
            val currentProgress = postList.find { it is FirstLevelCommentLoadingListItem }
            if (currentProgress == null) {
                postList.add(postList.size - 1, FirstLevelCommentLoadingListItem(IdUtil.generateLongId(), 0))
                sortPostItems()
            }
        }

    /**
     * Remove first-level Loading indicator for a new comment
     */
    override suspend fun removeLoadingForNewComment() =
        updateSafe {
            // Remove item from a fixed position - just after comments title
            postList.removeAll { it is FirstLevelCommentLoadingListItem }
        }

    override suspend fun addCommentsHeader() =
        updateSafe {
            val titleItem = postList.find { it is CommentsTitleListItem }
            if (titleItem == null) {
                postList.add(CommentsTitleListItem(IdUtil.generateLongId(), 0, SortingType.INTERESTING_FIRST))
                sortPostItems()
            }
        }

    /**
     * Adds new first-level comment (the loader'll be replaced)
     */
    override suspend fun addNewComment(comment: CommentModel) =
        updateSafe {
            postList.add(CommentsMapper.mapToFirstLevel(comment, currentUserRepository.userId.userId))
            sortPostItems()
        }

    override suspend fun updateCommentState(commentId: DiscussionIdModel, state: CommentListItemState) =
        updateComment(commentId) {
            when (it) {
                is FirstLevelCommentListItem -> it.copy(version = it.version + 1, state = state)
                is SecondLevelCommentListItem -> it.copy(version = it.version + 1, state = state)
                else -> throw UnsupportedOperationException("This comment type is not supported")
            }
        }

    override suspend fun deleteComment(commentId: DiscussionIdModel) =
        updateSafe {
            postList.removeAt(getCommentIndex(commentId))
        }

    override suspend fun deleteCommentsHeader() =
        updateSafe {
            postList.removeAll { it -> it is CommentsTitleListItem }
        }

    override suspend fun updateComment(newComment: CommentModel) =
        updateComment(newComment.contentId) {
            when (it) {
                is FirstLevelCommentListItem ->
                    it.copy(
                        version = it.version + 1,
                        state = CommentListItemState.NORMAL,
                        content = newComment.body
                    )

                is SecondLevelCommentListItem ->
                    it.copy(
                        version = it.version + 1,
                        state = CommentListItemState.NORMAL,
                        content = newComment.body
                    )

                else -> throw UnsupportedOperationException("This comment type is not supported")
            }
        }

    override suspend fun addLoadingForRepliedComment(repliedCommentId: DiscussionIdModel) =
        updateSafe {
            postList.add(getCommentIndex(repliedCommentId) + 1, SecondLevelCommentLoadingListItem(IdUtil.generateLongId(), 0))
            sortPostItems()
        }

    override suspend fun addReplyComment(
        repliedCommentId: DiscussionIdModel,
        repliedCommentAuthor: DiscussionAuthorModel,
        repliedCommentLevel: Int,
        commentModel: CommentModel
    ) =
        updateSafe {
            postList[getCommentIndex(repliedCommentId) + 1] =
                CommentsMapper.mapToSecondLevel(
                    commentModel,
                    currentUserRepository.userId.userId,
                    repliedCommentAuthor,
                    repliedCommentLevel
                )
            sortPostItems()
        }

    override suspend fun removeLoadingForRepliedComment(repliedCommentId: DiscussionIdModel) =
        updateSafe {
            postList.removeAt(getCommentIndex(repliedCommentId) + 1)
            sortPostItems()
        }

    override suspend fun updateCommentVoteStatus(
        commentId: DiscussionIdModel,
        isUpVoteActive: Boolean?,
        isDownVoteActive: Boolean?,
        voteBalanceDelta: Long
    ) = updateComment(commentId) { commentListItem ->
        when (commentListItem) {
            is FirstLevelCommentListItem ->
                commentListItem.copy(
                    version = commentListItem.version + 1,
                    isUpVoteActive = isUpVoteActive ?: commentListItem.isUpVoteActive,
                    isDownVoteActive = isDownVoteActive ?: commentListItem.isDownVoteActive,
                    voteBalance = commentListItem.voteBalance + voteBalanceDelta
                )
            is SecondLevelCommentListItem ->
                commentListItem.copy(
                    version = commentListItem.version + 1,
                    isUpVoteActive = isUpVoteActive ?: commentListItem.isUpVoteActive,
                    isDownVoteActive = isDownVoteActive ?: commentListItem.isDownVoteActive,
                    voteBalance = commentListItem.voteBalance + voteBalanceDelta
                )
            else -> throw UnsupportedOperationException("This comment list item is not supported")
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

    private fun createOrUpdatePostTitle(postDomain: PostDomain) {
        val oldTitle = postList.singleOrNull { it is PostTitleListItem }

        val newTitle = postDomain.body?.title?.let {
            PostTitleListItem(IdUtil.generateLongId(), 0, it)
        }

        when {
            oldTitle == null && newTitle == null -> {
            }
            oldTitle == null && newTitle != null -> postList.add(0, newTitle)
            oldTitle != null && newTitle == null -> postList.remove(oldTitle)
            oldTitle != null && newTitle != null ->
                postList[0] = (oldTitle as PostTitleListItem).copy(version = oldTitle.version + 1, title = newTitle.title)
        }
    }

    private fun createOrUpdatePostBody(postDomain: PostDomain) {
        val oldBodyIndex = postList.indexOfFirst { it is PostBodyListItem }

        if (oldBodyIndex == -1) {
            postList.add(
                PostBodyListItem(
                    IdUtil.generateLongId(),
                    0,
                    postDomain.body!!
                )
            )
        } else {
            val oldBody = postList[oldBodyIndex]
            postList[oldBodyIndex] = PostBodyListItem(
                oldBody.id,
                oldBody.version + 1,
                postDomain.body!!
            )
        }
    }

    private fun createOrUpdatePostControls(postDomain: PostDomain) {
        val oldControlsIndex = postList.indexOfFirst { it is PostControlsListItem }

        val controls = PostControlsListItem(
            IdUtil.generateLongId(),
            version = 0,
            voteBalance = postDomain.votes.upCount - postDomain.votes.downCount,
            isUpVoteActive = postDomain.votes.hasUpVote,
            isDownVoteActive = postDomain.votes.hasDownVote,
            totalComments = postDomain.stats?.commentsCount ?: 0,
            totalViews = postDomain.stats?.viewCount ?: 0
        )

        if (oldControlsIndex == -1) {
            postList.add(controls)
        } else {
            val oldControls = postList[oldControlsIndex]
            postList[oldControlsIndex] = controls.copy(id = oldControls.id, version = oldControls.version + 1)
        }
    }

    private fun createOrUpdateCommentsTitle(postDomain: PostDomain) {
        val oldTitle = postList.singleOrNull { it is CommentsTitleListItem }
        if(oldTitle == null){
            val newTitle = CommentsTitleListItem(
                IdUtil.generateLongId(),
                0,
                SortingType.INTERESTING_FIRST
            )
            postList.add(newTitle)
            sortPostItems()
        }
    }

    private suspend fun updateComment(commentId: DiscussionIdModel, updateAction: (CommentListItem) -> CommentListItem) {
        updateSafe {
            val commentIndex = getCommentIndex(commentId)
            if(commentIndex != -1){
                postList[commentIndex] = updateAction(postList[commentIndex] as CommentListItem)
            }
        }
    }

    private fun getCommentIndex(commentId: DiscussionIdModel) =
        postList.indexOfFirst { it is CommentListItem && it.externalId == commentId }

    private fun sortPostItems() {
        (postList as MutableList<GroupListItem>).sortWith(Comparator<GroupListItem> { item1, item2 ->
            if (item1.groupId > item2.groupId) 1
            else if (item1.groupId > item2.groupId) -1
            else 0 }
        )
    }
}