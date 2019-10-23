package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

interface CommentsLoader {
    val pageSize: Int

    /**
     * Loads the very first first-levels comments page
     */
    suspend fun loadStartFirstLevelPage()

    /**
     * Loads a next first-levels comments page
     */
    suspend fun loadNextFirstLevelPageByScroll()

    /**
     * Try to reload
     */
    suspend fun retryLoadFirstLevelPage()
}