package io.golos.cyber_android.ui.screens.post_filters

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class PostFiltersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostFiltersModel,
    @Named(Clarification.FILTER_GLOBAL) private val isNeedToSaveGlobalState: Boolean,
    @Named(Clarification.FILTER_TIME) private val timeFilter: PostFiltersHolder.UpdateTimeFilter?,
    @Named(Clarification.FILTER_PERIOD) private val periodFilter: PostFiltersHolder.PeriodTimeFilter?
) : ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {

    private val _updateTimeFilter = MutableLiveData<PostFiltersHolder.UpdateTimeFilter>()

    private val _periodTimeFilter = MutableLiveData<PostFiltersHolder.PeriodTimeFilter>()

    val updateTimeFilter = _updateTimeFilter.toLiveData()

    val periodTimeFilter = _periodTimeFilter.toLiveData()

    init {
        if (isNeedToSaveGlobalState) {
            launch {
                val feedFilters = model.feedFiltersFlow.first()
                _updateTimeFilter.value = feedFilters.updateTimeFilter
                _periodTimeFilter.value = feedFilters.periodTimeFilter
            }
        } else {
            _updateTimeFilter.value = timeFilter
            _periodTimeFilter.value = periodFilter
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
        if (isNeedToSaveGlobalState) {
            launch {
                model.updateFilters(
                    PostFiltersHolder.FeedFilters(currentUpdateTimeFilter, currentPeriodTimeFilter)
                )
                _command.value = NavigateBackwardCommand()
            }
        } else {
            _command.value = SendFilterActionCommand(currentUpdateTimeFilter, currentPeriodTimeFilter)
        }
    }

    fun onClosedClicked() {
        _command.value = NavigateBackwardCommand()
    }
}