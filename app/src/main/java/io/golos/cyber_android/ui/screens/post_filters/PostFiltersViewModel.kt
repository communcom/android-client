package io.golos.cyber_android.ui.screens.post_filters

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ApplyPostFiltersCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject

class PostFiltersViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: PostFiltersModel,
    private val filter: PostFilters
) : ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {

    init {
        Timber.d("filter: [$filter]")
    }

    private val updateTimeFilterMutableLiveData = MutableLiveData<UpdateTimeFilter>(model.updateTimeFilter)

    val updateTimeFilterLiveData = updateTimeFilterMutableLiveData.toLiveData()

    private val periodTimeFilterMutableLiveData = MutableLiveData<PeriodTimeFilter>(model.periodTimeFilter)

    val periodTimeFilterLiveData = periodTimeFilterMutableLiveData.toLiveData()

    fun changeUpdateTimeFilter(filter: UpdateTimeFilter) {
        updateTimeFilterMutableLiveData.value = filter
    }

    fun changePeriodTimeFilter(filter: PeriodTimeFilter) {
        periodTimeFilterMutableLiveData.value = filter
    }

    fun onNextClicked() {
        val currentUpdateTimeFilter: UpdateTimeFilter = updateTimeFilterMutableLiveData.value!!
        model.updateTimeFilter = currentUpdateTimeFilter
        val currentPeriodTimeFilter: PeriodTimeFilter = periodTimeFilterMutableLiveData.value!!
        model.periodTimeFilter = currentPeriodTimeFilter
        val postFilter = PostFilters().apply {
            updateTimeFilter = currentUpdateTimeFilter
            periodTimeFilter = currentPeriodTimeFilter
        }

        //todo look at the flowFilter (not updated)
        filter.flowFilter = flowOf(postFilter)
        filter.update(filter.flowFilter)

        _command.value = ApplyPostFiltersCommand(postFilter)
    }

    fun onClosedClicked() {
        _command.value = BackCommand()
    }

    enum class UpdateTimeFilter {
        HOT, //TOP_REWARDS
        NEW, //NEW
        POPULAR //TOP_LIKES
    }

    enum class PeriodTimeFilter {
        PAST_24_HOURS, //DAY
        PAST_WEEK, //WEEK
        PAST_MONTH, //MONTH
        ALL //ALL
    }
}