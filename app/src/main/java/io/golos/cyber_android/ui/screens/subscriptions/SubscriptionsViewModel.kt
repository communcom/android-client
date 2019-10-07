package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(dispatchersProvider: DispatchersProvider, model: SubscriptionsModel): ViewModelBase<SubscriptionsModel>(dispatchersProvider, model) {
}