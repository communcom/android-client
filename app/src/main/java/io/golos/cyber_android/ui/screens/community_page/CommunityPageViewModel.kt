package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class CommunityPageViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPageModel): ViewModelBase<CommunityPageModel>(dispatchersProvider, model) {

    fun start(){

    }
}