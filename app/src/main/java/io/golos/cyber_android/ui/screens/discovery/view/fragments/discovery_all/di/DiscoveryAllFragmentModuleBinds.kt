package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.model.DiscoveryAllModel
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.model.DiscoveryAllModelImpl
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view_model.DiscoveryAllViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class DiscoveryAllFragmentModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(DiscoveryAllViewModel::class)
    abstract fun providesDiscoveryViewModel(viewModel: DiscoveryAllViewModel): ViewModel

    @Binds
    abstract fun providesDiscoveryModel(discoveryAllModelImpl: DiscoveryAllModelImpl): DiscoveryAllModel
}
