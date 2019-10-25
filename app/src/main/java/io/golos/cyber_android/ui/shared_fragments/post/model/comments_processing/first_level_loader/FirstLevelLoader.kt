package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.first_level_loader

import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel

interface FirstLevelLoader {
    /**
     * Loads the very first first-levels comments page
     */
    suspend fun loadStartPage()

    /**
     * Loads a next first-levels comments page
     */
    suspend fun loadNextPageByScroll()

    /**
     * Try to reload
     */
    suspend fun retryLoadPage()

    fun getLoadedComment(commentId: DiscussionIdModel): CommentModel
}