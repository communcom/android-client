package io.golos.cyber_android.ui.screens.community_page_about

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class CommunityPageAboutViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageAboutModel
) : ViewModelBase<CommunityPageAboutModel>(dispatchersProvider, model) {
}