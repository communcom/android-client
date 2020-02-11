package io.golos.cyber_android.ui.screens.wallet_point.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.wallet_point.model.WalletPointModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class WalletPointViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: WalletPointModel
) : ViewModelBase<WalletPointModel>(dispatchersProvider, model) {

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

}