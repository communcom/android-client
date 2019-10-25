package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing

import io.golos.domain.interactors.model.DiscussionIdModel

interface CommentsProcessingFacade {
    val pageSize: Int

    suspend fun loadStartFirstLevelPage()

    suspend fun loadNextFirstLevelPageByScroll()

    suspend fun retryLoadFirstLevelPage()

    suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(commentText: String, postHasComments: Boolean)
}