package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view_model

import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.model.DiscoveryCommunitiesModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class DiscoveryCommunitiesViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discoveryCommunitiesModel: DiscoveryCommunitiesModel
) : ViewModelBase<DiscoveryCommunitiesModel>(
    dispatchersProvider,
    discoveryCommunitiesModel
)