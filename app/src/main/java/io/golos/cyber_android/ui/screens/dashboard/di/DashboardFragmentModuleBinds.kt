package io.golos.cyber_android.ui.screens.dashboard.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.dashboard.model.DashboardModel
import io.golos.cyber_android.ui.screens.dashboard.model.DashboardModelImpl
import io.golos.cyber_android.ui.screens.dashboard.view_model.DashboardViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface DashboardFragmentModuleBinds {

    @Binds
    @ViewModelKey(DashboardViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @FragmentScope
    fun bindModel(model: DashboardModelImpl): DashboardModel

}