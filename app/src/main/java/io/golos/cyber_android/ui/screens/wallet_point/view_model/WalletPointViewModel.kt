package io.golos.cyber_android.ui.screens.wallet_point.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_point.model.WalletPointModel
import io.golos.cyber_android.ui.screens.wallet_shared.history.view.WalletHistoryListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.view.WalletSendPointsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    val sendPointItems: LiveData<List<VersionedListItem>> = model.sendPointItems

    val historyItems: LiveData<List<VersionedListItem>> = model.historyItems

    val pageSize = model.pageSize

    private var loadPageJob: Job? = null

    init {
        loadPage(false)
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
//                model.initBalance(needReload)
//
//                _totalValue.value = model.totalBalance
//                _myPointsItems.value = model.getMyPointsItems()

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
}