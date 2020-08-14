package io.golos.cyber_android.ui.screens.discovery.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.discovery.model.DiscoveryModel
import io.golos.cyber_android.ui.screens.discovery.model.DiscoveryModelImpl
import io.golos.cyber_android.ui.screens.discovery.view_model.DiscoveryViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class DiscoveryFragmentModuleBinds {

    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(DiscoveryViewModel::class)
    abstract fun providesDiscoveryViewModel(viewModel: DiscoveryViewModel): ViewModel

    @Binds
    abstract fun providesDiscoveryModel(discoveryModelImpl: DiscoveryModelImpl): DiscoveryModel
}
