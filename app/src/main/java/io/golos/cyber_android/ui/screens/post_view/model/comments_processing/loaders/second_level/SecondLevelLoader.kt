package io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.second_level

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