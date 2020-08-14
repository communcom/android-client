package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModelImpl
import io.golos.cyber_android.ui.screens.communities_list.view_model.CommunitiesListViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class DiscoveryCommunitiesFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunitiesListViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: CommunitiesListViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunitiesListModelImpl): CommunitiesListModel
}
