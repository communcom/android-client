package io.golos.cyber_android.ui.screens.post_filters

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostFiltersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostFiltersModel
) : ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {

    private val _updateTimeFilter = MutableLiveData<PostFiltersHolder.UpdateTimeFilter>()

    private val _periodTimeFilter = MutableLiveData<PostFiltersHolder.PeriodTimeFilter>()

    val updateTimeFilter = _updateTimeFilter.toLiveData()

    val periodTimeFilter = _periodTimeFilter.toLiveData()

    init {
        launch {
            val feedFilters = model.feedFiltersFlow.first()
            _updateTimeFilter.value = feedFilters.updateTimeFilter
            _periodTimeFilter.value = feedFilters.periodTimeFilter
        }
    }

    fun changeUpdateTimeFilter(filter: PostFiltersHolder.UpdateTimeFilter) {
        _updateTimeFilter.value = filter
    }

    fun changePeriodTimeFilter(filter: PostFiltersHolder.PeriodTimeFilter) {
        _periodTimeFilter.value = filter
    }

    fun onNextClicked() {
        val currentUpdateTimeFilter: PostFiltersHolder.UpdateTimeFilter = _updateTimeFilter.value!!
        val currentPeriodTimeFilter: PostFiltersHolder.PeriodTimeFilter = _periodTimeFilter.value!!
        launch {
            model.updateFilters(PostFiltersHolder.FeedFilters(currentUpdateTimeFilter, currentPeriodTimeFilter))
            _command.value = BackCommand()
        }
    }

    fun onClosedClicked() {
        _command.value = BackCommand()
    }
}