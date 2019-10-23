package io.golos.cyber_android.ui.shared_fragments.post.model.comments_loader.second_level

interface SecondLevelLoader {
    /**
     * Loads a next comments page
     */
    suspend fun loadNextPage()

    /**
     * Try to reload
     */
    suspend fun retryLoadPage()
}