package io.golos.cyber_android.ui.shared_fragments.post.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.post.post_dto.PostMetadata

interface PostPageModel : ModelBase {
    val postId: DiscussionIdModel

    val postMetadata: PostMetadata

    val post: LiveData<List<VersionedListItem>>

    val commentsPageSize: Int

    suspend fun loadPost()

    fun getPostHeader(): PostHeader

    suspend fun getUserId(userName: String): String

    suspend fun deletePost()

    suspend fun voteForPost(isUpVote: Boolean)

    suspend fun updateCommentsSorting(sortingType: SortingType)

    suspend fun loadStartFirstLevelCommentsPage()

    suspend fun loadNextFirstLevelCommentsPage()

    suspend fun retryLoadingFirstLevelCommentsPage()

    suspend fun loadNextSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadingSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(commentText: String)

    suspend fun deleteComment(commentId: DiscussionIdModel)

    fun getCommentText(commentId: DiscussionIdModel): List<CharSequence>

    suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String)

    suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String)
}