package io.golos.cyber_android.ui.screens.wallet_point.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_point.model.WalletPointModel
import io.golos.cyber_android.ui.screens.wallet_shared.history.view.WalletHistoryListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.view.WalletSendPointsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class WalletPointViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: WalletPointModel
) : ViewModelBase<WalletPointModel>(dispatchersProvider, model),
    WalletSendPointsListItemEventsProcessor,
    WalletHistoryListItemEventsProcessor {

    private val _balanceInPoints = MutableLiveData<Double>(0.0)
    val balanceInPoints: LiveData<Double> = _balanceInPoints

    private val _availablePoints = MutableLiveData<Double>(0.0)
    val availablePoints: LiveData<Double> = _availablePoints

    private val _holdPoints = MutableLiveData<Double>(0.0)
    val holdPoints: LiveData<Double> = _holdPoints

    private val _availableHoldFactor = MutableLiveData<Double>(0.0)
    val availableHoldFactor: LiveData<Double> = _availableHoldFactor

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _balanceInCommuns = MutableLiveData<Double>(0.0)
    val balanceInCommuns: LiveData<Double> = _balanceInCommuns

    private val _carouselStartData = MutableLiveData<CarouselStartData>()
    val carouselStartData: LiveData<CarouselStartData> = _carouselStartData

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    val sendPointItems: LiveData<List<VersionedListItem>> = model.sendPointItems

    val historyItems: LiveData<List<VersionedListItem>> = model.historyItems

    val pageSize = model.pageSize

    private var loadPageJob: Job? = null

    init {
        loadPage(false)
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onCommunitySelected(communityId: String) {
        if(model.switchBalanceRecord(communityId)) {
            updateHeaders(false)
        }
    }

    fun onSwipeRefresh() = loadPage(true)

    override fun onSendPointsNextPageReached() {
        launch {
            model.loadSendPointsPage()
        }
    }

    override fun onSendPointsRetryClick() {
        launch {
            model.retrySendPointsPage()
        }
    }

    override fun onHistoryNextPageReached() {
        launch {
            model.loadHistoryPage()
        }
    }

    override fun onHistoryRetryClick() {
        launch {
            model.retryHistoryPage()
        }
    }

    private fun loadPage(needReload: Boolean) {
        loadPageJob?.cancel()
        loadPageJob = launch {
            try {
//                if(needReload) {
//                    model.clearSendPoints()
//                    model.clearHistory()
//                }
//
                model.initBalance(needReload)
                updateHeaders(true)

                // To load the very first page
                onSendPointsNextPageReached()
                onHistoryNextPageReached()
            } catch (ex: Exception) {
                Timber.e(ex)

                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateBackwardCommand()
            } finally {
                if(needReload) {
                    _swipeRefreshing.value = false
                }
            }
        }
    }

    private fun updateHeaders(initCarousel: Boolean) {
        _balanceInPoints.value = model.balanceInPoints
        _availablePoints.value = model.balanceInPoints - model.holdPoints
        _holdPoints.value = model.holdPoints
        _availableHoldFactor.value = _holdPoints.value!! / _availablePoints.value!!
        _title.value = model.title
        _balanceInCommuns.value = model.balanceInCommuns

        if(initCarousel) {
            _carouselStartData.value = model.getCarouselStartData()
        }
    }
}