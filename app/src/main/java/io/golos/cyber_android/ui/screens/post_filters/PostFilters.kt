package io.golos.cyber_android.ui.screens.post_filters

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class PostFilters @Inject constructor() {

    var updateTimeFilter: PostFiltersViewModel.UpdateTimeFilter =
        PostFiltersViewModel.UpdateTimeFilter.POPULAR

    var periodTimeFilter: PostFiltersViewModel.PeriodTimeFilter =
        PostFiltersViewModel.PeriodTimeFilter.PAST_24_HOURS

    lateinit var flowFilter: Flow<PostFilters>

    fun update(flow: Flow<PostFilters>): Flow<PostFilters> {
        return flow {
            Timber.d("filter: flow")
            flow.collect {
                Timber.d("filter: collect $it")
                emit(it)
            }
        }
    }
}