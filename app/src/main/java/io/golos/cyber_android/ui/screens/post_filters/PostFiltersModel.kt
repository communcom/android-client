package io.golos.cyber_android.ui.screens.post_filters

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase

interface PostFiltersModel: ModelBase {

    var updateTimeFilter: PostFiltersViewModel.UpdateTimeFilter

    var periodTimeFilter: PostFiltersViewModel.PeriodTimeFilter
}