package io.golos.cyber_android.ui.screens.post_filters

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostFiltersModelImpl @Inject constructor(private val postFiltersHolder: PostFiltersHolder): PostFiltersModel {

    override suspend fun updateFilters(filters: PostFiltersHolder.FeedFilters) {
        postFiltersHolder.updateFeedFilters(filters)
    }

    override val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters> = postFiltersHolder.feedFiltersFlow
}