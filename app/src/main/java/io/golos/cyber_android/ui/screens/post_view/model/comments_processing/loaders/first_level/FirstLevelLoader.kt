package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level

import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.ContentIdDomain

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

    fun getLoadedComment(commentId: ContentIdDomain): CommentDomain
}