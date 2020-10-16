package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view_model

import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.model.DiscoveryAllModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class DiscoveryAllViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discoveryAllModel: DiscoveryAllModel
) : ViewModelBase<DiscoveryAllModel>(
    dispatchersProvider,
    discoveryAllModel
)