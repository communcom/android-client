package io.golos.cyber_android.ui.screens.wallet.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.*
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.screens.wallet.view.my_points.WalletMyPointsListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.history.view.WalletHistoryListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view.WalletSendPointsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.NoDataListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserBriefDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class WalletViewModel
@Inject constructor(private val appContext: Context, dispatchersProvider: DispatchersProvider, model: WalletModel) : ViewModelBase<WalletModel>(dispatchersProvider, model), WalletMyPointsListItemEventsProcessor, WalletSendPointsListItemEventsProcessor, WalletHistoryListItemEventsProcessor {

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    private val _totalValue = MutableLiveData<Double>(0.0)
    val totalValue: LiveData<Double> = _totalValue

    private val _collapsedPanelTitle = MutableLiveData<String>(appContext.resources.getString(R.string.profile_wallet_title))
    val collapsedPanelTitle: LiveData<String> = _collapsedPanelTitle

    private val _myPointsItems = MutableLiveData<List<MyPointsListItem>>(listOf())
    val myPointsItems: LiveData<List<MyPointsListItem>> = _myPointsItems

    val sendPointItems: LiveData<List<VersionedListItem>> = model.sendPointItems

    private val _sendPointsVisibility = MutableLiveData<Int>(View.GONE)
    val sendPointsVisibility: LiveData<Int> = _sendPointsVisibility

    val historyItems: LiveData<List<VersionedListItem>> = model.historyItems

    val pageSize = model.pageSize

    private var loadPageJob: Job? = null

    init {
        sendPointItems.observeForever {
            _sendPointsVisibility.value = if (isSendPointsListEmpty(it)) View.GONE else View.VISIBLE
        }

        loadPage(false)

        launch {
            model.isBalanceUpdated.collect {
                it?.let {
                    loadPage(true)
                    model.clearBalanceUpdateLastCallback()
    }
            }
        }
    }

    fun onSwipeRefresh() = loadPage(true)

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onSettingsClick() {
        _command.value = ShowSettingsDialog(model.getEmptyBalanceVisibility())
    }

    fun onSeeAllMyPointsClick() {
        val balancesList = if(model.getEmptyBalanceVisibility())
            model.balance.filter { it.points > 0 }.sortedByDescending { it.points }
        else
            model.balance.sortedByDescending { it.points }
        _command.value = ShowMyPointsDialog(balancesList)
    }

    fun onSeeAllSendPointsClick() {
        _command.value = ShowSendPointsDialog()
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

    override fun onMyPointItemClick(communityId: CommunityIdDomain) {
        if (communityId.code == GlobalConstants.COMMUN_CODE) {
            return
        }

        _command.value = NavigateToWalletPoint(communityId, model.balance)
    }

    override fun onSendPointsItemClick(user: UserBriefDomain?) {
        model.balance.let {
            _command.value = NavigateToWalletSendPoints(it.first().communityId, user, it)
        }
    }

    fun onEmptyBalancesShowHide(isVisible: Boolean) {
        launch {
            model.toggleShowHideEmptyBalances(isVisible)
            setupPointsItems()
        }
    }

    fun onConvertClick() {
        model.balance.let { balance ->
            _command.value =
                NavigateToWalletConvertCommand(balance.first { it.communityId.code != GlobalConstants.COMMUN_CODE }.communityId, balance)
        }
    }

    private fun loadPage(needReload: Boolean) {
        loadPageJob?.cancel()
        loadPageJob = launch {
            try {
                if (needReload) {
                    model.clearSendPoints()
                    model.clearHistory()
                }

                model.initBalance(needReload)

                _totalValue.value = model.totalBalance
                setupPointsItems()

                // To load the very first page
                onSendPointsNextPageReached()
                onHistoryNextPageReached()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))

                _command.value = NavigateBackwardCommand()
            } finally {
                if (needReload) {
                    _swipeRefreshing.value = false
                }
            }
        }
    }

    private suspend fun setupPointsItems() {
        _myPointsItems.value = if(model.getEmptyBalanceVisibility())
            model.getMyPointsItems().filter { it.data.points > 0 }.sortedByDescending { it.data.points }
        else
            model.getMyPointsItems().sortedByDescending { it.data.points }
    }

    private fun isSendPointsListEmpty(items: List<VersionedListItem>) =
        items.isEmpty() || (items.size == 1 && (items[0] is NoDataListItem || items[0] is LoadingListItem))

}