package io.golos.cyber_android.ui.screens.notifications.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModelImpl
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface NotificationsFragmentModuleBinds {

    @Binds
    @ViewModelKey(NotificationsViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: NotificationsViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: NotificationsModelImpl): NotificationsModel
}