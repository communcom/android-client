package io.golos.cyber_android.ui.screens.post_filters

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ApplyPostFiltersCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class PostFiltersViewModel @Inject constructor(dispatchersProvider: DispatchersProvider,
                                               model: PostFiltersModel): ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {

    private val updateTimeFilterMutableLiveData = MutableLiveData<UpdateTimeFilter>(model.updateTimeFilter)

    val updateTimeFilterLiveData = updateTimeFilterMutableLiveData.toLiveData()

    private val periodTimeFilterMutableLiveData = MutableLiveData<PeriodTimeFilter>(model.periodTimeFilter)

    val periodTimeFilterLiveData = periodTimeFilterMutableLiveData.toLiveData()

    fun changeUpdateTimeFilter(filter: UpdateTimeFilter) {
        updateTimeFilterMutableLiveData.value = filter
    }

    fun changePeriodTimeFilter(filter: PeriodTimeFilter){
        periodTimeFilterMutableLiveData.value = filter
    }

    fun onNextClicked(){
        val currentUpdateTimeFilter: UpdateTimeFilter = updateTimeFilterMutableLiveData.value!!
        model.updateTimeFilter = currentUpdateTimeFilter
        val currentPeriodTimeFilter: PeriodTimeFilter = periodTimeFilterMutableLiveData.value!!
        model.periodTimeFilter = currentPeriodTimeFilter
        val postFilters = PostFilters()
        postFilters.updateTimeFilter = currentUpdateTimeFilter
        postFilters.periodTimeFilter = currentPeriodTimeFilter
        commandMutableLiveData.value = ApplyPostFiltersCommand(postFilters)
    }

    fun onClosedClicked() {
        commandMutableLiveData.value = BackCommand()
    }

    enum class UpdateTimeFilter{
        TOP,
        NEW,
        OLD
    }

    enum class PeriodTimeFilter{
        PAST_24_HOURS,
        PAST_MONTH,
        PAST_YEAR
    }
}