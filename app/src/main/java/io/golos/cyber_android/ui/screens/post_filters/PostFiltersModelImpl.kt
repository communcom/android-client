package io.golos.cyber_android.ui.screens.post_filters

import javax.inject.Inject

class PostFiltersModelImpl @Inject constructor(private val postFilters: PostFilters): PostFiltersModel {

    override var updateTimeFilter: PostFiltersViewModel.UpdateTimeFilter
        get() = postFilters.updateTimeFilter
        set(value) {
            postFilters.updateTimeFilter = value
        }
    override var periodTimeFilter: PostFiltersViewModel.PeriodTimeFilter
        get() = postFilters.periodTimeFilter
        set(value) {
            postFilters.periodTimeFilter = value
        }
}