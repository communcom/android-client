package io.golos.cyber_android.ui.screens.wallet.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.screens.wallet.view.my_points.WalletMyPointsListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet.view.send_points.WalletSendPointsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class WalletViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: WalletModel
) : ViewModelBase<WalletModel>(dispatchersProvider, model),
    WalletMyPointsListItemEventsProcessor,
    WalletSendPointsListItemEventsProcessor {

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    private val _totalValue = MutableLiveData<Double>(0.0)
    val totalValue: LiveData<Double> = _totalValue

    private val _myPointsItems = MutableLiveData<List<MyPointsListItem>>(listOf())
    val myPointsItems: LiveData<List<MyPointsListItem>> = _myPointsItems

    val sendPointItems: LiveData<List<VersionedListItem>> = model.sendPointItems

    val pageSize = model.pageSize

    init {
        loadPage(false)
    }

    fun onSwipeRefresh() {
        launch {
            delay(1000)
            _swipeRefreshing.value = false

            // loadPage(true)
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

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

    private fun loadPage(needReload: Boolean) {
        // use a Job as result here in case of refresh
        launch {
            try {
                model.initBalance(needReload)

                _totalValue.value = model.totalBalance
                _myPointsItems.value = model.getMyPointsItems()

                onSendPointsNextPageReached()       // To load the very first page
            } catch (ex: Exception) {
                Timber.e(ex)

                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateBackwardCommand()
            }
        }
    }
}