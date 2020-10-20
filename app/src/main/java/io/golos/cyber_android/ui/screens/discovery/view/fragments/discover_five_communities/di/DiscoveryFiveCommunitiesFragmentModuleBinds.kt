package io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModelImpl
import io.golos.cyber_android.ui.screens.communities_list.view_model.CommunitiesListViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class DiscoveryFiveCommunitiesFragmentModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(CommunitiesListViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: CommunitiesListViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunitiesListModelImpl): CommunitiesListModel
}