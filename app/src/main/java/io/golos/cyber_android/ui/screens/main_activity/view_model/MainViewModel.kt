package io.golos.cyber_android.ui.screens.main_activity.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.main_activity.model.MainModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(dispatchersProvider: DispatchersProvider, mainModel: MainModel) :
    ViewModelBase<MainModel>(dispatchersProvider, mainModel) {

}