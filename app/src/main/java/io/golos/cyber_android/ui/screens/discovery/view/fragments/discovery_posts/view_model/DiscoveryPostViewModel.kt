package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.view_model

import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.model.DiscoveryPostsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class DiscoveryPostViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    discoveryPostsModel: DiscoveryPostsModel
) : ViewModelBase<DiscoveryPostsModel>(
    dispatchersProvider,
    discoveryPostsModel
)