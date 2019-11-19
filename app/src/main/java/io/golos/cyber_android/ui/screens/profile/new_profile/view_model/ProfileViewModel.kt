package io.golos.cyber_android.ui.screens.profile.new_profile.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.profile.new_profile.model.ProfileModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class ProfileViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: ProfileModel
) : ViewModelBase<ProfileModel>(dispatchersProvider, model) {

}