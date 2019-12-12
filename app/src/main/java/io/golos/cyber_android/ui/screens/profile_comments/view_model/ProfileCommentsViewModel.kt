package io.golos.cyber_android.ui.screens.profile_comments.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class ProfileCommentsViewModel @Inject constructor(dispatchersProvider: DispatchersProvider,
                                                   model: ProfileCommentsModel): ViewModelBase<ProfileCommentsModel>(dispatchersProvider, model) {
}