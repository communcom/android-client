package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader

interface CommentsLoader {
    /**
     * Loads first/next first-levels comments page
     */
    suspend fun loadFirstLevelPage()
}