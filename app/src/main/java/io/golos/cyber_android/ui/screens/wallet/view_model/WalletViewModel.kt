package io.golos.cyber_android.ui.screens.wallet.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class WalletViewModel
@Inject
constructor(
    @Named(Clarification.TOTAL_COMMUN)
    totalCommun: Double,
    dispatchersProvider: DispatchersProvider,
    model: WalletModel
) : ViewModelBase<WalletModel>(dispatchersProvider, model) {

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    private val _totalValue = MutableLiveData<Double>(totalCommun)
    val totalValue: LiveData<Double> = _totalValue

    fun onSwipeRefresh() {
        launch {
            delay(1000)
            _swipeRefreshing.value = false
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }
}