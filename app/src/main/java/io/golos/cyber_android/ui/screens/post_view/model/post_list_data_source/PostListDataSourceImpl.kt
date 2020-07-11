package io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.mappers.mapToPost
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.*
import io.golos.cyber_android.ui.shared.recycler_view.GroupListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.utils.id.IdUtil
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

    override fun isNotComments(): Boolean {
        val firstLevelComment = postList.find { it is FirstLevelCommentListItem }
        return firstLevelComment == null
    }

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

    override suspend fun addFirstLevelComments(comments: List<CommentDomain>) =
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
                val commentListItem = CommentsMapper.mapToFirstLevel(rawComment, currentUserRepository.userId)
                if (commentPosition != -1) {
                    postList.add(commentPosition, commentListItem)
                } else {
                    postList.add(commentListItem)
                }

                // Add collapsed comments list item
                if (rawComment.childCommentsCount > 0) {
                    postList.add(
                        SecondLevelCommentCollapsedListItem(
                            id = IdUtil.generateLongId(),
                            version = 0,
                            isFirstItem = false,
                            isLastItem = false,
                            totalChild = rawComment.childCommentsCount.toLong(),
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
    override suspend fun addLoadingCommentsIndicator(parentCommentId: ContentIdDomain, commentsAdded: Int) =
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
    override suspend fun addRetryLoadingComments(parentCommentId: ContentIdDomain, commentsAdded: Int) =
        updateSafe {
            val parentCommentIndex = getCommentIndex(parentCommentId)
            if (parentCommentIndex == -1) {
                return@updateSafe
            }

            postList[parentCommentIndex + commentsAdded + 1] =
                SecondLevelCommentRetryListItem(IdUtil.generateLongId(), 0, false, false, parentCommentId)
            sortPostItems()
        }

    /**
     * [repliedAuthors] - id of all loaded comments and their repliedAuthors
     */
    override suspend fun addSecondLevelComments(
        parentCommentId: ContentIdDomain,
        comments: List<CommentDomain>,
        repliedAuthors: Map<ContentIdDomain, UserBriefDomain>,
        commentsAdded: Int,
        totalComments: Int,
        isEndOfDataReached: Boolean,
        nextTopCommentAuthor: UserBriefDomain?
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
                .map { CommentsMapper.mapToSecondLevel(it, currentUserRepository.userId, repliedAuthors) }
                .union(
                    if (isEndOfDataReached) {
                        listOf()
                    } else {
                        listOf(
                            SecondLevelCommentCollapsedListItem(     // Collapsed comments
                                IdUtil.generateLongId(),
                                0,
                                false,
                                false,
                                (totalComments - commentsAdded - comments.size).toLong(),
                                parentCommentId
                            )
                        )
                    }
                )
                .let {
                    postList.addAll(indexToNewData, it as Collection<VersionedListItem>)
                }
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
                postList.add(FirstLevelCommentLoadingListItem(IdUtil.generateLongId(), 0))
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
                postList.add(CommentsTitleListItem(IdUtil.generateLongId(), 0, false, false, SortingType.INTERESTING_FIRST))
                sortPostItems()
            }
        }

    override suspend fun updateDonation(donation: DonationsDomain) {
        updateSafe {
            val controlsIndex = postList.indexOfFirst { it is PostControlsListItem }

            if (controlsIndex != -1) {
                val controls = postList[controlsIndex] as PostControlsListItem
                postList[controlsIndex] = controls.copy(version = controls.version+1, post = controls.post.copy(donation = donation))
            }
        }
    }

    /**
     * Adds new first-level comment (the loader'll be replaced)
     */
    override suspend fun addNewComment(comment: CommentDomain) =
        updateSafe {
            postList.add(CommentsMapper.mapToFirstLevel(comment, currentUserRepository.userId))
            sortPostItems()
        }

    override suspend fun updateCommentState(commentId: ContentIdDomain, state: CommentListItemState) =
        updateComment(commentId) {
            when (it) {
                is FirstLevelCommentListItem -> it.copy(version = it.version + 1, state = state)
                is SecondLevelCommentListItem -> it.copy(version = it.version + 1, state = state)
                else -> throw UnsupportedOperationException("This comment type is not supported")
            }
        }

    override suspend fun deleteComment(commentId: ContentIdDomain) =
        updateSafe {
            postList.removeAt(getCommentIndex(commentId))
        }

    override suspend fun deleteCommentsHeader() =
        updateSafe {
            postList.removeAll { it -> it is CommentsTitleListItem }
        }

    override suspend fun updateComment(newComment: CommentDomain) =
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

    override suspend fun addLoadingForRepliedComment(repliedCommentId: ContentIdDomain) =
        updateSafe {
            postList.add(getCommentIndex(repliedCommentId) + 1, SecondLevelCommentLoadingListItem(IdUtil.generateLongId(), 0))
            sortPostItems()
        }

    override suspend fun addReplyComment(
        repliedCommentId: ContentIdDomain,
        repliedCommentAuthor: UserBriefDomain,
        repliedCommentLevel: Int,
        commentModel: CommentDomain
    ) =
        updateSafe {
            postList[getCommentIndex(repliedCommentId) + 1] =
                CommentsMapper.mapToSecondLevel(
                    commentModel,
                    currentUserRepository.userId,
                    repliedCommentAuthor,
                    repliedCommentLevel
                )
            sortPostItems()
        }

    override suspend fun removeLoadingForRepliedComment(repliedCommentId: ContentIdDomain) =
        updateSafe {
            postList.removeAt(getCommentIndex(repliedCommentId) + 1)
            sortPostItems()
        }

    override suspend fun updateCommentVoteStatus(
        commentId: ContentIdDomain,
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
            PostTitleListItem(IdUtil.generateLongId(), 0, false, false, it)
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
                    false,
                    false,
                    postDomain.body!!
                )
            )
        } else {
            val oldBody = postList[oldBodyIndex]
            postList[oldBodyIndex] = PostBodyListItem(
                oldBody.id,
                oldBody.version + 1,
                false,
                false,
                postDomain.body!!
            )
        }
    }

    private fun createOrUpdatePostControls(postDomain: PostDomain) {
        val oldControlsIndex = postList.indexOfFirst { it is PostControlsListItem }

        val controls = PostControlsListItem(
            IdUtil.generateLongId(),
            version = 0,
            isFirstItem = false,
            isLastItem = false,
            voteBalance = postDomain.votes.upCount - postDomain.votes.downCount,
            isUpVoteActive = postDomain.votes.hasUpVote,
            isDownVoteActive = postDomain.votes.hasDownVote,
            totalComments = postDomain.stats?.commentsCount ?: 0,
            totalViews = postDomain.stats?.viewCount ?: 0,
            shareUrl = postDomain.shareUrl,
            post = postDomain.mapToPost())

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
                false,
                false,
                SortingType.INTERESTING_FIRST
            )
            postList.add(newTitle)
            sortPostItems()
        }
    }

    private suspend fun updateComment(commentId: ContentIdDomain, updateAction: (CommentListItem) -> CommentListItem) {
        updateSafe {
            val commentIndex = getCommentIndex(commentId)
            if(commentIndex != -1){
                postList[commentIndex] = updateAction(postList[commentIndex] as CommentListItem)
            }
        }
    }

    private fun getCommentIndex(commentId: ContentIdDomain) =
        postList.indexOfFirst { it is CommentListItem && it .externalId == commentId }

    private fun sortPostItems() {
        (postList as MutableList<GroupListItem>).sortWith(Comparator<GroupListItem> { item1, item2 ->
            if (item1.groupId > item2.groupId) 1
            else if (item1.groupId > item2.groupId) -1
            else 0 }
        )
    }
}