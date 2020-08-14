package io.golos.cyber_android.ui.screens.discovery.view_model

import io.golos.cyber_android.ui.screens.discovery.model.DiscoveryModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class DiscoveryViewModel
@Inject
constructor(
    dispatcherProvider: DispatchersProvider,
    model:DiscoveryModel
) : ViewModelBase<DiscoveryModel>(dispatcherProvider,model){

}