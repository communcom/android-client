package io.golos.cyber_android.ui.screens.feed

import io.golos.cyber_android.ui.common.posts.AbstractFeedViewModel
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest

open class FeedPageTabViewModel<T : PostFeedUpdateRequest>(feedUseCase: AbstractFeedUseCase<T>) :
    AbstractFeedViewModel<T>(feedUseCase) {

    fun onSearch(query: String) {

    }

    fun onSort(sort: TrendingSort) {

    }

    fun onFilter(filter: TimeFilter) {

    }
}