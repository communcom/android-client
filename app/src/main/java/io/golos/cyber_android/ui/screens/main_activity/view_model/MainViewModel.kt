package io.golos.cyber_android.ui.screens.main_activity.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.main_activity.model.MainModel
import io.golos.cyber_android.ui.screens.main_activity.view.viewCommand.ContentPage
import io.golos.cyber_android.ui.screens.main_activity.view.viewCommand.NavigateToContentCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(dispatchersProvider: DispatchersProvider, mainModel: MainModel) :
    ViewModelBase<MainModel>(dispatchersProvider, mainModel) {

    init {
        launch {
            try {
                if(model.isNeedShowFtueBoard()){
                    _command.value = NavigateToContentCommand(ContentPage.FTUE)
                } else{
                    _command.value = NavigateToContentCommand(ContentPage.DASHBOARD)
                }
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }
}