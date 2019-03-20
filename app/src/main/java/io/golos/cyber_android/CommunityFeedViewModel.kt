package io.golos.cyber_android

import io.golos.domain.interactors.feed.CommunityFeedUseCase
import android.util.Log
import io.golos.cyber_android.ui.screens.feed.AbstractFeedViewModel
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.model.CommunityFeedUpdateRequest


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */


class CommunityFeedViewModel(communityFeedUserCase: CommunityFeedUseCase) :
    AbstractFeedViewModel<CommunityFeedUpdateRequest>(communityFeedUserCase) {

    fun onSearch(query: String) {
        Log.i("CommunityFeedViewModel", "onQuery $query")
    }

    fun onFilter(sort: TimeFilter) {
        Log.i("CommunityFeedViewModel", "onFilter $sort")
    }

    fun onSort(sort: TrendingSort) {
        Log.i("CommunityFeedViewModel", "onSort $sort")
    }
}