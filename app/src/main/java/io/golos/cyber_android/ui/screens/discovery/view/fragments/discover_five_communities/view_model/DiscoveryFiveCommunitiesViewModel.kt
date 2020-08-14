package io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.view_model

import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.model.DiscoveryFiveCommunitiesModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class DiscoveryFiveCommunitiesViewModel
@Inject constructor(dispatchersProvider: DispatchersProvider, discoveryFiveCommunitiesModel: DiscoveryFiveCommunitiesModel) : ViewModelBase<DiscoveryFiveCommunitiesModel>(dispatchersProvider, discoveryFiveCommunitiesModel)