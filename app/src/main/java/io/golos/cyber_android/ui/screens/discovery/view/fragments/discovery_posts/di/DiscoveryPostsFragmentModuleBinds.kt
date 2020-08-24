package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.model.DiscoveryPostsModel
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.model.DiscoveryPostsModelImpl
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.view_model.DiscoveryPostViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class DiscoveryPostsFragmentModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(DiscoveryPostViewModel::class)
    abstract fun providesDiscoveryViewModel(viewModel: DiscoveryPostViewModel): ViewModel

    @Binds
    abstract fun providesDiscoveryModel(discoveryPostsModelImpl: DiscoveryPostsModelImpl): DiscoveryPostsModel
}
