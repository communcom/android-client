package io.golos.cyber_android.ui.screens.profile_communities.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.profile_communities.model.ProfileCommunitiesModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class ProfileCommunitiesViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileCommunitiesModel
) : ViewModelBase<ProfileCommunitiesModel>(dispatchersProvider, model) {
}
