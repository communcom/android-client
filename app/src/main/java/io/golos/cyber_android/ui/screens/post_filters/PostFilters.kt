package io.golos.cyber_android.ui.screens.post_filters

import javax.inject.Inject

class PostFilters @Inject constructor(){

    var updateTimeFilter: PostFiltersViewModel.UpdateTimeFilter = PostFiltersViewModel.UpdateTimeFilter.TOP

    var periodTimeFilter: PostFiltersViewModel.PeriodTimeFilter = PostFiltersViewModel.PeriodTimeFilter.PAST_24_HOURS
}