package io.golos.cyber_android.ui.screens.post_filters

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class PostFiltersViewModel @Inject constructor(dispatchersProvider: DispatchersProvider,
                                               model: PostFiltersModel): ViewModelBase<PostFiltersModel>(dispatchersProvider, model) {
}