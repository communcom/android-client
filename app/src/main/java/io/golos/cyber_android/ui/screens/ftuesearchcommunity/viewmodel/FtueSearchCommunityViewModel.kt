package io.golos.cyber_android.ui.screens.ftuesearchcommunity.viewmodel

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.model.FtueSearchCommunityModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class FtueSearchCommunityViewModel @Inject constructor(dispatchersProvider: DispatchersProvider,
                                                       model: FtueSearchCommunityModel): ViewModelBase<FtueSearchCommunityModel>(dispatchersProvider, model) {
}