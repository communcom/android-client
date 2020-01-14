package io.golos.cyber_android.ui.screens.post_filters

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.utils.toLiveData
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

    private val _filtersMode = MutableLiveData<FiltersMode>()


    val updateTimeFilter = _updateTimeFilter.toLiveData()

    val periodTimeFilter = _periodTimeFilter.toLiveData()

    val filtersMode = _filtersMode.toLiveData()

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
        _filtersMode.value = if(filter == PostFiltersHolder.UpdateTimeFilter.POPULAR){
            FiltersMode.PERIOD_TIME
        } else{
            FiltersMode.UPDATE_TIME
        }
    }

    fun onDeselectUpdateTimeFilter(filter: PostFiltersHolder.UpdateTimeFilter){
        if(_updateTimeFilter.value == filter){
            //deselect current filter
            changeUpdateTimeFilter(filter)
        }
    }

    fun changePeriodTimeFilter(filter: PostFiltersHolder.PeriodTimeFilter) {
        _periodTimeFilter.value = filter
    }

    fun onDeselectPeriodTimeFilter(filter: PostFiltersHolder.PeriodTimeFilter){
        if(_periodTimeFilter.value == filter){
            //deselect current filter
            changePeriodTimeFilter(filter)
        }
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

    fun onCloseClicked() {
        _command.value = NavigateBackwardCommand()
    }

    fun onBackClicked() {
        _filtersMode.value = FiltersMode.UPDATE_TIME
    }

    enum class FiltersMode{
        UPDATE_TIME,
        PERIOD_TIME
    }
}