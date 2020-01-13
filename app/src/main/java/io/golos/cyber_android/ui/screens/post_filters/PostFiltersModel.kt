package io.golos.cyber_android.ui.screens.post_filters

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import kotlinx.coroutines.flow.Flow

interface PostFiltersModel: ModelBase {

    suspend fun updateFilters(filters: PostFiltersHolder.FeedFilters)

    suspend fun updateOpenFeedType(feedType: PostFiltersHolder.CurrentOpenTypeFeed)

    val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
}